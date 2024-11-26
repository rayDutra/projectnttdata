package com.nttdata.web.controller;

import com.nttdata.application.mapper.UserMapper;
import com.nttdata.application.service.ExcelService;
import com.nttdata.application.service.UserService;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.entity.User;
import com.nttdata.dto.UserDTO;
import com.nttdata.infrastructure.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
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
    private UserService userService;

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
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO, UriComponentsBuilder uriBuilder) {
        User user = UserMapper.toEntity(userDTO);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);
        var uri = uriBuilder.path("/api/users/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(uri).body(UserMapper.toDTO(savedUser));
    }
    @GetMapping
    public ResponseEntity<Page<UserDTO>> list(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        Page<UserDTO> dtoPage = page.map(UserMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }
    @PutMapping
    @Transactional
    public ResponseEntity<UserDTO> update(@RequestBody UserDTO userDTO) {
        var user = userRepository.findById(userDTO.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        UserMapper.updateEntityFromDTO(userDTO, user);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.deactivate();
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<UserDTO> getUserWithDetails(@PathVariable Long id) {
        User user = userService.getUserWithDetails(id);
        UserDTO userDTO = UserMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }
    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportUserTransactionsToExcel(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

            List<Transaction> transactions = user.getTransactions();
            ByteArrayOutputStream byteArrayOutputStream = excelService.generateExpenseAnalysisExcel(transactions);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=transactions_report.xlsx");
            return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
