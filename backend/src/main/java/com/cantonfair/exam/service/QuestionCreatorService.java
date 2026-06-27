package com.cantonfair.exam.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.cantonfair.exam.entity.QuestionCreator;
import com.cantonfair.exam.exception.BusinessException;
import com.cantonfair.exam.mapper.QuestionCreatorMapper;
import com.cantonfair.exam.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 出题人Service
 */
@Service
public class QuestionCreatorService {

    @Autowired
    private QuestionCreatorMapper questionCreatorMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 出题人登录
     */
    public Map<String, Object> login(String username, String password) {
        QuestionCreator questionCreator = questionCreatorMapper.selectByUsername(username);
        if (questionCreator == null) {
            throw new BusinessException("用户名不存在");
        }

        String md5Password = DigestUtil.md5Hex(password);
        if (!questionCreator.getPassword().equals(md5Password)) {
            throw new BusinessException("密码错误");
        }

        String token = jwtUtil.createToken(questionCreator.getId(), questionCreator.getUsername(), "creator", questionCreator.getDepartmentId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("creator", questionCreator);
        return result;
    }

    /**
     * 出题人注册
     */
    public void register(QuestionCreator questionCreator) {
        QuestionCreator existQuestionCreator = questionCreatorMapper.selectByUsername(questionCreator.getUsername());
        if (existQuestionCreator != null) {
            throw new BusinessException("用户名已存在");
        }

        questionCreator.setPassword(DigestUtil.md5Hex(questionCreator.getPassword()));
        questionCreatorMapper.insert(questionCreator);
    }

    public QuestionCreator getById(Long id) {
        return questionCreatorMapper.selectById(id);
    }

    public List<QuestionCreator> getAll() {
        return questionCreatorMapper.selectAll();
    }

    public List<QuestionCreator> getByCondition(QuestionCreator questionCreator) {
        return questionCreatorMapper.selectByCondition(questionCreator);
    }

    public void add(QuestionCreator questionCreator) {
        QuestionCreator existQuestionCreator = questionCreatorMapper.selectByUsername(questionCreator.getUsername());
        if (existQuestionCreator != null) {
            throw new BusinessException("用户名已存在");
        }

        questionCreator.setPassword(DigestUtil.md5Hex(questionCreator.getPassword()));
        questionCreatorMapper.insert(questionCreator);
    }

    public void update(QuestionCreator questionCreator) {
        questionCreatorMapper.update(questionCreator);
    }

    public void delete(Long id) {
        questionCreatorMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        questionCreatorMapper.deleteBatch(ids);
    }

    public void updatePassword(Long id, String oldPassword, String newPassword) {
        QuestionCreator questionCreator = questionCreatorMapper.selectById(id);
        if (questionCreator == null) {
            throw new BusinessException("用户不存在");
        }

        String md5OldPassword = DigestUtil.md5Hex(oldPassword);
        if (!questionCreator.getPassword().equals(md5OldPassword)) {
            throw new BusinessException("原密码错误");
        }

        String md5NewPassword = DigestUtil.md5Hex(newPassword);
        questionCreatorMapper.updatePassword(id, md5NewPassword);
    }
}