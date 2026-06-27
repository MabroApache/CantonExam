package com.cantonfair.exam.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.cantonfair.exam.entity.Candidate;
import com.cantonfair.exam.exception.BusinessException;
import com.cantonfair.exam.mapper.CandidateMapper;
import com.cantonfair.exam.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考生Service
 */
@Service
public class CandidateService {

    @Autowired
    private CandidateMapper candidateMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 考生登录
     */
    public Map<String, Object> login(String username, String password) {
        Candidate candidate = candidateMapper.selectByUsername(username);
        if (candidate == null) {
            throw new BusinessException("用户名不存在");
        }

        String md5Password = DigestUtil.md5Hex(password);
        if (!candidate.getPassword().equals(md5Password)) {
            throw new BusinessException("密码错误");
        }

        String token = jwtUtil.createToken(candidate.getId(), candidate.getUsername(), "candidate");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("candidate", candidate);
        return result;
    }

    /**
     * 考生注册
     */
    public void register(Candidate candidate) {
        Candidate existCandidate = candidateMapper.selectByUsername(candidate.getUsername());
        if (existCandidate != null) {
            throw new BusinessException("用户名已存在");
        }

        candidate.setPassword(DigestUtil.md5Hex(candidate.getPassword()));
        candidateMapper.insert(candidate);
    }

    public Candidate getById(Long id) {
        return candidateMapper.selectById(id);
    }

    public List<Candidate> getAll() {
        return candidateMapper.selectAll();
    }

    public List<Candidate> getByCondition(Candidate candidate) {
        return candidateMapper.selectByCondition(candidate);
    }

    public void add(Candidate candidate) {
        Candidate existCandidate = candidateMapper.selectByUsername(candidate.getUsername());
        if (existCandidate != null) {
            throw new BusinessException("用户名已存在");
        }

        candidate.setPassword(DigestUtil.md5Hex(candidate.getPassword()));
        candidateMapper.insert(candidate);
    }

    public void update(Candidate candidate) {
        candidateMapper.update(candidate);
    }

    public void delete(Long id) {
        candidateMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        candidateMapper.deleteBatch(ids);
    }

    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Candidate candidate = candidateMapper.selectById(id);
        if (candidate == null) {
            throw new BusinessException("用户不存在");
        }

        String md5OldPassword = DigestUtil.md5Hex(oldPassword);
        if (!candidate.getPassword().equals(md5OldPassword)) {
            throw new BusinessException("原密码错误");
        }

        String md5NewPassword = DigestUtil.md5Hex(newPassword);
        candidateMapper.updatePassword(id, md5NewPassword);
    }
}