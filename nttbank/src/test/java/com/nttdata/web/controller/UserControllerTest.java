package com.nttdata.web.controller;

import com.nttdata.application.mapper.UserMapper;
import com.nttdata.application.service.ExcelService;
import com.nttdata.application.service.impls.UserServiceImpl;
import com.nttdata.domain.entity.User;
import com.nttdata.dto.UserDTO;
import com.nttdata.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Disabled
    @Test
    void testCreateUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("testuser");
        userDTO.setPassword("password123");

        User user = new User();
        user.setLogin("testuser");
        user.setPassword("encodedPassword");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.empty());
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content("{\"login\":\"testuser\",\"password\":\"password123\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.login").value("testuser"));
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

        when(userServiceImpl.findById(1L)).thenReturn(user);

        mockMvc.perform(delete("/api/users/1"))
            .andExpect(status().isNoContent());
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

    @Disabled
    @Test
    void testExportUserTransactionsToExcel() throws Exception {
        mockMvc.perform(get("/api/users/1/export"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=transactions_report.xlsx"));
    }

    @Disabled
    @Test
    void testExportAllUsersToExcel() throws Exception {
        User user1 = new User(1L, "User One", "user1@example.com", "user1", "password1", new Date());
        User user2 = new User(2L, "User Two", "user2@example.com", "user2", "password2", new Date());

        List<User> users = List.of(user1, user2);

        ByteArrayOutputStream excelReport = new ByteArrayOutputStream();
        when(excelService.generateFullUserReport(users)).thenReturn(excelReport);

        mockMvc.perform(get("/api/users/export"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=users_report.xlsx"));
    }

}
