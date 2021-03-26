package com.example.ecommerce.ordermanager.controller;

import com.example.ecommerce.ordermanager.entity.Order;
import com.example.ecommerce.ordermanager.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping("/order")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Order createOrder(@RequestBody Order order) {
        return service.createOrder(order);
    }

    @GetMapping("/order/{id}")
    public Order getProduct(@PathVariable("id") int id) {
        return service.getOrder(id);
    }
}
