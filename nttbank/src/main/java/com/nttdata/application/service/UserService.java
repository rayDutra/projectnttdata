package com.nttdata.application.service;

import com.nttdata.domain.entity.User;
import com.nttdata.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    public User getUserWithDetails(Long id) {
        return userRepository.findByIdWithAccountsAndTransactions(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
