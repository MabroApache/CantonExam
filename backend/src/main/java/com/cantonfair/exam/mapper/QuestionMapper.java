package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题库Mapper接口
 */
@Mapper
public interface QuestionMapper {
    
    Question selectById(Long id);
    
    List<Question> selectAll();
    
    List<Question> selectByCondition(Question question);
    
    List<Question> selectByTypeId(Long typeId);
    
    List<Question> selectByCreatorId(Long creatorId);
    
    List<Question> selectByDepartmentId(Long departmentId);
    
    List<Question> selectByTypeIdAndDepartmentId(@Param("typeId") Long typeId,
                                                  @Param("departmentId") Long departmentId);
    
    List<Question> selectRandomQuestions(@Param("typeId") Long typeId,
                                         @Param("count") Integer count);
    
    int insert(Question question);
    
    int update(Question question);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
}