package com.cantonfair.exam.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考生答案实体类
 */
@Data
public class CandidateAnswer implements Serializable {
    private Long id;
    private Long recordId;
    private Long questionId;
    private Long typeId;
    private String typeName;
    private String title;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String imageUrl;
    private String candidateAnswer;
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