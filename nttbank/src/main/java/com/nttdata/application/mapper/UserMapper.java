package com.nttdata.application.mapper;

import com.nttdata.domain.entity.User;
import com.nttdata.dto.UserDTO;

public class UserMapper {

    public static User toEntity(UserDTO userDTO) {
        return new User(
            userDTO.getName(),
            userDTO.getEmail(),
            userDTO.getPassword(),
            userDTO.getDate()
        );
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getDate()
        );
    }
}
