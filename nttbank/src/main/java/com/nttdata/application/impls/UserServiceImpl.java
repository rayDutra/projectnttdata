package com.nttdata.application.impls;

import com.nttdata.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserServiceImpl {

    public User findById(Long id);

    public User getUserWithDetails(Long id);

    Page<User> findAll(Pageable pageable);

}
