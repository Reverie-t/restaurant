package org.example.restaurantmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.restaurantmanagement.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}