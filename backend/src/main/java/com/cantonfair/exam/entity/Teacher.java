package com.cantonfair.exam.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师实体类
 */
@Data
public class Teacher implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private String avatar;
    private String department;
    private String title;
    private Integer status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}