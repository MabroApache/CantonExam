package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.QuestionType;
import com.cantonfair.exam.mapper.QuestionTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 题型Service
 */
@Service
public class QuestionTypeService {

    @Autowired
    private QuestionTypeMapper questionTypeMapper;

    public QuestionType getById(Long id) {
        return questionTypeMapper.selectById(id);
    }

    public QuestionType getByCode(String code) {
        return questionTypeMapper.selectByCode(code);
    }

    public List<QuestionType> getAll() {
        return questionTypeMapper.selectAll();
    }

    public List<QuestionType> getByCondition(QuestionType questionType) {
        return questionTypeMapper.selectByCondition(questionType);
    }

    public void add(QuestionType questionType) {
        questionType.setStatus(1);
        questionTypeMapper.insert(questionType);
    }

    public void update(QuestionType questionType) {
        questionTypeMapper.update(questionType);
    }

    public void delete(Long id) {
        questionTypeMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        questionTypeMapper.deleteBatch(ids);
    }
}