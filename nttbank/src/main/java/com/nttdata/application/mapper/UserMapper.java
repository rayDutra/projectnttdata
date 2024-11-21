package com.nttdata.application.mapper;

import com.nttdata.domain.entity.User;
import com.nttdata.dto.UserDTO;

public class UserMapper {

    public static User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        return new User(
            userDTO.getId(),
            userDTO.getName(),
            userDTO.getEmail(),
            userDTO.getLogin(),
            userDTO.getPassword(),
            userDTO.getDate()
        );
    }

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getLogin(),
            user.getPassword(),
            user.getDate()
        );
    }
    public static void updateEntityFromDTO(UserDTO userDTO, User user) {
        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getLogin() != null) {
            user.setLogin(userDTO.getLogin());
        }
        if (userDTO.getPassword() != null) {
            user.setPassword(userDTO.getPassword());
        }
        if (userDTO.getDate() != null) {
            user.setDate(userDTO.getDate());
        }
    }

}
