package org.example.restaurantmanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.UserAddRequest;
import org.example.restaurantmanagement.dto.UserUpdateRequest;
import org.example.restaurantmanagement.entity.User;
import org.example.restaurantmanagement.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public List<User> list(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (username != null) {
            username = username.trim();
        }

        if (username != null && !username.isBlank()) {
            wrapper.like(User::getUsername, username);
        }

        return userMapper.selectList(wrapper);  }

    public Page<User> list(Integer pageNum,
                           Integer pageSize,
                           String username) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(User::getUsername, username.trim());
        }

        wrapper.orderByAsc(User::getId);

        Page<User> page = new Page<>(pageNum, pageSize);

        return userMapper.selectPage(page, wrapper);
    }

    /**
     * 登录验证
     */
    public User login(String username, String password) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();

        wrapper.eq("username", username)
                .eq("password", password);

        return userMapper.selectOne(wrapper);
    }

    public void add(UserAddRequest request){

        User exist = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
        );

        if(exist != null){
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();

        BeanUtils.copyProperties(request,user);

        userMapper.insert(user);

    }
    public void update(UserUpdateRequest request){

        System.out.println(request);

        User user = new User();

        BeanUtils.copyProperties(request, user);

        System.out.println(user);

        int rows = userMapper.updateById(user);

        System.out.println("影响行数：" + rows);
    }
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id){

        User user = userMapper.selectById(id);

        if(user == null){
            throw new RuntimeException("用户不存在");
        }

        if("admin".equals(user.getUsername())){
            throw new RuntimeException("系统管理员不能删除");
        }

        userMapper.deleteById(id);
    }
}