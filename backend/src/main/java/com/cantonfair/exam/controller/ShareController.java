package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Share;
import com.cantonfair.exam.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 交流分享Controller
 */
@RestController
@RequestMapping("/share")
public class ShareController {

    @Autowired
    private ShareService shareService;

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Share> getById(@PathVariable Long id) {
        Share share = shareService.getById(id);
        return Result.success(share);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Share>> getAll() {
        List<Share> shares = shareService.getAll();
        return Result.success(shares);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Share>> getByCondition(Share share) {
        List<Share> shares = shareService.getByCondition(share);
        return Result.success(shares);
    }

    /**
     * 根据学生ID查询
     */
    @GetMapping("/student/{studentId}")
    public Result<List<Share>> getByStudentId(@PathVariable Long studentId) {
        List<Share> shares = shareService.getByStudentId(studentId);
        return Result.success(shares);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Share share) {
        shareService.add(share);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody Share share) {
        shareService.update(share);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        shareService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        shareService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 浏览
     */
    @PostMapping("/view/{id}")
    public Result<Void> view(@PathVariable Long id) {
        shareService.view(id);
        return Result.success();
    }

    /**
     * 点赞
     */
    @PostMapping("/like/{id}")
    public Result<Void> like(@PathVariable Long id) {
        shareService.like(id);
        return Result.success();
    }
}