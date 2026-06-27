package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.QuestionCreator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 出题人Mapper接口
 */
@Mapper
public interface QuestionCreatorMapper {
    
    QuestionCreator selectById(Long id);
    
    QuestionCreator selectByUsername(String username);
    
    List<QuestionCreator> selectAll();
    
    List<QuestionCreator> selectByCondition(QuestionCreator questionCreator);
    
    int insert(QuestionCreator questionCreator);
    
    int update(QuestionCreator questionCreator);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
    
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}