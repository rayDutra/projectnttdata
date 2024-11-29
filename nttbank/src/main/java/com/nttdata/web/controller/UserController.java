package com.nttdata.web.controller;

import com.nttdata.application.mapper.UserMapper;
import com.nttdata.application.service.ExcelService;
import com.nttdata.application.impls.UserServiceImpl;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.entity.User;
import com.nttdata.dto.UserDTO;
import com.nttdata.infrastructure.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearer-key")
public class UserController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            List<User> users = excelService.parseExcelFile(file);
            for (User user : users) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.saveAll(users);

            return ResponseEntity.ok("Usuários cadastrados com sucesso!");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erro ao processar o arquivo: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO, UriComponentsBuilder uriBuilder) {
        userRepository.findByLogin(userDTO.getLogin())
            .ifPresent(user -> {
                throw new RuntimeException("O login já está em uso.");
            });

        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        var uri = uriBuilder.path("/api/users/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(uri).body(userMapper.toDTO(savedUser));
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> list(Pageable pageable) {
        Page<User> page = userServiceImpl.findAll(pageable);
        Page<UserDTO> dtoPage = page.map(userMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = userServiceImpl.findById(id);
        userMapper.updateEntityFromDTO(userDTO, user);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User user = userServiceImpl.findById(id);
        user.deactivate();
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserWithDetails(@PathVariable Long id) {
        User user = userServiceImpl.getUserWithDetails(id);
        UserDTO userDTO = userMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportUserTransactionsToExcel(@PathVariable Long id) {
        try {
            User user = userServiceImpl.findById(id);
            List<Transaction> transactions = user.getTransactions();
            ByteArrayOutputStream byteArrayOutputStream = excelService.generateExpenseAnalysisExcel(transactions);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=transactions_report.xlsx");

            return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar o relatório de transações.", e);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAllUsersWithDetailsToExcel() {
        try {
            List<User> users = userRepository.findAll();
            ByteArrayOutputStream excelReport = excelService.generateFullUserReport(users);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=users_report.xlsx");

            return new ResponseEntity<>(excelReport.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar o relatório de usuários.", e);
        }
    }

}



