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

/**
 * 试卷Controller
 */
@RestController
@RequestMapping("/paper")
public class PaperController {

    @Autowired
    private PaperService paperService;

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Paper> getById(@PathVariable Long id) {
        Paper paper = paperService.getById(id);
        return Result.success(paper);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Paper>> getAll() {
        List<Paper> papers = paperService.getAll();
        return Result.success(papers);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Paper>> getByCondition(Paper paper) {
        List<Paper> papers = paperService.getByCondition(paper);
        return Result.success(papers);
    }

    /**
     * 根据教师ID查询
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<Paper>> getByTeacherId(@PathVariable Long teacherId) {
        List<Paper> papers = paperService.getByTeacherId(teacherId);
        return Result.success(papers);
    }

    /**
     * 新增试卷（管理员）
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Paper paper) {
        paper.setStatus(0); // 草稿状态
        paper.setSingleCount(0);
        paper.setMultiCount(0);
        paper.setJudgeCount(0);
        paper.setFillCount(0);
        paper.setEssayCount(0);
        paperService.add(paper);
        return Result.success();
    }

    /**
     * 手动组卷
     */
    @PostMapping("/manual")
    public Result<Void> manualCreatePaper(@RequestBody Map<String, Object> params) {
        Paper paper = new Paper();
        paper.setName((String) params.get("name"));
        
        // 处理 courseId - 可能接收到的格式是数组
        Object courseIdObj = params.get("courseId");
        if (courseIdObj instanceof List) {
            List<?> courseIdList = (List<?>) courseIdObj;
            if (!courseIdList.isEmpty()) {
                Object idVal = courseIdList.get(0);
                if (idVal instanceof Number) {
                    paper.setCourseId(((Number) idVal).longValue());
                } else {
                    paper.setCourseId(Long.parseLong(idVal.toString()));
                }
            }
        } else if (courseIdObj != null) {
            if (courseIdObj instanceof Number) {
                paper.setCourseId(((Number) courseIdObj).longValue());
            } else {
                paper.setCourseId(Long.parseLong(courseIdObj.toString()));
            }
        }
        
        paper.setCourseName((String) params.get("courseName"));
        
        // 处理 teacherId
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
        
        // 处理 duration
        Object durationObj = params.get("duration");
        if (durationObj instanceof Number) {
            paper.setDuration(((Number) durationObj).intValue());
        } else {
            paper.setDuration(Integer.parseInt(durationObj.toString()));
        }
        
        paper.setDescription((String) params.get("description"));
        
        // 处理 questionIds - 转换为 Long 类型列表，过滤掉 null 值
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

    /**
     * 自动组卷
     */
    @PostMapping("/auto")
    public Result<Void> autoCreatePaper(@RequestBody Map<String, Object> params) {
        Paper paper = new Paper();
        paper.setName((String) params.get("name"));
        
        // 处理 courseId - 可能接收到的格式是数组
        Object courseIdObj = params.get("courseId");
        if (courseIdObj instanceof List) {
            List<?> courseIdList = (List<?>) courseIdObj;
            if (!courseIdList.isEmpty()) {
                paper.setCourseId(Long.parseLong(courseIdList.get(0).toString()));
            }
        } else {
            paper.setCourseId(Long.parseLong(courseIdObj.toString()));
        }
        
        paper.setCourseName((String) params.get("courseName"));
        
        // 处理 teacherId
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

    /**
     * 获取试卷题目列表
     */
    @GetMapping("/questions/{paperId}")
    public Result<List<PaperQuestion>> getPaperQuestions(@PathVariable Long paperId) {
        List<PaperQuestion> questions = paperService.getPaperQuestions(paperId);
        return Result.success(questions);
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody Paper paper) {
        paperService.update(paper);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        paperService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        paperService.deleteBatch(ids);
        return Result.success();
    }
}