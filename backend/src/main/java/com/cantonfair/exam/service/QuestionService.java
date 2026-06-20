package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.Question;
import com.cantonfair.exam.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 题库Service
 */
@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    public Question getById(Long id) {
        return questionMapper.selectById(id);
    }

    public List<Question> getAll() {
        return questionMapper.selectAll();
    }

    public List<Question> getByCondition(Question question) {
        return questionMapper.selectByCondition(question);
    }

    public List<Question> getByCourseId(Long courseId) {
        return questionMapper.selectByCourseId(courseId);
    }

    public List<Question> getByTypeId(Long typeId) {
        return questionMapper.selectByTypeId(typeId);
    }

    public List<Question> getByTeacherId(Long teacherId) {
        return questionMapper.selectByTeacherId(teacherId);
    }

    /**
     * 随机抽取题目（用于自动组卷）
     */
    public List<Question> getRandomQuestions(Long courseId, Long typeId, Integer count) {
        return questionMapper.selectRandomQuestions(courseId, typeId, count);
    }

    public void add(Question question) {
        question.setStatus(1);
        questionMapper.insert(question);
    }

    public void update(Question question) {
        questionMapper.update(question);
    }

    public void delete(Long id) {
        questionMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        questionMapper.deleteBatch(ids);
    }
}