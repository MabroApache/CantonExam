package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.QuestionCreator;
import com.cantonfair.exam.service.QuestionCreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 出题人Controller
 */
@RestController
@RequestMapping("/creator")
public class QuestionCreatorController {

    @Autowired
    private QuestionCreatorService questionCreatorService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody QuestionCreator questionCreator) {
        Map<String, Object> result = questionCreatorService.login(questionCreator.getUsername(), questionCreator.getPassword());
        return Result.success(result);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody QuestionCreator questionCreator) {
        questionCreatorService.register(questionCreator);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<QuestionCreator> getById(@PathVariable Long id) {
        QuestionCreator questionCreator = questionCreatorService.getById(id);
        return Result.success(questionCreator);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<QuestionCreator>> getAll() {
        List<QuestionCreator> questionCreators = questionCreatorService.getAll();
        return Result.success(questionCreators);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<QuestionCreator>> getByCondition(QuestionCreator questionCreator) {
        List<QuestionCreator> questionCreators = questionCreatorService.getByCondition(questionCreator);
        return Result.success(questionCreators);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody QuestionCreator questionCreator) {
        questionCreatorService.add(questionCreator);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody QuestionCreator questionCreator) {
        questionCreatorService.update(questionCreator);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        questionCreatorService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        questionCreatorService.deleteBatch(ids);
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
        questionCreatorService.updatePassword(id, oldPassword, newPassword);
        return Result.success();
    }
}