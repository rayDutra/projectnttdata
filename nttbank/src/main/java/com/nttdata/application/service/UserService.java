package com.nttdata.application.service;

import com.nttdata.application.dto.UserDTO;
import com.nttdata.application.service.impls.UserServiceImpl;
import com.nttdata.domain.entity.User;
import com.nttdata.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService implements UserServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";


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

    public void validateEmail(String email) {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("O e-mail fornecido não é válido.");
        }
    }

    public User createUser(UserDTO userDTO) {
        validateEmail(userDTO.getEmail());

        userRepository.findByLogin(userDTO.getLogin())
            .ifPresent(user -> {
                throw new RuntimeException("O login já está em uso.");
            });

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setLogin(userDTO.getLogin());
        user.setPassword(userDTO.getPassword());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return userRepository.save(user);
    }

}
