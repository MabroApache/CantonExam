package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.ExamRecord;
import com.cantonfair.exam.entity.PaperQuestion;
import com.cantonfair.exam.entity.Question;
import com.cantonfair.exam.entity.Score;
import com.cantonfair.exam.entity.CandidateAnswer;
import com.cantonfair.exam.entity.Exam;
import com.cantonfair.exam.entity.Paper;
import com.cantonfair.exam.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 考试记录Service
 */
@Service
public class ExamRecordService {

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private CandidateAnswerMapper CandidateAnswerMapper;

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private PaperMapper paperMapper;

    public ExamRecord getById(Long id) {
        return examRecordMapper.selectById(id);
    }

    public List<ExamRecord> getAll() {
        return examRecordMapper.selectAll();
    }

    public List<ExamRecord> getByCondition(ExamRecord examRecord) {
        return examRecordMapper.selectByCondition(examRecord);
    }

    public ExamRecord getByExamAndCandidate(Long examId, Long CandidateId) {
        return examRecordMapper.selectByExamAndCandidate(examId, CandidateId);
    }

    /**
     * 开始考试
     */
    @Transactional
    public ExamRecord startExam(Long examId, Long paperId, Long CandidateId, String CandidateName) {
        // 检查是否已经参加过考试
        ExamRecord existRecord = examRecordMapper.selectByExamAndCandidate(examId, CandidateId);
        if (existRecord != null) {
            return existRecord;
        }
        
        Exam exam = examMapper.selectById(examId);
        Paper paper = paperMapper.selectById(paperId);
        
        // 创建考试记录
        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setExamName(exam != null ? exam.getName() : "");
        record.setPaperId(paperId);
        record.setPaperName(paper != null ? paper.getName() : "");
        record.setCandidateId(CandidateId);
        record.setCandidateName(CandidateName);
        record.setStartTime(LocalDateTime.now());
        record.setStatus(0); // 未提交
        examRecordMapper.insert(record);
        
        // 获取试卷题目
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectByPaperId(paperId);
        
        // 创建学生答案记录
        List<CandidateAnswer> CandidateAnswers = new ArrayList<>();
        for (PaperQuestion pq : paperQuestions) {
            Question question = questionMapper.selectById(pq.getQuestionId());
            
            CandidateAnswer answer = new CandidateAnswer();
            answer.setRecordId(record.getId());
            answer.setQuestionId(pq.getQuestionId());
            answer.setTypeId(question.getTypeId());
            answer.setTypeName(question.getTypeName());
            answer.setTitle(question.getTitle());
            answer.setOptionA(question.getOptionA());
            answer.setOptionB(question.getOptionB());
            answer.setOptionC(question.getOptionC());
            answer.setOptionD(question.getOptionD());
            answer.setImageUrl(question.getImageUrl());
            answer.setCorrectAnswer(question.getAnswer());
            answer.setScore(pq.getScore());
            answer.setGetScore(BigDecimal.ZERO);
            answer.setIsCorrect(0);
            
            CandidateAnswers.add(answer);
        }
        
        CandidateAnswerMapper.insertBatch(CandidateAnswers);
        
        return record;
    }

