package org.example.restaurantmanagement.vo;

import lombok.Data;

@Data
public class TableVO {

    private Long id;

    private String tableNo;

    private Integer capacity;

    private Integer status;

    private String area;

    private String remark;

    private Integer sort;

}