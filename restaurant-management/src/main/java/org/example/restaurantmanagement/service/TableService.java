package org.example.restaurantmanagement.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.TableAddRequest;
import org.example.restaurantmanagement.dto.TableUpdateRequest;
import org.example.restaurantmanagement.entity.TableInfo;
import org.example.restaurantmanagement.mapper.TableInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableInfoMapper tableInfoMapper;

    public List<TableInfo> list(
            String tableNo,
            Integer status,
            String area){

        LambdaQueryWrapper<TableInfo> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.like(
                StringUtils.hasText(tableNo),
                TableInfo::getTableNo,
                tableNo);

        wrapper.eq(
                status != null,
                TableInfo::getStatus,
                status);

        wrapper.like(
                StringUtils.hasText(area),
                TableInfo::getArea,
                area);

        wrapper.orderByAsc(TableInfo::getSort);

        return tableInfoMapper.selectList(wrapper);

    }
    public void add(TableAddRequest request){

        TableInfo exist = tableInfoMapper.selectOne(
                new LambdaQueryWrapper<TableInfo>()
                        .eq(TableInfo::getTableNo, request.getTableNo())
        );

        if(exist != null){
            throw new RuntimeException("桌号已存在");
        }

        TableInfo tableInfo = new TableInfo();

        BeanUtils.copyProperties(request, tableInfo);

        tableInfoMapper.insert(tableInfo);

    }
    public void update(TableUpdateRequest request){

        TableInfo exist = tableInfoMapper.selectOne(

                new LambdaQueryWrapper<TableInfo>()

                        .eq(TableInfo::getTableNo, request.getTableNo())

                        .ne(TableInfo::getId, request.getId())

        );

        if(exist != null){

            throw new RuntimeException("桌号已存在");

        }

        TableInfo tableInfo = new TableInfo();

        BeanUtils.copyProperties(request, tableInfo);

        tableInfoMapper.updateById(tableInfo);

    }
    public void delete(Long id){

        TableInfo tableInfo = tableInfoMapper.selectById(id);

        if(tableInfo == null){

            throw new RuntimeException("餐桌不存在");

        }

        if(tableInfo.getStatus() == 1){

            throw new RuntimeException("餐桌正在使用，不能删除");

        }

        tableInfoMapper.deleteById(id);

    }
    public void updateStatus(Long id,Integer status){

        TableInfo tableInfo = new TableInfo();

        tableInfo.setId(id);

        tableInfo.setStatus(status);

        tableInfoMapper.updateById(tableInfo);

    }


}