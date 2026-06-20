package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统公告Mapper接口
 */
@Mapper
public interface NoticeMapper {
    
    Notice selectById(Long id);
    
    List<Notice> selectAll();
    
    List<Notice> selectByCondition(Notice notice);
    
    List<Notice> selectPublished();
    
    int insert(Notice notice);
    
    int update(Notice notice);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
}