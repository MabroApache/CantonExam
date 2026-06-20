package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Share;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 交流分享Mapper接口
 */
@Mapper
public interface ShareMapper {
    
    Share selectById(Long id);
    
    List<Share> selectAll();
    
    List<Share> selectByCondition(Share share);
    
    List<Share> selectByStudentId(Long studentId);
    
    int insert(Share share);
    
    int update(Share share);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
    
    int updateViewCount(Long id);
    
    int updateLikeCount(Long id);
}