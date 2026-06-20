package com.cantonfair.exam.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.cantonfair.exam.entity.Teacher;
import com.cantonfair.exam.exception.BusinessException;
import com.cantonfair.exam.mapper.TeacherMapper;
import com.cantonfair.exam.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师Service
 */
@Service
public class TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 教师登录
     */
    public Map<String, Object> login(String username, String password) {
        Teacher teacher = teacherMapper.selectByUsername(username);
        if (teacher == null) {
            throw new BusinessException("用户名不存在");
        }
        
        String md5Password = DigestUtil.md5Hex(password);
        if (!teacher.getPassword().equals(md5Password)) {
            throw new BusinessException("密码错误");
        }
        
        if (teacher.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        String token = jwtUtil.createToken(teacher.getId(), teacher.getUsername(), "teacher");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("teacher", teacher);
        return result;
    }

    /**
     * 教师注册
     */
    public void register(Teacher teacher) {
        Teacher existTeacher = teacherMapper.selectByUsername(teacher.getUsername());
        if (existTeacher != null) {
            throw new BusinessException("用户名已存在");
        }
        
        teacher.setPassword(DigestUtil.md5Hex(teacher.getPassword()));
        teacher.setStatus(1);
        teacherMapper.insert(teacher);
    }

    public Teacher getById(Long id) {
        return teacherMapper.selectById(id);
    }

    public List<Teacher> getAll() {
        return teacherMapper.selectAll();
    }

    public List<Teacher> getByCondition(Teacher teacher) {
        return teacherMapper.selectByCondition(teacher);
    }

    public void add(Teacher teacher) {
        Teacher existTeacher = teacherMapper.selectByUsername(teacher.getUsername());
        if (existTeacher != null) {
            throw new BusinessException("用户名已存在");
        }
        
        teacher.setPassword(DigestUtil.md5Hex(teacher.getPassword()));
        teacher.setStatus(1);
        teacherMapper.insert(teacher);
    }

    public void update(Teacher teacher) {
        teacherMapper.update(teacher);
    }

    public void delete(Long id) {
        teacherMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        teacherMapper.deleteBatch(ids);
    }

    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new BusinessException("用户不存在");
        }
        
        String md5OldPassword = DigestUtil.md5Hex(oldPassword);
        if (!teacher.getPassword().equals(md5OldPassword)) {
            throw new BusinessException("原密码错误");
        }
        
        String md5NewPassword = DigestUtil.md5Hex(newPassword);
        teacherMapper.updatePassword(id, md5NewPassword);
    }
}