package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.CandidateAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考生答案Mapper接口
 */
@Mapper
public interface CandidateAnswerMapper {
    
    CandidateAnswer selectById(Long id);
    
    List<CandidateAnswer> selectByRecordId(Long recordId);
    
    int insert(CandidateAnswer candidateAnswer);
    
    int insertBatch(@Param("list") List<CandidateAnswer> list);
    
    int update(CandidateAnswer candidateAnswer);
    
    int deleteByRecordId(Long recordId);
}