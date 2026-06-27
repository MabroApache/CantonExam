package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Candidate;
import com.cantonfair.exam.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 考生Controller
 */
@RestController
@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Candidate candidate) {
        Map<String, Object> result = candidateService.login(candidate.getUsername(), candidate.getPassword());
        return Result.success(result);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Candidate candidate) {
        candidateService.register(candidate);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Candidate> getById(@PathVariable Long id) {
        Candidate candidate = candidateService.getById(id);
        return Result.success(candidate);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Candidate>> getAll() {
        List<Candidate> candidates = candidateService.getAll();
        return Result.success(candidates);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Candidate>> getByCondition(Candidate candidate) {
        List<Candidate> candidates = candidateService.getByCondition(candidate);
        return Result.success(candidates);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Candidate candidate) {
        candidateService.add(candidate);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody Candidate candidate) {
        candidateService.update(candidate);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        candidateService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        candidateService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        Long id = Long.parseLong(params.get("id"));
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        candidateService.updatePassword(id, oldPassword, newPassword);
        return Result.success();
    }
}