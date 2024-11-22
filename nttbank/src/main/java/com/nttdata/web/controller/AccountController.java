package com.nttdata.web.controller;

import com.nttdata.application.mapper.AccountMapper;
import com.nttdata.application.service.AccountService;
import com.nttdata.application.service.UserService;
import com.nttdata.dto.AccountDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@SecurityRequirement(name = "bearer-key")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        AccountMapper.setUserService(userService);

        var account = AccountMapper.toEntity(accountDTO);
        var savedAccount = accountService.save(account);
        var accountResponse = AccountMapper.toDTO(savedAccount);
        return ResponseEntity.ok(accountResponse);
    }
    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> accountsDTO = accountService.findAll().stream()
            .map(AccountMapper::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(accountsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        var account = accountService.findById(id);
        if (account != null) {
            return ResponseEntity.ok(AccountMapper.toDTO(account));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody AccountDTO accountDTO) {
        var account = accountService.update(id, accountDTO);
        return ResponseEntity.ok(AccountMapper.toDTO(account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateAccount(@PathVariable Long id) {
        accountService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
