package org.example.restaurantmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TableUpdateRequest extends TableAddRequest{

    @NotNull(message = "ID不能为空")
    private Long id;

}