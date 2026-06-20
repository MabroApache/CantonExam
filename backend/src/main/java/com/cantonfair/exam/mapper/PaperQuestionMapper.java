package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.PaperQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 试卷题目关联Mapper接口
 */
@Mapper
public interface PaperQuestionMapper {
    
    PaperQuestion selectById(Long id);
    
    List<PaperQuestion> selectByPaperId(Long paperId);
    
    List<PaperQuestion> selectByQuestionId(Long questionId);
    
    int insert(PaperQuestion paperQuestion);
    
    int insertBatch(@Param("list") List<PaperQuestion> list);
    
    int deleteByPaperId(Long paperId);
    
    int deleteById(Long id);
}