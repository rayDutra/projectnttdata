package com.nttdata.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class MockApiService {

    @Value("${api.mock.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    public List<Object> getUsers() {
        Object[] accounts = restTemplate.getForObject(apiUrl, Object[].class);
        return List.of(accounts);
    }

    public Object getUsersById(String id) {
        String url = apiUrl + "/" + id;
        return restTemplate.getForObject(url, Object.class);
    }
}
