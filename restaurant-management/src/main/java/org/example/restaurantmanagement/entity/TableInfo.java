package org.example.restaurantmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("table_info")
public class TableInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 桌号
    private String tableNo;

    // 可容纳人数
    private Integer capacity;

    // 0空闲 1用餐中 2已预订
    private Integer status;

    // 区域
    private String area;

    // 备注
    private String remark;

    // 排序
    private Integer sort;

}