package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Student;
import com.cantonfair.exam.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生Controller
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Student student) {
        Map<String, Object> result = studentService.login(student.getUsername(), student.getPassword());
        return Result.success(result);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Student student) {
        studentService.register(student);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Student>> getAll() {
        List<Student> students = studentService.getAll();
        return Result.success(students);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Student>> getByCondition(Student student) {
        List<Student> students = studentService.getByCondition(student);
        return Result.success(students);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Student student) {
        studentService.add(student);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody Student student) {
        studentService.update(student);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        studentService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 审核学生
     */
    @PostMapping("/audit")
    public Result<Void> audit(@RequestBody Map<String, Object> params) {
        Long id = Long.parseLong(params.get("id").toString());
        Integer status = Integer.parseInt(params.get("status").toString());
        studentService.audit(id, status);
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
        studentService.updatePassword(id, oldPassword, newPassword);
        return Result.success();
    }
}