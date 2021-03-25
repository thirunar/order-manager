package com.example.ecommerce.ordermanager.repository;

import com.example.ecommerce.ordermanager.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
