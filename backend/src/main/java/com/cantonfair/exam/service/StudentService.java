package com.cantonfair.exam.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.cantonfair.exam.entity.Student;
import com.cantonfair.exam.exception.BusinessException;
import com.cantonfair.exam.mapper.StudentMapper;
import com.cantonfair.exam.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生Service
 */
@Service
public class StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 学生登录
     */
    public Map<String, Object> login(String username, String password) {
        Student student = studentMapper.selectByUsername(username);
        if (student == null) {
            throw new BusinessException("用户名不存在");
        }
        
        String md5Password = DigestUtil.md5Hex(password);
        if (!student.getPassword().equals(md5Password)) {
            throw new BusinessException("密码错误");
        }
        
        if (student.getStatus() == 0) {
            throw new BusinessException("账号未审核，请等待管理员审核");
        }
        
        if (student.getStatus() == 2) {
            throw new BusinessException("账号审核未通过，请联系管理员");
        }
        
        String token = jwtUtil.createToken(student.getId(), student.getUsername(), "student");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("student", student);
        return result;
    }

    /**
     * 学生注册
     */
    public void register(Student student) {
        Student existStudent = studentMapper.selectByUsername(student.getUsername());
        if (existStudent != null) {
            throw new BusinessException("用户名已存在");
        }
        
        student.setPassword(DigestUtil.md5Hex(student.getPassword()));
        student.setStatus(0); // 待审核
        studentMapper.insert(student);
    }

    public Student getById(Long id) {
        return studentMapper.selectById(id);
    }

    public List<Student> getAll() {
        return studentMapper.selectAll();
    }

    public List<Student> getByCondition(Student student) {
        return studentMapper.selectByCondition(student);
    }

    public void add(Student student) {
        Student existStudent = studentMapper.selectByUsername(student.getUsername());
        if (existStudent != null) {
            throw new BusinessException("用户名已存在");
        }
        
        student.setPassword(DigestUtil.md5Hex(student.getPassword()));
        studentMapper.insert(student);
    }

    public void update(Student student) {
        studentMapper.update(student);
    }

    public void delete(Long id) {
        studentMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        studentMapper.deleteBatch(ids);
    }

    /**
     * 审核学生
     */
    public void audit(Long id, Integer status) {
        studentMapper.updateStatus(id, status);
    }

    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("用户不存在");
        }
        
        String md5OldPassword = DigestUtil.md5Hex(oldPassword);
        if (!student.getPassword().equals(md5OldPassword)) {
            throw new BusinessException("原密码错误");
        }
        
        String md5NewPassword = DigestUtil.md5Hex(newPassword);
        studentMapper.updatePassword(id, md5NewPassword);
    }
}