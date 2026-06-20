package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.QuestionType;
import com.cantonfair.exam.service.QuestionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题型Controller
 */
@RestController
@RequestMapping("/questionType")
public class QuestionTypeController {

    @Autowired
    private QuestionTypeService questionTypeService;

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<QuestionType> getById(@PathVariable Long id) {
        QuestionType questionType = questionTypeService.getById(id);
        return Result.success(questionType);
    }

    /**
     * 根据编码查询
     */
    @GetMapping("/code/{code}")
    public Result<QuestionType> getByCode(@PathVariable String code) {
        QuestionType questionType = questionTypeService.getByCode(code);
        return Result.success(questionType);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<QuestionType>> getAll() {
        List<QuestionType> questionTypes = questionTypeService.getAll();
        return Result.success(questionTypes);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<QuestionType>> getByCondition(QuestionType questionType) {
        List<QuestionType> questionTypes = questionTypeService.getByCondition(questionType);
        return Result.success(questionTypes);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody QuestionType questionType) {
        questionTypeService.add(questionType);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody QuestionType questionType) {
        questionTypeService.update(questionType);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        questionTypeService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        questionTypeService.deleteBatch(ids);
        return Result.success();
    }
}