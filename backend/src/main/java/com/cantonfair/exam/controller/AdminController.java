package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Admin;
import com.cantonfair.exam.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员Controller
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Admin admin) {
        Map<String, Object> result = adminService.login(admin.getUsername(), admin.getPassword());
        return Result.success(result);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Admin> getById(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        return Result.success(admin);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Admin>> getAll() {
        List<Admin> admins = adminService.getAll();
        return Result.success(admins);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Admin>> getByCondition(Admin admin) {
        List<Admin> admins = adminService.getByCondition(admin);
        return Result.success(admins);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Admin admin) {
        adminService.add(admin);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody Admin admin) {
        adminService.update(admin);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        adminService.deleteBatch(ids);
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
        adminService.updatePassword(id, oldPassword, newPassword);
        return Result.success();
    }
}