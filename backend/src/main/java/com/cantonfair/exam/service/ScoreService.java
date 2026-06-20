package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.Score;
import com.cantonfair.exam.mapper.ScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 成绩Service
 */
@Service
public class ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    public Score getById(Long id) {
        return scoreMapper.selectById(id);
    }

    public List<Score> getAll() {
        return scoreMapper.selectAll();
    }

    public List<Score> getByCondition(Score score) {
        return scoreMapper.selectByCondition(score);
    }

    public List<Score> getByExamId(Long examId) {
        return scoreMapper.selectByExamId(examId);
    }

    public List<Score> getByStudentId(Long studentId) {
        return scoreMapper.selectByStudentId(studentId);
    }

    public Score getByExamAndStudent(Long examId, Long studentId) {
        return scoreMapper.selectByExamAndStudent(examId, studentId);
    }

    public void delete(Long id) {
        scoreMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        scoreMapper.deleteBatch(ids);
    }
}