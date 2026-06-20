package com.cantonfair.exam.controller;

import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.entity.Notice;
import com.cantonfair.exam.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统公告Controller
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        return Result.success(notice);
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<Notice>> getAll() {
        List<Notice> notices = noticeService.getAll();
        return Result.success(notices);
    }

    /**
     * 条件查询
     */
    @GetMapping("/search")
    public Result<List<Notice>> getByCondition(Notice notice) {
        List<Notice> notices = noticeService.getByCondition(notice);
        return Result.success(notices);
    }

    /**
     * 查询已发布的公告
     */
    @GetMapping("/published")
    public Result<List<Notice>> getPublished() {
        List<Notice> notices = noticeService.getPublished();
        return Result.success(notices);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Notice notice) {
        noticeService.add(notice);
        return Result.success();
    }

    /**
     * 发布公告
     */
    @PostMapping("/publish")
    public Result<Void> publish(@RequestBody Map<String, Object> params) {
        Long id = Long.parseLong(params.get("id").toString());
        String publisher = (String) params.get("publisher");
        noticeService.publish(id, publisher);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody Notice notice) {
        noticeService.update(notice);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        noticeService.deleteBatch(ids);
        return Result.success();
    }
}