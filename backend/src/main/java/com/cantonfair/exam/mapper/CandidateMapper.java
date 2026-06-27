package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Candidate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考生Mapper接口
 */
@Mapper
public interface CandidateMapper {
    
    Candidate selectById(Long id);
    
    Candidate selectByUsername(String username);
    
    List<Candidate> selectAll();
    
    List<Candidate> selectByCondition(Candidate candidate);
    
    int insert(Candidate candidate);
    
    int update(Candidate candidate);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
    
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}