package com.cantonfair.exam.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Question implements Serializable {
    private Long id;
    private Long typeId;
    private String typeName;
    private Long creatorId;
    private String creatorName;
    private Long departmentId;
    private String departmentName;
    private String title;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String analysis;
    private Object tags;
    private String imageUrl;
    private String videoUrl;
    private Integer difficulty;
    private Integer status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    public String getTags() {
        if (tags instanceof List) {
            try {
                return new ObjectMapper().writeValueAsString(tags);
            } catch (JsonProcessingException e) {
                return "";
            }
        }
        return tags != null ? tags.toString() : null;
    }
    
    public void setTags(Object tags) {
        this.tags = tags;
    }
}