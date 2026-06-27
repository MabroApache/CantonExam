-- ========================================
-- 广交会在线考试系统数据库设计
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS cantonfair_exam DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE cantonfair_exam;

-- ========================================
-- 1. 管理员表
-- ========================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `name` VARCHAR(50) DEFAULT NULL COMMENT '姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 初始化管理员账号（密码：123456，使用MD5加密）
INSERT INTO `admin` (`username`, `password`, `name`, `phone`, `email`) 
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800138000', 'admin@cantonfair.com');

-- ========================================
-- 2. 部门表
-- ========================================
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '部门描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 初始化部门数据
INSERT INTO `department` (`name`, `description`) VALUES
('安全管理部', '负责展会安全管理工作'),
('展馆管理部', '负责展馆日常管理工作'),
('消防安全部', '负责消防安全相关工作'),
('应急管理部', '负责应急突发事件处理'),
('后勤保障部', '负责后勤保障工作'),
('信息化部', '负责信息化相关工作');


-- ========================================
-- 3. 出题人表
-- ========================================
DROP TABLE IF EXISTS `question_creator`;
CREATE TABLE `question_creator` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `name` VARCHAR(50) DEFAULT NULL COMMENT '姓名',
  `gender` TINYINT DEFAULT 1 COMMENT '性别：0-女，1-男',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `department_id` BIGINT NOT NULL COMMENT '所属部门ID',
  `department_name` VARCHAR(100) DEFAULT NULL COMMENT '所属部门名称',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出题人表';

-- ========================================
-- 4. 考生表
-- ========================================
DROP TABLE IF EXISTS `candidate`;
CREATE TABLE `candidate` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `name` VARCHAR(50) DEFAULT NULL COMMENT '姓名',
  `gender` TINYINT DEFAULT 1 COMMENT '性别：0-女，1-男',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考生表';

-- ========================================
-- 5. 题型表
-- ========================================
DROP TABLE IF EXISTS `question_type`;
CREATE TABLE `question_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '题型名称',
  `code` VARCHAR(20) NOT NULL COMMENT '题型编码：single-单选，multi-多选，judge-判断，fill-填空，essay-简答',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '题型描述',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题型表';

-- 初始化题型数据
INSERT INTO `question_type` (`name`, `code`, `description`, `sort`) VALUES
('单选题', 'single', '从多个选项中选择一个正确答案', 1),
('多选题', 'multi', '从多个选项中选择多个正确答案', 2),
('判断题', 'judge', '判断题目描述是否正确', 3),
('填空题', 'fill', '填写空白处的正确答案', 4),
('简答题', 'essay', '根据题目要求进行简答', 5);

-- ========================================
-- 6. 题库表
-- ========================================
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type_id` BIGINT NOT NULL COMMENT '题型ID',
  `type_name` VARCHAR(50) DEFAULT NULL COMMENT '题型名称',
  `creator_id` BIGINT NOT NULL COMMENT '出题人ID',
  `creator_name` VARCHAR(50) DEFAULT NULL COMMENT '出题人姓名',
  `department_id` BIGINT NOT NULL COMMENT '所属部门ID',
  `department_name` VARCHAR(100) DEFAULT NULL COMMENT '所属部门名称',
  `title` TEXT NOT NULL COMMENT '题目内容',
  `option_a` VARCHAR(500) DEFAULT NULL COMMENT '选项A',
  `option_b` VARCHAR(500) DEFAULT NULL COMMENT '选项B',
  `option_c` VARCHAR(500) DEFAULT NULL COMMENT '选项C',
  `option_d` VARCHAR(500) DEFAULT NULL COMMENT '选项D',
  `answer` TEXT NOT NULL COMMENT '正确答案',
  `analysis` TEXT COMMENT '答案解析',
  `tags` VARCHAR(255) DEFAULT NULL COMMENT '题目标签（多个标签用逗号分隔）',
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT '题目图片URL',
  `video_url` VARCHAR(500) DEFAULT NULL COMMENT '题目视频URL',
  `difficulty` TINYINT DEFAULT 1 COMMENT '难度：1-简单，2-中等，3-困难',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type_id` (`type_id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库表';

-- ========================================
-- 7. 试卷表
-- ========================================
DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '试卷名称',
  `creator_id` BIGINT NOT NULL COMMENT '出题人ID',
  `creator_name` VARCHAR(50) DEFAULT NULL COMMENT '出题人姓名',
  `department_id` BIGINT NOT NULL COMMENT '所属部门ID',
  `department_name` VARCHAR(100) DEFAULT NULL COMMENT '所属部门名称',
  `total_score` DECIMAL(5,1) DEFAULT 100 COMMENT '总分',
  `duration` INT DEFAULT 60 COMMENT '考试时长（分钟）',
  `single_count` INT DEFAULT 0 COMMENT '单选题数量',
  `single_score` DECIMAL(5,1) DEFAULT 0 COMMENT '单选题总分',
  `multi_count` INT DEFAULT 0 COMMENT '多选题数量',
  `multi_score` DECIMAL(5,1) DEFAULT 0 COMMENT '多选题总分',
  `judge_count` INT DEFAULT 0 COMMENT '判断题数量',
  `judge_score` DECIMAL(5,1) DEFAULT 0 COMMENT '判断题总分',
  `fill_count` INT DEFAULT 0 COMMENT '填空题数量',
  `fill_score` DECIMAL(5,1) DEFAULT 0 COMMENT '填空题总分',
  `essay_count` INT DEFAULT 0 COMMENT '简答题数量',
  `essay_score` DECIMAL(5,1) DEFAULT 0 COMMENT '简答题总分',
  `description` TEXT COMMENT '试卷说明',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-草稿，1-已发布',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- ========================================
