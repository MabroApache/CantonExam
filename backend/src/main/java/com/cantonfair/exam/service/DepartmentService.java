package com.cantonfair.exam.service;

import com.cantonfair.exam.entity.Department;
import com.cantonfair.exam.mapper.DepartmentMapper;
import com.cantonfair.exam.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门Service
 */
@Service
public class DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    public Department getById(Long id) {
        return departmentMapper.selectById(id);
    }

    public List<Department> getAll() {
        return departmentMapper.selectAll();
    }

    public List<Department> getByCondition(Department department) {
        return departmentMapper.selectByCondition(department);
    }

    public void add(Department department) {
        Department existDepartment = departmentMapper.selectById(department.getId());
        if (existDepartment != null) {
            throw new BusinessException("部门已存在");
        }

        department.setStatus(1);
        departmentMapper.insert(department);
    }

    public void update(Department department) {
        departmentMapper.update(department);
    }

    public void delete(Long id) {
        departmentMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        departmentMapper.deleteBatch(ids);
    }
}