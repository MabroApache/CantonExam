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

    public List<Score> getByCandidateId(Long CandidateId) {
        return scoreMapper.selectByCandidateId(CandidateId);
    }

    public Score getByExamAndCandidate(Long examId, Long CandidateId) {
        return scoreMapper.selectByExamAndCandidate(examId, CandidateId);
    }

    public void delete(Long id) {
        scoreMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        scoreMapper.deleteBatch(ids);
    }
}