-- 8. 试卷题目关联表
-- ========================================
DROP TABLE IF EXISTS `paper_question`;
CREATE TABLE `paper_question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
  `question_id` BIGINT NOT NULL COMMENT '题目ID',
  `score` DECIMAL(5,1) DEFAULT 0 COMMENT '题目分值',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联表';

-- ========================================
-- 9. 考试安排表
-- ========================================
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '考试名称',
  `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
  `paper_name` VARCHAR(100) DEFAULT NULL COMMENT '试卷名称',
  `creator_id` BIGINT NOT NULL COMMENT '出题人ID',
  `creator_name` VARCHAR(50) DEFAULT NULL COMMENT '出题人姓名',
  `department_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
  `department_name` VARCHAR(100) DEFAULT NULL COMMENT '所属部门名称',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `duration` INT DEFAULT 60 COMMENT '考试时长（分钟）',
  `total_score` DECIMAL(5,1) DEFAULT 100 COMMENT '总分',
  `description` TEXT COMMENT '考试说明',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-未开始，1-进行中，2-已结束',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试安排表';

-- ========================================
-- 10. 考试记录表
-- ========================================
DROP TABLE IF EXISTS `exam_record`;
CREATE TABLE `exam_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_id` BIGINT NOT NULL COMMENT '考试ID',
  `exam_name` VARCHAR(100) DEFAULT NULL COMMENT '考试名称',
  `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
  `paper_name` VARCHAR(100) DEFAULT NULL COMMENT '试卷名称',
  `candidate_id` BIGINT NOT NULL COMMENT '考生ID',
  `candidate_name` VARCHAR(50) DEFAULT NULL COMMENT '考生姓名',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始答题时间',
  `submit_time` DATETIME DEFAULT NULL COMMENT '提交时间',
  `total_score` DECIMAL(5,1) DEFAULT 0 COMMENT '总分',
  `objective_score` DECIMAL(5,1) DEFAULT 0 COMMENT '客观题得分',
  `subjective_score` DECIMAL(5,1) DEFAULT 0 COMMENT '主观题得分',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-未提交，1-已提交，2-已批改',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_candidate_id` (`candidate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- ========================================
-- 11. 考生答案表
-- ========================================
DROP TABLE IF EXISTS `candidate_answer`;
CREATE TABLE `candidate_answer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` BIGINT NOT NULL COMMENT '考试记录ID',
  `question_id` BIGINT NOT NULL COMMENT '题目ID',
  `type_id` BIGINT NOT NULL COMMENT '题型ID',
  `type_name` VARCHAR(50) DEFAULT NULL COMMENT '题型名称',
  `candidate_answer` TEXT COMMENT '考生答案',
  `correct_answer` TEXT COMMENT '正确答案',
  `score` DECIMAL(5,1) DEFAULT 0 COMMENT '题目分值',
  `get_score` DECIMAL(5,1) DEFAULT 0 COMMENT '得分',
  `is_correct` TINYINT DEFAULT 0 COMMENT '是否正确：0-错误，1-正确，2-部分正确',
  `comment` TEXT COMMENT '批注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_record_id` (`record_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考生答案表';

-- ========================================
-- 12. 成绩表
-- ========================================
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_id` BIGINT NOT NULL COMMENT '考试ID',
  `exam_name` VARCHAR(100) DEFAULT NULL COMMENT '考试名称',
  `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
  `paper_name` VARCHAR(100) DEFAULT NULL COMMENT '试卷名称',
  `candidate_id` BIGINT NOT NULL COMMENT '考生ID',
  `candidate_name` VARCHAR(50) DEFAULT NULL COMMENT '考生姓名',
  `total_score` DECIMAL(5,1) DEFAULT 0 COMMENT '总分',
  `objective_score` DECIMAL(5,1) DEFAULT 0 COMMENT '客观题得分',
  `subjective_score` DECIMAL(5,1) DEFAULT 0 COMMENT '主观题得分',
  `rank` INT DEFAULT NULL COMMENT '排名',
  `submit_time` DATETIME DEFAULT NULL COMMENT '提交时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_candidate_id` (`candidate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- ========================================
-- 数据库创建完成
-- ========================================
SELECT '数据库创建完成！' AS message;