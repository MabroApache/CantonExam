package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教师Mapper接口
 */
@Mapper
public interface TeacherMapper {
    
    Teacher selectById(Long id);
    
    Teacher selectByUsername(String username);
    
    List<Teacher> selectAll();
    
    List<Teacher> selectByCondition(Teacher teacher);
    
    int insert(Teacher teacher);
    
    int update(Teacher teacher);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
    
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}