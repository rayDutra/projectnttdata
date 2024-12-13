package com.nttdata.application.service.impls;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.User;
import com.nttdata.domain.enums.AccountType;
import com.nttdata.application.dto.AccountDTO;

import java.util.List;

public interface AccountServiceImpl {
    boolean existsByUserIdAndType(Long userId, AccountType type);
    Account save(User user, AccountDTO accountDTO);
    List<Account> findAll();
    Account findById(Long id);
    Account update(Long id, AccountDTO accountDTO);
    void deactivate(Long id);
    void delete(Long id);
}
