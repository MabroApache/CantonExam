package com.cantonfair.exam.controller;

import com.alibaba.fastjson2.JSON;
import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.ExamRecord;
import com.cantonfair.exam.entity.CandidateAnswer;
import com.cantonfair.exam.service.ExamRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 考试记录Controller
 */
@RestController
@RequestMapping("/record")
public class ExamRecordController {

    @Autowired
    private ExamRecordService examRecordService;

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<ExamRecord> getById(@PathVariable Long id) {
        ExamRecord record = examRecordService.getById(id);
        return Result.success(record);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<ExamRecord>> getAll(HttpServletRequest request) {
        Long departmentId = (Long) request.getAttribute("departmentId");
        ExamRecord record = new ExamRecord();
        record.setDepartmentId(departmentId);
        List<ExamRecord> records = examRecordService.getByCondition(record);
        return Result.success(records);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<ExamRecord>> getByCondition(ExamRecord record, HttpServletRequest request) {
        Long departmentId = (Long) request.getAttribute("departmentId");
        record.setDepartmentId(departmentId);
        List<ExamRecord> records = examRecordService.getByCondition(record);
        return Result.success(records);
    }

    /**
     * 开始考试
     */
    @PostMapping("/start")
    public Result<ExamRecord> startExam(@RequestBody Map<String, Object> params) {
        Long examId = Long.parseLong(params.get("examId").toString());
        Long paperId = Long.parseLong(params.get("paperId").toString());
        Long candidateId = Long.parseLong(params.get("candidateId").toString());
        String candidateName = (String) params.get("candidateName");
        
        ExamRecord record = examRecordService.startExam(examId, paperId, candidateId, candidateName);
        return Result.success(record);
    }

    /**
     * 提交试卷
     */
    @PostMapping("/submit")
    public Result<Void> submitExam(@RequestBody Map<String, Object> params) {
        Long recordId = Long.parseLong(params.get("recordId").toString());
        
        // 使用JSON正确转换答案列表
        Object answersObj = params.get("answers");
        List<CandidateAnswer> answers = JSON.parseArray(JSON.toJSONString(answersObj), CandidateAnswer.class);
        
        examRecordService.submitExam(recordId, answers);
        return Result.success();
    }

    /**
     * 手动阅卷
     */
    @PostMapping("/grade")
    public Result<Void> gradeExam(@RequestBody Map<String, Object> params) {
        Long recordId = Long.parseLong(params.get("recordId").toString());
        
        // 使用JSON正确转换答案列表
        Object answersObj = params.get("answers");
        List<CandidateAnswer> answers = JSON.parseArray(JSON.toJSONString(answersObj), CandidateAnswer.class);
        
        examRecordService.gradeExam(recordId, answers);
        return Result.success();
    }

    /**
     * 获取学生答案列表
     */
    @GetMapping("/answers/{recordId}")
    public Result<List<CandidateAnswer>> getCandidateAnswers(@PathVariable Long recordId) {
        List<CandidateAnswer> answers = examRecordService.getCandidateAnswers(recordId);
        return Result.success(answers);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        examRecordService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        examRecordService.deleteBatch(ids);
        return Result.success();
    }
}