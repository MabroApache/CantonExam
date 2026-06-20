package com.cantonfair.exam.mapper;

import com.cantonfair.exam.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管理员Mapper接口
 */
@Mapper
public interface AdminMapper {
    
    /**
     * 根据ID查询
     */
    Admin selectById(Long id);
    
    /**
     * 根据用户名查询
     */
    Admin selectByUsername(String username);
    
    /**
     * 查询所有
     */
    List<Admin> selectAll();
    
    /**
     * 条件查询
     */
    List<Admin> selectByCondition(Admin admin);
    
    /**
     * 新增
     */
    int insert(Admin admin);
    
    /**
     * 更新
     */
    int update(Admin admin);
    
    /**
     * 删除
     */
    int deleteById(Long id);
    
    /**
     * 批量删除
     */
    int deleteBatch(@Param("ids") List<Long> ids);
    
    /**
     * 修改密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}