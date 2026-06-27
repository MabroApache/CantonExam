package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Paper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 试卷Mapper接口
 */
@Mapper
public interface PaperMapper {
    
    Paper selectById(Long id);
    
    List<Paper> selectAll();
    
    List<Paper> selectByCondition(Paper paper);
    
    List<Paper> selectByCreatorId(Long creatorId);
    
    List<Paper> selectByDepartmentId(Long departmentId);
    
    int insert(Paper paper);
    
    int update(Paper paper);
    
    int deleteById(Long id);
    
    int deleteBatch(@Param("ids") List<Long> ids);
}