package com.nttdata.web.controller;

import com.nttdata.application.service.MockApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
public class MockApiController {

    @Autowired
    private MockApiService mockApiService;

    @PostMapping("/mock")
    public Object createUser(@RequestBody Map<String, Object> user) {
        return mockApiService.createUser(user);
    }
    @GetMapping("/mock")
    public List<Object> getUsers() {
        return mockApiService.getUsers();
    }

    @GetMapping("/mock/{id}")
    public Object getUsersById(@PathVariable String id) {
        return mockApiService.getUsersById(id);
    }

    @PutMapping("/mock/{id}")
    public Object updateUser(@PathVariable String id, @RequestBody Map<String, Object> user) {
        return mockApiService.updateUser(id, user);
    }

    @DeleteMapping("/mock/{id}")
    public void deleteUser(@PathVariable String id) {
        mockApiService.deleteUser(id);
    }
}
