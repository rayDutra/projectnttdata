package com.nttdata.web.controller;

import com.nttdata.application.mapper.AccountMapper;
import com.nttdata.application.service.AccountService;
import com.nttdata.application.service.UserService;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.User;
import com.nttdata.dto.AccountDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@SecurityRequirement(name = "bearer-key")
public class AccountController {

    @Autowired
    private AccountService accountService;

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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Usuário não encontrado"));
            }
            boolean accountExists = accountService.existsByUserIdAndType(user.getId(), accountDTO.getType());
            if (accountExists) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Já existe uma conta do tipo '" + accountDTO.getType() + "' para este usuário."));
            }
            Account savedAccount = accountService.save(user, accountDTO);
            AccountDTO accountResponse = accountMapper.toDTO(savedAccount);

            return ResponseEntity.ok(accountResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Erro: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erro interno do servidor"));
        }
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> accountsDTO = accountService.findAll().stream()
            .map(accountMapper::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(accountsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        var account = accountService.findById(id);
        if (account != null) {
            return ResponseEntity.ok(accountMapper.toDTO(account));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody AccountDTO accountDTO) {
        var account = accountService.update(id, accountDTO);
        return ResponseEntity.ok(accountMapper.toDTO(account));
    }
}
