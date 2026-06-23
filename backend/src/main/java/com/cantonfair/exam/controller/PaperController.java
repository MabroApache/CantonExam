package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Paper;
import com.cantonfair.exam.entity.PaperQuestion;
import com.cantonfair.exam.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paper")
public class PaperController {

    @Autowired
    private PaperService paperService;

    @GetMapping("/{id}")
    public Result<Paper> getById(@PathVariable Long id) {
        Paper paper = paperService.getById(id);
        return Result.success(paper);
    }

    @GetMapping("/list")
    public Result<List<Paper>> getAll() {
        List<Paper> papers = paperService.getAll();
        return Result.success(papers);
    }

    @GetMapping("/search")
    public Result<List<Paper>> getByCondition(Paper paper) {
        List<Paper> papers = paperService.getByCondition(paper);
        return Result.success(papers);
    }

    @GetMapping("/teacher/{teacherId}")
    public Result<List<Paper>> getByTeacherId(@PathVariable Long teacherId) {
        List<Paper> papers = paperService.getByTeacherId(teacherId);
        return Result.success(papers);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Paper paper) {
        paper.setStatus(0);
        paper.setSingleCount(0);
        paper.setMultiCount(0);
        paper.setJudgeCount(0);
        paper.setFillCount(0);
        paper.setEssayCount(0);
        paperService.add(paper);
        return Result.success();
    }

    @PostMapping("/manual")
    public Result<Void> manualCreatePaper(@RequestBody Map<String, Object> params) {
        Paper paper = new Paper();
        paper.setName((String) params.get("name"));
        
        Object teacherIdObj = params.get("teacherId");
        if (teacherIdObj instanceof List) {
            List<?> teacherIdList = (List<?>) teacherIdObj;
            if (!teacherIdList.isEmpty()) {
                Object idVal = teacherIdList.get(0);
                if (idVal instanceof Number) {
                    paper.setTeacherId(((Number) idVal).longValue());
                } else {
                    paper.setTeacherId(Long.parseLong(idVal.toString()));
                }
            }
        } else if (teacherIdObj != null) {
            if (teacherIdObj instanceof Number) {
                paper.setTeacherId(((Number) teacherIdObj).longValue());
            } else {
                paper.setTeacherId(Long.parseLong(teacherIdObj.toString()));
            }
        }
        
        paper.setTeacherName((String) params.get("teacherName"));
        
        Object durationObj = params.get("duration");
        if (durationObj instanceof Number) {
            paper.setDuration(((Number) durationObj).intValue());
        } else {
            paper.setDuration(Integer.parseInt(durationObj.toString()));
        }
        
        paper.setDescription((String) params.get("description"));
        
        List<Long> questionIds = new ArrayList<>();
        Object questionIdsObj = params.get("questionIds");
        if (questionIdsObj instanceof List) {
            List<?> qIds = (List<?>) questionIdsObj;
            for (Object qId : qIds) {
                if (qId != null) {
                    if (qId instanceof Number) {
                        questionIds.add(((Number) qId).longValue());
                    } else {
                        questionIds.add(Long.parseLong(qId.toString()));
                    }
                }
            }
        }
        
        paperService.manualCreatePaper(paper, questionIds);
        return Result.success();
    }

    @PostMapping("/auto")
    public Result<Void> autoCreatePaper(@RequestBody Map<String, Object> params) {
        Paper paper = new Paper();
        paper.setName((String) params.get("name"));
        
        Object teacherIdObj = params.get("teacherId");
        if (teacherIdObj instanceof List) {
            List<?> teacherIdList = (List<?>) teacherIdObj;
            if (!teacherIdList.isEmpty()) {
                paper.setTeacherId(Long.parseLong(teacherIdList.get(0).toString()));
            }
        } else {
            paper.setTeacherId(Long.parseLong(teacherIdObj.toString()));
        }
        
        paper.setTeacherName((String) params.get("teacherName"));
        paper.setDuration(Integer.parseInt(params.get("duration").toString()));
        paper.setDescription((String) params.get("description"));
        
        int singleCount = Integer.parseInt(params.get("singleCount").toString());
        int multiCount = Integer.parseInt(params.get("multiCount").toString());
        int judgeCount = Integer.parseInt(params.get("judgeCount").toString());
        int fillCount = Integer.parseInt(params.get("fillCount").toString());
        int essayCount = Integer.parseInt(params.get("essayCount").toString());
        
        BigDecimal singleScore = new BigDecimal(params.get("singleScore").toString());
        BigDecimal multiScore = new BigDecimal(params.get("multiScore").toString());
        BigDecimal judgeScore = new BigDecimal(params.get("judgeScore").toString());
        BigDecimal fillScore = new BigDecimal(params.get("fillScore").toString());
        BigDecimal essayScore = new BigDecimal(params.get("essayScore").toString());
        
        paperService.autoCreatePaper(paper, singleCount, multiCount, judgeCount, 
                fillCount, essayCount, singleScore, multiScore, judgeScore, fillScore, essayScore);
        return Result.success();
    }

    @GetMapping("/questions/{paperId}")
    public Result<List<PaperQuestion>> getPaperQuestions(@PathVariable Long paperId) {
        List<PaperQuestion> questions = paperService.getPaperQuestions(paperId);
        return Result.success(questions);
    }

    @PutMapping
    public Result<Void> update(@RequestBody Paper paper) {
        paperService.update(paper);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        paperService.delete(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        paperService.deleteBatch(ids);
        return Result.success();
    }
}