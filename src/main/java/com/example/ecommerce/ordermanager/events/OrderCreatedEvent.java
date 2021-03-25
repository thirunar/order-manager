package com.example.ecommerce.ordermanager.events;

import com.example.ecommerce.ordermanager.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class OrderCreatedEvent {

    private Integer id;

    @Singular
    private List<OrderItem> items;
}
