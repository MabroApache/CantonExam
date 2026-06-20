package com.cantonfair.exam.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.cantonfair.exam.entity.Admin;
import com.cantonfair.exam.exception.BusinessException;
import com.cantonfair.exam.mapper.AdminMapper;
import com.cantonfair.exam.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员Service
 */
@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 管理员登录
     */
    public Map<String, Object> login(String username, String password) {
        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            throw new BusinessException("用户名不存在");
        }
        
        // 验证密码（MD5加密）
        String md5Password = DigestUtil.md5Hex(password);
        if (!admin.getPassword().equals(md5Password)) {
            throw new BusinessException("密码错误");
        }
        
        if (admin.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 生成Token
        String token = jwtUtil.createToken(admin.getId(), admin.getUsername(), "admin");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("admin", admin);
        return result;
    }

    /**
     * 根据ID查询
     */
    public Admin getById(Long id) {
        return adminMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Admin> getAll() {
        return adminMapper.selectAll();
    }

    /**
     * 条件查询
     */
    public List<Admin> getByCondition(Admin admin) {
        return adminMapper.selectByCondition(admin);
    }

    /**
     * 新增
     */
    public void add(Admin admin) {
        // 检查用户名是否存在
        Admin existAdmin = adminMapper.selectByUsername(admin.getUsername());
        if (existAdmin != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // MD5加密密码
        admin.setPassword(DigestUtil.md5Hex(admin.getPassword()));
        admin.setStatus(1);
        adminMapper.insert(admin);
    }

    /**
     * 更新
     */
    public void update(Admin admin) {
        adminMapper.update(admin);
    }

    /**
     * 删除
     */
    public void delete(Long id) {
        adminMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Long> ids) {
        adminMapper.deleteBatch(ids);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证原密码
        String md5OldPassword = DigestUtil.md5Hex(oldPassword);
        if (!admin.getPassword().equals(md5OldPassword)) {
            throw new BusinessException("原密码错误");
        }
        
        // 更新密码
        String md5NewPassword = DigestUtil.md5Hex(newPassword);
        adminMapper.updatePassword(id, md5NewPassword);
    }
}