package com.nttdata.web.controller;

import com.nttdata.application.mapper.UserMapper;
import com.nttdata.domain.entity.User;
import com.nttdata.dto.UserDTO;
import com.nttdata.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

}
