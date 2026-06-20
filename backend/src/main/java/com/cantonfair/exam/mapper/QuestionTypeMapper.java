package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.QuestionType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题型Mapper接口
 */
@Mapper
public interface QuestionTypeMapper {
    
    QuestionType selectById(Long id);
    
    QuestionType selectByCode(String code);
    
    List<QuestionType> selectAll();
    
    List<QuestionType> selectByCondition(QuestionType questionType);
    
    int insert(QuestionType questionType);
    
    int update(QuestionType questionType);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
}