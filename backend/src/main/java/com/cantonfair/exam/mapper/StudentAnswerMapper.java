package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.StudentAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生答案Mapper接口
 */
@Mapper
public interface StudentAnswerMapper {
    
    StudentAnswer selectById(Long id);
    
    List<StudentAnswer> selectByRecordId(Long recordId);
    
    int insert(StudentAnswer studentAnswer);
    
    int insertBatch(@Param("list") List<StudentAnswer> list);
    
    int update(StudentAnswer studentAnswer);
    
    int deleteByRecordId(Long recordId);
}