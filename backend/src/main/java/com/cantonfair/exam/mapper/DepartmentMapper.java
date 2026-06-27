package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface DepartmentMapper {
    
    Department selectById(Long id);
    
    List<Department> selectAll();
    
    List<Department> selectByCondition(Department department);
    
    int insert(Department department);
    
    int update(Department department);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
}