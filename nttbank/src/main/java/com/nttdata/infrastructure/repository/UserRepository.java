package com.nttdata.infrastructure.repository;

import com.nttdata.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    @Query("SELECT u FROM User u " +
        "LEFT JOIN FETCH u.accounts a " +
        "LEFT JOIN FETCH a.transactions " +
        "WHERE u.id = :id")
    Optional<User> findByIdWithAccountsAndTransactions(@Param("id") Long id);
}

