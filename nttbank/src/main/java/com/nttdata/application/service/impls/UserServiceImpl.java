package com.nttdata.application.service.impls;

import com.nttdata.application.dto.UserDTO;
import com.nttdata.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;


public interface UserServiceImpl {

    public User findById(Long id);

    public User getUserWithDetails(Long id);

    Page<User> findAll(Pageable pageable);

    public User createUser(UserDTO userDTO);
    public void validateEmail(String email);
}
