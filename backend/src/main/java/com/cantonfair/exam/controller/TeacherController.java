package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Teacher;
import com.cantonfair.exam.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师Controller
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Teacher teacher) {
        Map<String, Object> result = teacherService.login(teacher.getUsername(), teacher.getPassword());
        return Result.success(result);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Teacher teacher) {
        teacherService.register(teacher);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Teacher> getById(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        return Result.success(teacher);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Teacher>> getAll() {
        List<Teacher> teachers = teacherService.getAll();
        return Result.success(teachers);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Teacher>> getByCondition(Teacher teacher) {
        List<Teacher> teachers = teacherService.getByCondition(teacher);
        return Result.success(teachers);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Teacher teacher) {
        teacherService.add(teacher);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody Teacher teacher) {
        teacherService.update(teacher);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        teacherService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        Long id = Long.parseLong(params.get("id"));
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        teacherService.updatePassword(id, oldPassword, newPassword);
        return Result.success();
    }
}