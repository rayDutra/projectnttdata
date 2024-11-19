package com.nttdata.infrastructure.repository;

import com.nttdata.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
