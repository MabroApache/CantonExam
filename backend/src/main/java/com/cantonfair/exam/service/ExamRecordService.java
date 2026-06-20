package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.ExamRecord;
import com.cantonfair.exam.entity.PaperQuestion;
import com.cantonfair.exam.entity.Question;
import com.cantonfair.exam.entity.Score;
import com.cantonfair.exam.entity.StudentAnswer;
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
    private StudentAnswerMapper studentAnswerMapper;

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    public ExamRecord getById(Long id) {
        return examRecordMapper.selectById(id);
    }

    public List<ExamRecord> getAll() {
        return examRecordMapper.selectAll();
    }

    public List<ExamRecord> getByCondition(ExamRecord examRecord) {
        return examRecordMapper.selectByCondition(examRecord);
    }

    public ExamRecord getByExamAndStudent(Long examId, Long studentId) {
        return examRecordMapper.selectByExamAndStudent(examId, studentId);
    }

    /**
     * 开始考试
     */
    @Transactional
    public ExamRecord startExam(Long examId, Long paperId, Long studentId, String studentName) {
        // 检查是否已经参加过考试
        ExamRecord existRecord = examRecordMapper.selectByExamAndStudent(examId, studentId);
        if (existRecord != null) {
            return existRecord;
        }
        
        // 创建考试记录
        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setPaperId(paperId);
        record.setStudentId(studentId);
        record.setStudentName(studentName);
        record.setStartTime(LocalDateTime.now());
        record.setStatus(0); // 未提交
        examRecordMapper.insert(record);
        
        // 获取试卷题目
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectByPaperId(paperId);
        
        // 创建学生答案记录
        List<StudentAnswer> studentAnswers = new ArrayList<>();
        for (PaperQuestion pq : paperQuestions) {
            Question question = questionMapper.selectById(pq.getQuestionId());
            
            StudentAnswer answer = new StudentAnswer();
            answer.setRecordId(record.getId());
            answer.setQuestionId(pq.getQuestionId());
            answer.setTypeId(question.getTypeId());
            answer.setTypeName(question.getTypeName());
            answer.setCorrectAnswer(question.getAnswer());
            answer.setScore(pq.getScore());
            answer.setGetScore(BigDecimal.ZERO);
            answer.setIsCorrect(0);
            
            studentAnswers.add(answer);
        }
        
        studentAnswerMapper.insertBatch(studentAnswers);
        
        return record;
    }

    /**
     * 提交试卷
     */
    @Transactional
    public void submitExam(Long recordId, List<StudentAnswer> answers) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }
        
        // 获取该考试记录下的所有学生答案
        List<StudentAnswer> existingAnswers = studentAnswerMapper.selectByRecordId(recordId);
        
        // 创建 questionId -> StudentAnswer 的映射
        java.util.Map<Long, StudentAnswer> answerMap = new java.util.HashMap<>();
        for (StudentAnswer sa : existingAnswers) {
            answerMap.put(sa.getQuestionId(), sa);
        }
        
        // 更新学生答案
        BigDecimal objectiveScore = BigDecimal.ZERO; // 客观题得分
        BigDecimal subjectiveScore = BigDecimal.ZERO; // 主观题得分
        
        for (StudentAnswer answer : answers) {
            // 获取已有的学生答案记录
            StudentAnswer existingAnswer = answerMap.get(answer.getQuestionId());
            if (existingAnswer == null) {
                continue; // 找不到对应的答案记录，跳过
            }
            
            Question question = questionMapper.selectById(answer.getQuestionId());
            if (question == null) {
                continue; // 找不到题目，跳过
            }
            
            // 设置学生答案
            existingAnswer.setStudentAnswer(answer.getStudentAnswer());
            
            // 自动阅卷客观题
            String typeName = question.getTypeName();
            if ("单选题".equals(typeName) || "判断题".equals(typeName)) {
                // 单选题和判断题：完全匹配
                if (existingAnswer.getStudentAnswer() != null && 
                    existingAnswer.getStudentAnswer().equals(question.getAnswer())) {
                    existingAnswer.setGetScore(existingAnswer.getScore());
                    existingAnswer.setIsCorrect(1);
                    objectiveScore = objectiveScore.add(existingAnswer.getScore());
                } else {
                    existingAnswer.setGetScore(BigDecimal.ZERO);
                    existingAnswer.setIsCorrect(0);
                }
            } else if ("多选题".equals(typeName)) {
                // 多选题：答案完全匹配才得分
                if (existingAnswer.getStudentAnswer() != null && 
                    existingAnswer.getStudentAnswer().equals(question.getAnswer())) {
                    existingAnswer.setGetScore(existingAnswer.getScore());
                    existingAnswer.setIsCorrect(1);
                    objectiveScore = objectiveScore.add(existingAnswer.getScore());
                } else {
                    existingAnswer.setGetScore(BigDecimal.ZERO);
                    existingAnswer.setIsCorrect(0);
                }
            } else if ("填空题".equals(typeName)) {
                // 填空题：答案匹配得分（可以支持多个答案）
                if (existingAnswer.getStudentAnswer() != null && 
                    existingAnswer.getStudentAnswer().trim().equals(question.getAnswer().trim())) {
                    existingAnswer.setGetScore(existingAnswer.getScore());
                    existingAnswer.setIsCorrect(1);
                    objectiveScore = objectiveScore.add(existingAnswer.getScore());
                } else {
                    existingAnswer.setGetScore(BigDecimal.ZERO);
                    existingAnswer.setIsCorrect(0);
                }
            } else {
                // 简答题：主观题，需要手动阅卷
                existingAnswer.setGetScore(BigDecimal.ZERO);
                existingAnswer.setIsCorrect(0);
            }
            
            studentAnswerMapper.update(existingAnswer);
        }
        
        // 更新考试记录
        record.setSubmitTime(LocalDateTime.now());
        record.setObjectiveScore(objectiveScore);
        record.setStatus(1); // 已提交
        examRecordMapper.update(record);
    }

    /**
     * 手动阅卷
     */
    @Transactional
    public void gradeExam(Long recordId, List<StudentAnswer> answers) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }
        
        BigDecimal subjectiveScore = BigDecimal.ZERO;
        
        for (StudentAnswer answer : answers) {
            studentAnswerMapper.update(answer);
            subjectiveScore = subjectiveScore.add(answer.getGetScore());
        }
        
        // 计算总分
        BigDecimal totalScore = record.getObjectiveScore().add(subjectiveScore);
        
        record.setSubjectiveScore(subjectiveScore);
        record.setTotalScore(totalScore);
        record.setStatus(2); // 已批改
        examRecordMapper.update(record);
        
        // 创建成绩记录
        Score score = new Score();
        score.setExamId(record.getExamId());
        score.setPaperId(record.getPaperId());
        score.setStudentId(record.getStudentId());
        score.setStudentName(record.getStudentName());
        score.setTotalScore(totalScore);
        score.setObjectiveScore(record.getObjectiveScore());
        score.setSubjectiveScore(subjectiveScore);
        score.setSubmitTime(record.getSubmitTime());
        scoreMapper.insert(score);
    }

    /**
     * 获取学生答案列表
     */
    public List<StudentAnswer> getStudentAnswers(Long recordId) {
        return studentAnswerMapper.selectByRecordId(recordId);
    }

    public void delete(Long id) {
        studentAnswerMapper.deleteByRecordId(id);
        examRecordMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            studentAnswerMapper.deleteByRecordId(id);
        }
        examRecordMapper.deleteBatch(ids);
    }
}