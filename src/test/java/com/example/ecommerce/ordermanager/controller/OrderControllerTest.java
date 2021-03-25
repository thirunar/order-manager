package com.example.ecommerce.ordermanager.controller;

import com.example.ecommerce.ordermanager.entity.Order;
import com.example.ecommerce.ordermanager.entity.OrderItem;
import com.example.ecommerce.ordermanager.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void shouldCreateAnOrder() throws Exception {
        Order order = Order.builder().id(1).item(OrderItem.builder().productId(1).quantity(10).build()).build();
        when(orderService.createOrder(any())).thenReturn(order);

        mockMvc.perform(post("/order")
                .content(new ObjectMapper().writeValueAsString(Order.builder().item(OrderItem.builder().productId(1).quantity(10).build()).build())))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1,\"items\":[{\"id\":null,\"productId\":1,\"quantity\":10}]}"));

    }

    @Test
    void shouldGetAnOrder() throws Exception {
        Order order = Order.builder().id(1).item(OrderItem.builder().productId(1).quantity(10).build()).build();
        when(orderService.getOrder(anyInt())).thenReturn(order);

        mockMvc.perform(get("/order/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"items\":[{\"id\":null,\"productId\":1,\"quantity\":10}]}"));
    }
}