package com.nttdata.web.controller;

import com.nttdata.infrastructure.mapper.UserMapper;
import com.nttdata.application.service.ExcelService;
import com.nttdata.application.service.impls.UserServiceImpl;
import com.nttdata.domain.entity.User;
import com.nttdata.application.dto.UserDTO;
import com.nttdata.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    private ExcelService excelService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }


    @Test
    void testUploadFile() throws Exception {
        mockMvc.perform(multipart("/api/users/upload")
                .file("file", "arquivo".getBytes()))
            .andExpect(status().isOk())
            .andExpect(content().string("Usuários cadastrados com sucesso!"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("updateduser");
        userDTO.setPassword("newpassword");

        User user = new User();
        user.setId(1L);
        user.setLogin("olduser");
        user.setPassword("oldpassword");

        when(userServiceImpl.findById(1L)).thenReturn(user);
        doNothing().when(userMapper).updateEntityFromDTO(userDTO, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        mockMvc.perform(put("/api/users/1")
                .contentType("application/json")
                .content("{\"login\":\"updateduser\",\"password\":\"newpassword\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login").value("updateduser"));
    }


    @Test
    void testDeleteUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setLogin("userToDelete");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setLogin(user.getLogin());

        when(userServiceImpl.findById(1L)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        mockMvc.perform(delete("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.login").value("userToDelete"));
    }

    @Test
    void testGetUserWithDetails() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setLogin("testuser");

        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("testuser");

        when(userServiceImpl.getUserWithDetails(1L)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login").value("testuser"));
    }


}
