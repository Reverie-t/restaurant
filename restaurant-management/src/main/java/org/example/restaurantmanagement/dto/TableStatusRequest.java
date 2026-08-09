package org.example.restaurantmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TableStatusRequest {

    @NotNull
    private Long id;

    @NotNull
    private Integer status;

}