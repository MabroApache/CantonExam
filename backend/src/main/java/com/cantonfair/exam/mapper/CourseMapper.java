package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程Mapper接口
 */
@Mapper
public interface CourseMapper {
    
    Course selectById(Long id);
    
    List<Course> selectAll();
    
    List<Course> selectByCondition(Course course);
    
    List<Course> selectByTeacherId(Long teacherId);
    
    int insert(Course course);
    
    int update(Course course);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
}