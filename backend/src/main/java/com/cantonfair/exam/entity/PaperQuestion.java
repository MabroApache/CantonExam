package com.cantonfair.exam.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 试卷题目关联实体类
 */
@Data
public class PaperQuestion implements Serializable {
    private Long id;
    private Long paperId;
    private Long questionId;
    private BigDecimal score;
    private Integer sort;
    
    // 额外字段：题目类型
    private Long typeId;
    private String typeName;
    
    // 额外字段：题目内容（用于查看试卷详情）
    private String title;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String analysis;
    private String tags;
    private String imageUrl;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}