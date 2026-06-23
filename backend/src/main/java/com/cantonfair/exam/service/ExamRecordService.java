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
            answer.setTitle(question.getTitle());
            answer.setOptionA(question.getOptionA());
            answer.setOptionB(question.getOptionB());
            answer.setOptionC(question.getOptionC());
            answer.setOptionD(question.getOptionD());
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
        BigDecimal objectiveScore = BigDecimal.ZERO;
        BigDecimal subjectiveScore = BigDecimal.ZERO;
        
        for (StudentAnswer answer : answers) {
            StudentAnswer existingAnswer = answerMap.get(answer.getQuestionId());
            if (existingAnswer == null) {
                continue;
            }
            
            Question question = questionMapper.selectById(answer.getQuestionId());
            if (question == null) {
                continue;
            }
            
            existingAnswer.setStudentAnswer(answer.getStudentAnswer());
            
            String typeName = question.getTypeName();
            if ("单选题".equals(typeName)) {
                if (existingAnswer.getStudentAnswer() != null && 
                    existingAnswer.getStudentAnswer().equals(question.getAnswer())) {
                    existingAnswer.setGetScore(existingAnswer.getScore());
                    existingAnswer.setIsCorrect(1);
                    objectiveScore = objectiveScore.add(existingAnswer.getScore());
                } else {
                    existingAnswer.setGetScore(BigDecimal.ZERO);
                    existingAnswer.setIsCorrect(0);
                }
            } else if ("判断题".equals(typeName)) {
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
                String studentAns = existingAnswer.getStudentAnswer();
                String correctAns = question.getAnswer();
                
                if (studentAns != null && studentAns.equals(correctAns)) {
                    existingAnswer.setGetScore(existingAnswer.getScore());
                    existingAnswer.setIsCorrect(1);
                    objectiveScore = objectiveScore.add(existingAnswer.getScore());
                } else if (studentAns != null && !studentAns.isEmpty()) {
                    int correctCount = 0;
                    int totalCorrectCount = correctAns.length();
                    
                    for (char c : studentAns.toCharArray()) {
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
            
            studentAnswerMapper.update(existingAnswer);
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
    public void gradeExam(Long recordId, List<StudentAnswer> answers) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }
        
        BigDecimal objectiveScore = BigDecimal.ZERO;
        BigDecimal subjectiveScore = BigDecimal.ZERO;
        
        for (StudentAnswer answer : answers) {
            StudentAnswer existingAnswer = studentAnswerMapper.selectById(answer.getId());
            if (existingAnswer != null) {
                existingAnswer.setGetScore(answer.getGetScore());
                existingAnswer.setComment(answer.getComment());
                studentAnswerMapper.update(existingAnswer);
                
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
        
        Score existingScore = scoreMapper.selectByExamAndStudent(record.getExamId(), record.getStudentId());
        if (existingScore != null) {
            existingScore.setTotalScore(totalScore);
            existingScore.setObjectiveScore(objectiveScore);
            existingScore.setSubjectiveScore(subjectiveScore);
            scoreMapper.update(existingScore);
        } else {
            Score score = new Score();
            score.setExamId(record.getExamId());
            score.setPaperId(record.getPaperId());
            score.setStudentId(record.getStudentId());
            score.setStudentName(record.getStudentName());
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