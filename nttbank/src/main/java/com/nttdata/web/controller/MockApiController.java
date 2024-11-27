package com.nttdata.web.controller;

import com.nttdata.application.service.MockApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class MockApiController {

    @Autowired
    private MockApiService mockApiService;

    @GetMapping("/mock")
    public List<Object> getUsers() {
        return mockApiService.getUsers();
    }
    @GetMapping("/mock/{id}")
    public Object getUsersById(@PathVariable String id) {
        return mockApiService.getUsersById(id);
    }
}
