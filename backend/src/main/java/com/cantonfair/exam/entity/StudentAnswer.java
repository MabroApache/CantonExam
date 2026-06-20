package com.cantonfair.exam.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生答案实体类
 */
@Data
public class StudentAnswer implements Serializable {
    private Long id;
    private Long recordId;
    private Long questionId;
    private Long typeId;
    private String typeName;
    private String studentAnswer;
    private String correctAnswer;
    private BigDecimal score;
    private BigDecimal getScore;
    private Integer isCorrect;
    private String comment;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}