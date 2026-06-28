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

    public List<Paper> getByCreatorId(Long creatorId) {
        return paperMapper.selectByCreatorId(creatorId);
    }

    public void add(Paper paper) {
        paperMapper.insert(paper);
    }

    @Transactional
    public void manualCreatePaper(Paper paper, List<Long> questionIds, List<BigDecimal> scores) {
        BigDecimal totalScore = BigDecimal.ZERO;
        int singleCount = 0, multiCount = 0, judgeCount = 0, fillCount = 0, essayCount = 0;
        
        List<PaperQuestion> paperQuestions = new ArrayList<>();
        for (int i = 0; i < questionIds.size(); i++) {
            Long questionId = questionIds.get(i);
            Question question = questionMapper.selectById(questionId);
            if (question != null) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(questionId);
                if (scores != null && i < scores.size()) {
                    pq.setScore(scores.get(i));
                    totalScore = totalScore.add(scores.get(i));
                } else {
                    pq.setScore(BigDecimal.ZERO);
                }
                pq.setSort(i + 1);
                paperQuestions.add(pq);
                
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
        paper.setStatus(0);
        
        paperMapper.insert(paper);
        
        for (PaperQuestion pq : paperQuestions) {
            pq.setPaperId(paper.getId());
        }
        paperQuestionMapper.insertBatch(paperQuestions);
    }

    @Transactional
    public void autoCreatePaper(Paper paper, int singleCount, int multiCount, int judgeCount, 
                                int fillCount, int essayCount, BigDecimal singleScore, 
                                BigDecimal multiScore, BigDecimal judgeScore, 
                                BigDecimal fillScore, BigDecimal essayScore) {
        List<PaperQuestion> paperQuestions = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        int sort = 1;
        
        Long singleTypeId = 1L;
        Long multiTypeId = 2L;
        Long judgeTypeId = 3L;
        Long fillTypeId = 4L;
        Long essayTypeId = 5L;
        
        if (singleCount > 0) {
            List<Question> questions = questionMapper.selectRandomQuestions(singleTypeId, singleCount);
            for (Question question : questions) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(question.getId());
                pq.setScore(singleScore);
                pq.setSort(sort++);
                paperQuestions.add(pq);
                totalScore = totalScore.add(singleScore);
            }
        }
        
        if (multiCount > 0) {
            List<Question> questions = questionMapper.selectRandomQuestions(multiTypeId, multiCount);
            for (Question question : questions) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(question.getId());
                pq.setScore(multiScore);
                pq.setSort(sort++);
                paperQuestions.add(pq);
                totalScore = totalScore.add(multiScore);
            }
        }
        
        if (judgeCount > 0) {
            List<Question> questions = questionMapper.selectRandomQuestions(judgeTypeId, judgeCount);
            for (Question question : questions) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(question.getId());
                pq.setScore(judgeScore);
                pq.setSort(sort++);
                paperQuestions.add(pq);
                totalScore = totalScore.add(judgeScore);
            }
        }
        
        if (fillCount > 0) {
            List<Question> questions = questionMapper.selectRandomQuestions(fillTypeId, fillCount);
            for (Question question : questions) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(question.getId());
                pq.setScore(fillScore);
                pq.setSort(sort++);
                paperQuestions.add(pq);
                totalScore = totalScore.add(fillScore);
            }
        }
        
        if (essayCount > 0) {
            List<Question> questions = questionMapper.selectRandomQuestions(essayTypeId, essayCount);
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
        
        paperMapper.insert(paper);
        
        for (PaperQuestion pq : paperQuestions) {
            pq.setPaperId(paper.getId());
        }
        paperQuestionMapper.insertBatch(paperQuestions);
    }

    public List<PaperQuestion> getPaperQuestions(Long paperId) {
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectByPaperId(paperId);
        
        for (PaperQuestion pq : paperQuestions) {
            Question question = questionMapper.selectById(pq.getQuestionId());
            if (question != null) {
                pq.setTypeId(question.getTypeId());
                pq.setTypeName(question.getTypeName());
                // 设置题目完整内容
                pq.setTitle(question.getTitle());
                pq.setOptionA(question.getOptionA());
                pq.setOptionB(question.getOptionB());
                pq.setOptionC(question.getOptionC());
                pq.setOptionD(question.getOptionD());
                pq.setAnswer(question.getAnswer());
                pq.setAnalysis(question.getAnalysis());
                pq.setTags(question.getTags());
                pq.setImageUrl(question.getImageUrl());
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