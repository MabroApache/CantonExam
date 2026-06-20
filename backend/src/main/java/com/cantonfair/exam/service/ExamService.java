package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.Exam;
import com.cantonfair.exam.mapper.ExamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 考试安排Service
 */
@Service
public class ExamService {

    @Autowired
    private ExamMapper examMapper;

    public Exam getById(Long id) {
        return examMapper.selectById(id);
    }

    public List<Exam> getAll() {
        return examMapper.selectAll();
    }

    public List<Exam> getByCondition(Exam exam) {
        return examMapper.selectByCondition(exam);
    }

    public List<Exam> getByStudentId(Long studentId) {
        return examMapper.selectByStudentId(studentId);
    }

    public void add(Exam exam) {
        exam.setStatus(0); // 未开始
        examMapper.insert(exam);
    }

    public void update(Exam exam) {
        examMapper.update(exam);
    }

    public void delete(Long id) {
        examMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        examMapper.deleteBatch(ids);
    }
}