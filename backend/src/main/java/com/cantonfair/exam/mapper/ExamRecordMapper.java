package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.ExamRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试记录Mapper接口
 */
@Mapper
public interface ExamRecordMapper {
    
    ExamRecord selectById(Long id);
    
    List<ExamRecord> selectAll();
    
    List<ExamRecord> selectByCondition(ExamRecord examRecord);
    
    ExamRecord selectByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);
    
    int insert(ExamRecord examRecord);
    
    int update(ExamRecord examRecord);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
}