package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成绩Mapper接口
 */
@Mapper
public interface ScoreMapper {
    
    Score selectById(Long id);
    
    List<Score> selectAll();
    
    List<Score> selectByCondition(Score score);
    
    List<Score> selectByExamId(Long examId);
    
    List<Score> selectByCandidateId(Long candidateId);
    
    Score selectByExamAndCandidate(@Param("examId") Long examId, @Param("candidateId") Long candidateId);
    
    int insert(Score score);
    
    int update(Score score);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
}