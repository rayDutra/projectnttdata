package com.nttdata.web.controller;

import com.nttdata.application.mapper.AccountMapper;
import com.nttdata.application.service.UserService;
import com.nttdata.application.service.impls.AccountServiceImpl;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.User;
import com.nttdata.dto.AccountDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@SecurityRequirement(name = "bearer-key")
public class AccountController {

    @Autowired
    private AccountServiceImpl accountServiceImpl;

    @Autowired
    private UserService userService;

    private final AccountMapper accountMapper;

    @Autowired
    public AccountController(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountDTO accountDTO) {
        try {
            User user = userService.findById(accountDTO.getUserId());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado");
            }

            boolean accountExists = accountServiceImpl.existsByUserIdAndType(user.getId(), accountDTO.getType());
            if (accountExists) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Já existe uma conta do tipo '" + accountDTO.getType() + "' para este usuário.");
            }

            Account savedAccount = accountServiceImpl.save(user, accountDTO);
            AccountDTO accountResponse = accountMapper.toDTO(savedAccount);
            return ResponseEntity.status(HttpStatus.CREATED).body(accountResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno ao criar a conta: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> accountsDTO = accountServiceImpl.findAll().stream()
            .map(accountMapper::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(accountsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        var account = accountServiceImpl.findById(id);
        if (account != null) {
            return ResponseEntity.ok(accountMapper.toDTO(account));
        } else {
            throw new EntityNotFoundException("Conta não encontrada para o ID: " + id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody AccountDTO accountDTO) {
        var account = accountServiceImpl.update(id, accountDTO);
        if (account == null) {
            throw new EntityNotFoundException("Conta não encontrada para o ID: " + id);
        }
        return ResponseEntity.ok(accountMapper.toDTO(account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        try {
            var account = accountServiceImpl.findById(id);
            if (account == null) {
                throw new EntityNotFoundException("Conta não encontrada para o ID: " + id);
            }
            accountServiceImpl.delete(id);
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro interno ao excluir a conta", e);
        }
    }
}
