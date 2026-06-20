package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.Notice;
import com.cantonfair.exam.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统公告Service
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    public Notice getById(Long id) {
        return noticeMapper.selectById(id);
    }

    public List<Notice> getAll() {
        return noticeMapper.selectAll();
    }

    public List<Notice> getByCondition(Notice notice) {
        return noticeMapper.selectByCondition(notice);
    }

    public List<Notice> getPublished() {
        return noticeMapper.selectPublished();
    }

    public void add(Notice notice) {
        noticeMapper.insert(notice);
    }

    public void publish(Long id, String publisher) {
        Notice notice = noticeMapper.selectById(id);
        if (notice != null) {
            notice.setStatus(1);
            notice.setPublisher(publisher);
            notice.setPublishTime(LocalDateTime.now());
            noticeMapper.update(notice);
        }
    }

    public void update(Notice notice) {
        noticeMapper.update(notice);
    }

    public void delete(Long id) {
        noticeMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        noticeMapper.deleteBatch(ids);
    }
}