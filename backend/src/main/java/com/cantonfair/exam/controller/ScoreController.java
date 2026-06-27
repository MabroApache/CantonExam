package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Score;
import com.cantonfair.exam.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 成绩Controller
 */
@RestController
@RequestMapping("/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Score> getById(@PathVariable Long id) {
        Score score = scoreService.getById(id);
        return Result.success(score);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Score>> getAll(HttpServletRequest request) {
        Long departmentId = (Long) request.getAttribute("departmentId");
        Score score = new Score();
        score.setDepartmentId(departmentId);
        List<Score> scores = scoreService.getByCondition(score);
        return Result.success(scores);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Score>> getByCondition(Score score, HttpServletRequest request) {
        Long departmentId = (Long) request.getAttribute("departmentId");
        score.setDepartmentId(departmentId);
        List<Score> scores = scoreService.getByCondition(score);
        return Result.success(scores);
    }

    /**
     * 根据考试ID查询
     */
    @GetMapping("/exam/{examId}")
    public Result<List<Score>> getByExamId(@PathVariable Long examId) {
        List<Score> scores = scoreService.getByExamId(examId);
        return Result.success(scores);
    }

    /**
     * 根据考生ID查询
     */
    @GetMapping("/candidate/{candidateId}")
    public Result<List<Score>> getByCandidateId(@PathVariable Long candidateId) {
        List<Score> scores = scoreService.getByCandidateId(candidateId);
        return Result.success(scores);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scoreService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        scoreService.deleteBatch(ids);
        return Result.success();
    }
}