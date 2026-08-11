package org.example.restaurantmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.restaurantmanagement.entity.User;

@Data
@AllArgsConstructor
public class CustomerLoginResponse {

    private String token;

    private User user;
}