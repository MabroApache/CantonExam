package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Exam;
import com.cantonfair.exam.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 考试安排Controller
 */
@RestController
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Exam> getById(@PathVariable Long id) {
        Exam exam = examService.getById(id);
        return Result.success(exam);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Exam>> getAll(HttpServletRequest request) {
        Long departmentId = (Long) request.getAttribute("departmentId");
        List<Exam> exams = examService.getByDepartmentId(departmentId);
        return Result.success(exams);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Exam>> getByCondition(Exam exam, HttpServletRequest request) {
        Long departmentId = (Long) request.getAttribute("departmentId");
        exam.setDepartmentId(departmentId);
        List<Exam> exams = examService.getByCondition(exam);
        return Result.success(exams);
    }

    /**
     * 根据出题人ID查询考试
     */
    @GetMapping("/creator/{creatorId}")
    public Result<List<Exam>> getByCreatorId(@PathVariable Long creatorId) {
        List<Exam> exams = examService.getByCreatorId(creatorId);
        return Result.success(exams);
    }

    /**
     * 获取考生可参加的考试列表
     */
    @GetMapping("/candidate/{candidateId}")
    public Result<List<Exam>> getByCandidateId(@PathVariable Long candidateId) {
        List<Exam> exams = examService.getAvailableExams(candidateId);
        return Result.success(exams);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Exam exam, HttpServletRequest request) {
        Long departmentId = (Long) request.getAttribute("departmentId");
        exam.setDepartmentId(departmentId);
        examService.add(exam);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody Exam exam) {
        examService.update(exam);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        examService.deleteBatch(ids);
        return Result.success();
    }
}