package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.Paper;
import com.cantonfair.exam.entity.PaperQuestion;
import com.cantonfair.exam.entity.Question;
import com.cantonfair.exam.mapper.PaperMapper;
import com.cantonfair.exam.mapper.PaperQuestionMapper;
import com.cantonfair.exam.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 试卷Service
 */
@Service
public class PaperService {

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    public Paper getById(Long id) {
        return paperMapper.selectById(id);
    }

    public List<Paper> getAll() {
        return paperMapper.selectAll();
    }

    public List<Paper> getByCondition(Paper paper) {
        return paperMapper.selectByCondition(paper);
    }

    public List<Paper> getByTeacherId(Long teacherId) {
        return paperMapper.selectByTeacherId(teacherId);
    }

    /**
     * 新增试卷（管理员）
     */
    public void add(Paper paper) {
        paperMapper.insert(paper);
    }

    /**
     * 手动组卷
     */
    @Transactional
    public void manualCreatePaper(Paper paper, List<Long> questionIds) {
        // 计算总分
        BigDecimal totalScore = BigDecimal.ZERO;
        int singleCount = 0, multiCount = 0, judgeCount = 0, fillCount = 0, essayCount = 0;
        
        List<PaperQuestion> paperQuestions = new ArrayList<>();
        for (int i = 0; i < questionIds.size(); i++) {
            Long questionId = questionIds.get(i);
            Question question = questionMapper.selectById(questionId);
            if (question != null) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(questionId);
                pq.setScore(question.getScore());
                pq.setSort(i + 1);
                paperQuestions.add(pq);
                
                totalScore = totalScore.add(question.getScore());
                
                // 统计题型数量
                String typeName = question.getTypeName();
                if ("单选题".equals(typeName)) singleCount++;
                else if ("多选题".equals(typeName)) multiCount++;
                else if ("判断题".equals(typeName)) judgeCount++;
                else if ("填空题".equals(typeName)) fillCount++;
                else if ("简答题".equals(typeName)) essayCount++;
            }
        }
        
        paper.setTotalScore(totalScore);
        paper.setSingleCount(singleCount);
        paper.setMultiCount(multiCount);
        paper.setJudgeCount(judgeCount);
        paper.setFillCount(fillCount);
        paper.setEssayCount(essayCount);
        paper.setStatus(0); // 草稿状态
        
        // 先插入试卷，获取ID
        paperMapper.insert(paper);
        
        // 再设置paperId并插入试卷题目
        for (PaperQuestion pq : paperQuestions) {
            pq.setPaperId(paper.getId());
        }
        paperQuestionMapper.insertBatch(paperQuestions);
    }

    /**
     * 自动组卷
     */
    @Transactional
    public void autoCreatePaper(Paper paper, int singleCount, int multiCount, int judgeCount, 
                                int fillCount, int essayCount, BigDecimal singleScore, 
                                BigDecimal multiScore, BigDecimal judgeScore, 
                                BigDecimal fillScore, BigDecimal essayScore) {
        List<PaperQuestion> paperQuestions = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        int sort = 1;
        
        // 获取题型ID
        Long singleTypeId = 1L; // 单选题
        Long multiTypeId = 2L; // 多选题
        Long judgeTypeId = 3L; // 判断题
        Long fillTypeId = 4L; // 填空题
        Long essayTypeId = 5L; // 简答题
        
        // 随机抽取单选题
        if (singleCount > 0) {
            List<Question> questions = questionMapper.selectRandomQuestions(paper.getCourseId(), singleTypeId, singleCount);
            for (Question question : questions) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(question.getId());
                pq.setScore(singleScore);
                pq.setSort(sort++);
                paperQuestions.add(pq);
                totalScore = totalScore.add(singleScore);
            }
        }
        
        // 随机抽取多选题
        if (multiCount > 0) {
            List<Question> questions = questionMapper.selectRandomQuestions(paper.getCourseId(), multiTypeId, multiCount);
            for (Question question : questions) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(question.getId());
                pq.setScore(multiScore);
                pq.setSort(sort++);
                paperQuestions.add(pq);
                totalScore = totalScore.add(multiScore);
            }
        }
        
        // 随机抽取判断题
        if (judgeCount > 0) {
            List<Question> questions = questionMapper.selectRandomQuestions(paper.getCourseId(), judgeTypeId, judgeCount);
            for (Question question : questions) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(question.getId());
                pq.setScore(judgeScore);
                pq.setSort(sort++);
                paperQuestions.add(pq);
                totalScore = totalScore.add(judgeScore);
            }
        }
        
        // 随机抽取填空题
        if (fillCount > 0) {
            List<Question> questions = questionMapper.selectRandomQuestions(paper.getCourseId(), fillTypeId, fillCount);
            for (Question question : questions) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(question.getId());
                pq.setScore(fillScore);
                pq.setSort(sort++);
                paperQuestions.add(pq);
                totalScore = totalScore.add(fillScore);
            }
        }
        
        // 随机抽取简答题
        if (essayCount > 0) {
            List<Question> questions = questionMapper.selectRandomQuestions(paper.getCourseId(), essayTypeId, essayCount);
            for (Question question : questions) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(question.getId());
                pq.setScore(essayScore);
                pq.setSort(sort++);
                paperQuestions.add(pq);
                totalScore = totalScore.add(essayScore);
            }
        }
        
        paper.setTotalScore(totalScore);
        paper.setSingleCount(singleCount);
        paper.setMultiCount(multiCount);
        paper.setJudgeCount(judgeCount);
        paper.setFillCount(fillCount);
        paper.setEssayCount(essayCount);
        paper.setStatus(0);
        
        // 先插入试卷，获取ID
        paperMapper.insert(paper);
        
        // 再设置paperId并插入试卷题目
        for (PaperQuestion pq : paperQuestions) {
            pq.setPaperId(paper.getId());
        }
        paperQuestionMapper.insertBatch(paperQuestions);
    }

    /**
     * 获取试卷题目列表（包含题目类型信息）
     */
    public List<PaperQuestion> getPaperQuestions(Long paperId) {
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectByPaperId(paperId);
        
        // 为每个题目添加类型信息
        for (PaperQuestion pq : paperQuestions) {
            Question question = questionMapper.selectById(pq.getQuestionId());
            if (question != null) {
                pq.setTypeId(question.getTypeId());
                pq.setTypeName(question.getTypeName());
            }
        }
        
        return paperQuestions;
    }

    public void update(Paper paper) {
        paperMapper.update(paper);
    }

    public void delete(Long id) {
        paperQuestionMapper.deleteByPaperId(id);
        paperMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            paperQuestionMapper.deleteByPaperId(id);
        }
        paperMapper.deleteBatch(ids);
    }
}