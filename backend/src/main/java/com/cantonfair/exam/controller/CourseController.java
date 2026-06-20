package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Course;
import com.cantonfair.exam.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程Controller
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        return Result.success(course);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Course>> getAll() {
        List<Course> courses = courseService.getAll();
        return Result.success(courses);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Course>> getByCondition(Course course) {
        List<Course> courses = courseService.getByCondition(course);
        return Result.success(courses);
    }

    /**
     * 根据教师ID查询
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<Course>> getByTeacherId(@PathVariable Long teacherId) {
        List<Course> courses = courseService.getByTeacherId(teacherId);
        return Result.success(courses);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Course course) {
        courseService.add(course);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody Course course) {
        courseService.update(course);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        courseService.deleteBatch(ids);
        return Result.success();
    }
}