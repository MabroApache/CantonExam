package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.Share;
import com.cantonfair.exam.mapper.ShareMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 交流分享Service
 */
@Service
public class ShareService {

    @Autowired
    private ShareMapper shareMapper;

    public Share getById(Long id) {
        return shareMapper.selectById(id);
    }

    public List<Share> getAll() {
        return shareMapper.selectAll();
    }

    public List<Share> getByCondition(Share share) {
        return shareMapper.selectByCondition(share);
    }

    public List<Share> getByStudentId(Long studentId) {
        return shareMapper.selectByStudentId(studentId);
    }

    public void add(Share share) {
        share.setViewCount(0);
        share.setLikeCount(0);
        share.setStatus(1);
        shareMapper.insert(share);
    }

    public void update(Share share) {
        shareMapper.update(share);
    }

    public void delete(Long id) {
        shareMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        shareMapper.deleteBatch(ids);
    }

    public void view(Long id) {
        shareMapper.updateViewCount(id);
    }

    public void like(Long id) {
        shareMapper.updateLikeCount(id);
    }
}