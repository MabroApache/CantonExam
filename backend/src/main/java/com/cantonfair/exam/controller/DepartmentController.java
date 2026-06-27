package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Department;
import com.cantonfair.exam.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门Controller
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        return Result.success(department);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Department>> getAll() {
        List<Department> departments = departmentService.getAll();
        return Result.success(departments);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Department>> getByCondition(Department department) {
        List<Department> departments = departmentService.getByCondition(department);
        return Result.success(departments);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Department department) {
        departmentService.add(department);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody Department department) {
        departmentService.update(department);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        departmentService.deleteBatch(ids);
        return Result.success();
    }
}