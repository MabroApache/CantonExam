package com.cantonfair.exam.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Paper implements Serializable {
    private Long id;
    private String name;
    private Long creatorId;
    private String creatorName;
    private Long departmentId;
    private String departmentName;
    private BigDecimal totalScore;
    private Integer duration;
    private Integer singleCount;
    private Integer multiCount;
    private Integer judgeCount;
    private Integer fillCount;
    private Integer essayCount;
    private BigDecimal singleScore;
    private BigDecimal multiScore;
    private BigDecimal judgeScore;
    private BigDecimal fillScore;
    private BigDecimal essayScore;
    private String description;
    private Integer status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}