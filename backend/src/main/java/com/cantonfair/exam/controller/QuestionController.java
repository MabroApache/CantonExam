package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Question;
import com.cantonfair.exam.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/{id}")
    public Result<Question> getById(@PathVariable Long id) {
        Question question = questionService.getById(id);
        return Result.success(question);
    }

    @GetMapping("/list")
    public Result<List<Question>> getAll() {
        List<Question> questions = questionService.getAll();
        return Result.success(questions);
    }

    @GetMapping("/search")
    public Result<List<Question>> getByCondition(Question question) {
        List<Question> questions = questionService.getByCondition(question);
        return Result.success(questions);
    }

    @GetMapping("/type/{typeId}")
    public Result<List<Question>> getByTypeId(@PathVariable Long typeId) {
        List<Question> questions = questionService.getByTypeId(typeId);
        return Result.success(questions);
    }

    @GetMapping("/teacher/{teacherId}")
    public Result<List<Question>> getByTeacherId(@PathVariable Long teacherId) {
        List<Question> questions = questionService.getByTeacherId(teacherId);
        return Result.success(questions);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Question question) {
        questionService.add(question);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Question question) {
        questionService.update(question);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        questionService.delete(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        questionService.deleteBatch(ids);
        return Result.success();
    }
}