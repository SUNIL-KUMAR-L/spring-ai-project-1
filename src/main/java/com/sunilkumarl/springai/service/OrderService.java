package com.sunilkumarl.springai.service;

import com.sunilkumarl.springai.model.Order;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OrderService {

    private final RestClient restClient;

    public OrderService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:3000")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public List<Order> findAll() {
        return restClient.get()
                .uri("/orders")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Order>>() {});
    }

    public List<Order> findOrdersByCustomerId(Integer customer_id) {
        return restClient.get()
                .uri("/orders?customer_id={customer_id}", customer_id)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Order>>() {});
    }
}
