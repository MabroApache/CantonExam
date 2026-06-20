package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.Course;
import com.cantonfair.exam.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程Service
 */
@Service
public class CourseService {

    @Autowired
    private CourseMapper courseMapper;

    public Course getById(Long id) {
        return courseMapper.selectById(id);
    }

    public List<Course> getAll() {
        return courseMapper.selectAll();
    }

    public List<Course> getByCondition(Course course) {
        return courseMapper.selectByCondition(course);
    }

    public List<Course> getByTeacherId(Long teacherId) {
        return courseMapper.selectByTeacherId(teacherId);
    }

    public void add(Course course) {
        course.setStatus(1);
        courseMapper.insert(course);
    }

    public void update(Course course) {
        courseMapper.update(course);
    }

    public void delete(Long id) {
        courseMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        courseMapper.deleteBatch(ids);
    }
}