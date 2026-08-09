package org.example.restaurantmanagement.vo;

import lombok.Data;

import java.util.List;


@Data
public class PageResult<T> {


    private List<T> records;


    private Long total;


    private Integer pageNum;


    private Integer pageSize;


    public PageResult(
            List<T> records,
            Long total,
            Integer pageNum,
            Integer pageSize
    ){

        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;

    }

}