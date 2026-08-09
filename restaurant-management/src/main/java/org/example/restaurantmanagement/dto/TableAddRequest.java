package org.example.restaurantmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TableAddRequest {

    @NotBlank(message = "桌号不能为空")
    private String tableNo;

    @Min(value = 1, message = "容纳人数必须大于0")
    private Integer capacity;

    private Integer status;

    private String area;

    private String remark;

    private Integer sort;
}