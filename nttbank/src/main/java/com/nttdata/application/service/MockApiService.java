package com.nttdata.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class MockApiService {

    private final String API_URL = "https://6747216f38c8741641d5869f.mockapi.io/api/v1/users";

    @Autowired
    private RestTemplate restTemplate;

    public List<Object> getUsers() {
        Object[] accounts = restTemplate.getForObject(API_URL, Object[].class);
        return List.of(accounts);
    }
    public Object getUsersById(String id) {
        String url = API_URL + "/" + id;
        return restTemplate.getForObject(url, Object.class);
    }
}
