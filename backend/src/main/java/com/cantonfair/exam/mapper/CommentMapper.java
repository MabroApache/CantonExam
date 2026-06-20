package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper {
    
    Comment selectById(Long id);
    
    List<Comment> selectByShareId(Long shareId);
    
    int insert(Comment comment);
    
    int deleteById(Long id);
    
    int deleteByShareId(Long shareId);
}