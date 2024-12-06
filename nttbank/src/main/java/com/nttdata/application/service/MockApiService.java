package com.nttdata.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class MockApiService {

    @Value("${api.mock.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    public List<Object> getUsers() {
        Object[] users = restTemplate.getForObject(apiUrl, Object[].class);
        return List.of(users);
    }
    public Object createUser(Map<String, Object> user) {
        return restTemplate.postForObject(apiUrl, user, Object.class);
    }
    public Object getUsersById(String id) {
        String url = apiUrl + "/" + id;
        return restTemplate.getForObject(url, Object.class);
    }

    public Object updateUser(String id, Map<String, Object> user) {
        String url = apiUrl + "/" + id;
        restTemplate.put(url, user);
        return user;
    }

    public void deleteUser(String id) {
        String url = apiUrl + "/" + id;
        restTemplate.delete(url);
    }
}
