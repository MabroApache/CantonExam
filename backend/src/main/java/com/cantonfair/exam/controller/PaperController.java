package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Paper;
import com.cantonfair.exam.entity.PaperQuestion;
import com.cantonfair.exam.service.PaperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paper")
public class PaperController {

    private static final Logger logger = LoggerFactory.getLogger(PaperController.class);

    @Autowired
    private PaperService paperService;

    @GetMapping("/{id}")
    public Result<Paper> getById(@PathVariable Long id) {
        Paper paper = paperService.getById(id);
        return Result.success(paper);
    }

    @GetMapping("/list")
    public Result<List<Paper>> getAll(HttpServletRequest request) {
        Long departmentId = (Long) request.getAttribute("departmentId");
        Paper paper = new Paper();
        paper.setDepartmentId(departmentId);
        List<Paper> papers = paperService.getByCondition(paper);
        return Result.success(papers);
    }

    @GetMapping("/search")
    public Result<List<Paper>> getByCondition(Paper paper, HttpServletRequest request) {
        Long departmentId = (Long) request.getAttribute("departmentId");
        paper.setDepartmentId(departmentId);
        List<Paper> papers = paperService.getByCondition(paper);
        return Result.success(papers);
    }

    @GetMapping("/creator/{creatorId}")
    public Result<List<Paper>> getByCreatorId(@PathVariable Long creatorId) {
        List<Paper> papers = paperService.getByCreatorId(creatorId);
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
        try {
            logger.info("开始手动组卷，参数: {}", params);
            
            Paper paper = new Paper();
            paper.setName((String) params.get("name"));
            
            Object creatorIdObj = params.get("creatorId");
            if (creatorIdObj instanceof List) {
                List<?> creatorIdList = (List<?>) creatorIdObj;
                if (!creatorIdList.isEmpty()) {
                    Object idVal = creatorIdList.get(0);
                    if (idVal instanceof Number) {
                        paper.setCreatorId(((Number) idVal).longValue());
                    } else {
                        paper.setCreatorId(Long.parseLong(idVal.toString()));
                    }
                }
            } else if (creatorIdObj != null) {
                if (creatorIdObj instanceof Number) {
                    paper.setCreatorId(((Number) creatorIdObj).longValue());
                } else {
                    paper.setCreatorId(Long.parseLong(creatorIdObj.toString()));
                }
            }
            
            paper.setCreatorName((String) params.get("creatorName"));
            
            Object departmentIdObj = params.get("departmentId");
            if (departmentIdObj instanceof Number) {
                paper.setDepartmentId(((Number) departmentIdObj).longValue());
            } else if (departmentIdObj != null) {
                paper.setDepartmentId(Long.parseLong(departmentIdObj.toString()));
            }
            
            paper.setDepartmentName((String) params.get("departmentName"));
            
            Object durationObj = params.get("duration");
            if (durationObj instanceof Number) {
                paper.setDuration(((Number) durationObj).intValue());
            } else if (durationObj != null) {
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
            
            List<BigDecimal> scores = new ArrayList<>();
            Object scoresObj = params.get("scores");
            if (scoresObj instanceof List) {
                List<?> scoreList = (List<?>) scoresObj;
                for (Object score : scoreList) {
                    if (score != null) {
                        scores.add(new BigDecimal(score.toString()));
                    }
                }
            }
            
            paperService.manualCreatePaper(paper, questionIds, scores);
            logger.info("手动组卷成功，试卷名称: {}", paper.getName());
            return Result.success();
        } catch (Exception e) {
            logger.error("手动组卷失败", e);
            return Result.error("组卷失败：" + e.getMessage());
        }
    }

    @PostMapping("/auto")
    public Result<Void> autoCreatePaper(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            logger.info("开始自动组卷，参数: {}", params);
            
            Paper paper = new Paper();
            paper.setName((String) params.get("name"));
            
            Object creatorIdObj = params.get("creatorId");
            if (creatorIdObj instanceof List) {
                List<?> creatorIdList = (List<?>) creatorIdObj;
                if (!creatorIdList.isEmpty()) {
                    paper.setCreatorId(Long.parseLong(creatorIdList.get(0).toString()));
                }
            } else if (creatorIdObj != null) {
                paper.setCreatorId(Long.parseLong(creatorIdObj.toString()));
            }
            
            paper.setCreatorName((String) params.get("creatorName"));
            
            Long departmentId = (Long) request.getAttribute("departmentId");
            paper.setDepartmentId(departmentId);
            
            Object durationObj = params.get("duration");
            if (durationObj instanceof Number) {
                paper.setDuration(((Number) durationObj).intValue());
            } else if (durationObj != null) {
                paper.setDuration(Integer.parseInt(durationObj.toString()));
            }
            
            paper.setDescription((String) params.get("description"));
            
            int singleCount = params.get("singleCount") != null ? Integer.parseInt(params.get("singleCount").toString()) : 0;
            int multiCount = params.get("multiCount") != null ? Integer.parseInt(params.get("multiCount").toString()) : 0;
            int judgeCount = params.get("judgeCount") != null ? Integer.parseInt(params.get("judgeCount").toString()) : 0;
            int fillCount = params.get("fillCount") != null ? Integer.parseInt(params.get("fillCount").toString()) : 0;
            int essayCount = params.get("essayCount") != null ? Integer.parseInt(params.get("essayCount").toString()) : 0;
            
            BigDecimal singleScore = params.get("singleScore") != null ? new BigDecimal(params.get("singleScore").toString()) : BigDecimal.ZERO;
            BigDecimal multiScore = params.get("multiScore") != null ? new BigDecimal(params.get("multiScore").toString()) : BigDecimal.ZERO;
            BigDecimal judgeScore = params.get("judgeScore") != null ? new BigDecimal(params.get("judgeScore").toString()) : BigDecimal.ZERO;
            BigDecimal fillScore = params.get("fillScore") != null ? new BigDecimal(params.get("fillScore").toString()) : BigDecimal.ZERO;
            BigDecimal essayScore = params.get("essayScore") != null ? new BigDecimal(params.get("essayScore").toString()) : BigDecimal.ZERO;
            
            paperService.autoCreatePaper(paper, singleCount, multiCount, judgeCount, 
                    fillCount, essayCount, singleScore, multiScore, judgeScore, fillScore, essayScore);
            logger.info("自动组卷成功，试卷名称: {}", paper.getName());
            return Result.success();
        } catch (Exception e) {
            logger.error("自动组卷失败", e);
            return Result.error("组卷失败：" + e.getMessage());
        }
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