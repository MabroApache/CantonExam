package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper {
    
    Student selectById(Long id);
    
    Student selectByUsername(String username);
    
    List<Student> selectAll();
    
    List<Student> selectByCondition(Student student);
    
    int insert(Student student);
    
    int update(Student student);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
    
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}