package com.nttdata.infrastructure.repository;


import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.User;
import com.nttdata.domain.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUserAndType(User user, AccountType accountType);
    boolean existsByUserIdAndType(Long userId, AccountType type);
}