    /**
     * 提交试卷
     */
    @Transactional
    public void submitExam(Long recordId, List<CandidateAnswer> answers) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }
        
        // 获取该考试记录下的所有学生答案
        List<CandidateAnswer> existingAnswers = CandidateAnswerMapper.selectByRecordId(recordId);
        
        // 创建 questionId -> CandidateAnswer 的映射
        java.util.Map<Long, CandidateAnswer> answerMap = new java.util.HashMap<>();
        for (CandidateAnswer sa : existingAnswers) {
            answerMap.put(sa.getQuestionId(), sa);
        }
        
        // 更新学生答案
        BigDecimal objectiveScore = BigDecimal.ZERO;
        BigDecimal subjectiveScore = BigDecimal.ZERO;
        
        for (CandidateAnswer answer : answers) {
            CandidateAnswer existingAnswer = answerMap.get(answer.getQuestionId());
            if (existingAnswer == null) {
                continue;
            }
            
            Question question = questionMapper.selectById(answer.getQuestionId());
            if (question == null) {
                continue;
            }
            
            existingAnswer.setCandidateAnswer(answer.getCandidateAnswer());
            
            String typeName = question.getTypeName();
            if ("单选题".equals(typeName)) {
                if (existingAnswer.getCandidateAnswer() != null && 
                    existingAnswer.getCandidateAnswer().equals(question.getAnswer())) {
                    existingAnswer.setGetScore(existingAnswer.getScore());
                    existingAnswer.setIsCorrect(1);
                    objectiveScore = objectiveScore.add(existingAnswer.getScore());
                } else {
                    existingAnswer.setGetScore(BigDecimal.ZERO);
                    existingAnswer.setIsCorrect(0);
                }
            } else if ("判断题".equals(typeName)) {
                if (existingAnswer.getCandidateAnswer() != null && 
                    existingAnswer.getCandidateAnswer().equals(question.getAnswer())) {
                    existingAnswer.setGetScore(existingAnswer.getScore());
                    existingAnswer.setIsCorrect(1);
                    objectiveScore = objectiveScore.add(existingAnswer.getScore());
                } else {
                    existingAnswer.setGetScore(BigDecimal.ZERO);
                    existingAnswer.setIsCorrect(0);
                }
            } else if ("多选题".equals(typeName)) {
                String CandidateAns = existingAnswer.getCandidateAnswer();
                String correctAns = question.getAnswer();
                
                if (CandidateAns != null && CandidateAns.equals(correctAns)) {
                    existingAnswer.setGetScore(existingAnswer.getScore());
                    existingAnswer.setIsCorrect(1);
                    objectiveScore = objectiveScore.add(existingAnswer.getScore());
                } else if (CandidateAns != null && !CandidateAns.isEmpty()) {
                    int correctCount = 0;
                    int totalCorrectCount = correctAns.length();
                    
                    for (char c : CandidateAns.toCharArray()) {
                        if (correctAns.indexOf(c) >= 0) {
                            correctCount++;
                        }
                    }
                    
                    if (correctCount > 0) {
                        BigDecimal halfScore = existingAnswer.getScore().divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
                        existingAnswer.setGetScore(halfScore);
                        existingAnswer.setIsCorrect(2);
                        objectiveScore = objectiveScore.add(halfScore);
                    } else {
                        existingAnswer.setGetScore(BigDecimal.ZERO);
                        existingAnswer.setIsCorrect(0);
                    }
                } else {
                    existingAnswer.setGetScore(BigDecimal.ZERO);
                    existingAnswer.setIsCorrect(0);
                }
            } else if ("填空题".equals(typeName)) {
                existingAnswer.setGetScore(BigDecimal.ZERO);
                existingAnswer.setIsCorrect(0);
                subjectiveScore = subjectiveScore.add(existingAnswer.getScore());
            } else if ("简答题".equals(typeName)) {
                existingAnswer.setGetScore(BigDecimal.ZERO);
                existingAnswer.setIsCorrect(0);
                subjectiveScore = subjectiveScore.add(existingAnswer.getScore());
            }
            
            CandidateAnswerMapper.update(existingAnswer);
        }
        
        record.setSubmitTime(LocalDateTime.now());
        record.setObjectiveScore(objectiveScore);
        record.setSubjectiveScore(subjectiveScore);
        record.setTotalScore(objectiveScore.add(subjectiveScore));
        record.setStatus(1);
        examRecordMapper.update(record);
    }

    /**
     * 手动阅卷
     */
    @Transactional
    public void gradeExam(Long recordId, List<CandidateAnswer> answers) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }
        
        BigDecimal objectiveScore = BigDecimal.ZERO;
        BigDecimal subjectiveScore = BigDecimal.ZERO;
        
        for (CandidateAnswer answer : answers) {
            CandidateAnswer existingAnswer = CandidateAnswerMapper.selectById(answer.getId());
            if (existingAnswer != null) {
                existingAnswer.setGetScore(answer.getGetScore());
                existingAnswer.setComment(answer.getComment());
                CandidateAnswerMapper.update(existingAnswer);
                
                String typeName = existingAnswer.getTypeName();
                if ("填空题".equals(typeName) || "简答题".equals(typeName)) {
                    subjectiveScore = subjectiveScore.add(existingAnswer.getGetScore());
                } else {
                    objectiveScore = objectiveScore.add(existingAnswer.getGetScore());
                }
            }
        }
        
        BigDecimal totalScore = objectiveScore.add(subjectiveScore);
        
        record.setObjectiveScore(objectiveScore);
        record.setSubjectiveScore(subjectiveScore);
        record.setTotalScore(totalScore);
        record.setStatus(2);
        examRecordMapper.update(record);
        
        Score existingScore = scoreMapper.selectByExamAndCandidate(record.getExamId(), record.getCandidateId());
        if (existingScore != null) {
            existingScore.setTotalScore(totalScore);
            existingScore.setObjectiveScore(objectiveScore);
            existingScore.setSubjectiveScore(subjectiveScore);
            scoreMapper.update(existingScore);
        } else {
            Exam exam = examMapper.selectById(record.getExamId());
            Paper paper = paperMapper.selectById(record.getPaperId());
            
            Score score = new Score();
            score.setExamId(record.getExamId());
            score.setExamName(exam != null ? exam.getName() : record.getExamName());
            score.setPaperId(record.getPaperId());
            score.setPaperName(paper != null ? paper.getName() : record.getPaperName());
            score.setCandidateId(record.getCandidateId());
            score.setCandidateName(record.getCandidateName());
            score.setTotalScore(totalScore);
            score.setObjectiveScore(objectiveScore);
            score.setSubjectiveScore(subjectiveScore);
            score.setSubmitTime(record.getSubmitTime());
            scoreMapper.insert(score);
        }
    }

    /**
     * 获取学生答案列表
     */
    public List<CandidateAnswer> getCandidateAnswers(Long recordId) {
        return CandidateAnswerMapper.selectByRecordId(recordId);
    }

    public void delete(Long id) {
        CandidateAnswerMapper.deleteByRecordId(id);
        examRecordMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            CandidateAnswerMapper.deleteByRecordId(id);
        }
        examRecordMapper.deleteBatch(ids);
    }
}