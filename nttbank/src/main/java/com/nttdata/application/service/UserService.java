package com.nttdata.application.service;

import com.nttdata.application.service.impls.UserServiceImpl;
import com.nttdata.domain.entity.User;
import com.nttdata.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User não encontrada para o ID: " + id));
    }
    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    @Override
    public User getUserWithDetails(Long id) {
        return userRepository.findByIdWithAccountsAndTransactions(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
