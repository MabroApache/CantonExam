package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试安排Mapper接口
 */
@Mapper
public interface ExamMapper {
    
    Exam selectById(Long id);
    
    List<Exam> selectAll();
    
    List<Exam> selectByCondition(Exam exam);
    
    List<Exam> selectByStudentId(Long studentId);
    
    int insert(Exam exam);
    
    int update(Exam exam);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
}