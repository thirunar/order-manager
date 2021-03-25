package com.example.ecommerce.ordermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;

@SpringBootApplication(exclude = SpringApplicationAdminJmxAutoConfiguration.class)
public class OrderManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderManagerApplication.class, args);
	}

}
