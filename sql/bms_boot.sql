/*
 Navicat Premium Data Transfer

 Source Server         : zty_mysql
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : localhost:3306
 Source Schema         : bms_boot

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : 65001

 Date: 15/03/2023 19:46:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_admins
-- ----------------------------
DROP TABLE IF EXISTS `t_admins`;
CREATE TABLE `t_admins`  (
  `admin_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员表的唯一标识',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码(MD5加密)',
  `admin_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '管理员真实姓名',
  `status` int(1) NOT NULL COMMENT '1表示可用 0表示禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`admin_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1624 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_book_admins
-- ----------------------------
DROP TABLE IF EXISTS `t_book_admins`;
CREATE TABLE `t_book_admins`  (
  `book_admin_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '图书管理员表的唯一标识',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码md5加密',
  `book_admin_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图书管理员真实姓名',
  `status` int(1) NOT NULL COMMENT '1表示可用 0表示禁用',
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电子邮箱',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`book_admin_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1546 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_book_rule
-- ----------------------------
DROP TABLE IF EXISTS `t_book_rule`;
CREATE TABLE `t_book_rule`  (
  `rule_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '借阅规则记录的唯一标识',
  `book_rule_id` int(11) NOT NULL COMMENT '借阅规则编号',
  `book_days` int(11) NOT NULL COMMENT '借阅天数',
  `book_limit_number` int(11) NOT NULL COMMENT '限制借阅的本数',
  `book_limit_library` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '限制的图书馆',
  `book_overdue_fee` double NOT NULL COMMENT '图书借阅后每天逾期费用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`rule_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_book_type
-- ----------------------------
DROP TABLE IF EXISTS `t_book_type`;
CREATE TABLE `t_book_type`  (
  `type_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '图书类别唯一标识',
  `type_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '借阅类别的昵称',
  `type_content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '借阅类别的描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_books
-- ----------------------------
DROP TABLE IF EXISTS `t_books`;
CREATE TABLE `t_books`  (
  `book_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '图书表唯一标识',
  `book_number` bigint(11) NOT NULL COMMENT '图书编号 图书的唯一标识',
  `book_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图书名称',
  `book_author` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图书作者',
  `book_library` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图书所在图书馆名称',
  `book_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图书类别',
  `book_location` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图书位置',
  `book_status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图书状态',
  `book_description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图书描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`book_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_books_borrow
-- ----------------------------
DROP TABLE IF EXISTS `t_books_borrow`;
CREATE TABLE `t_books_borrow`  (
  `borrow_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '借阅表唯一标识',
  `card_number` bigint(11) NOT NULL COMMENT '借阅证编号 固定11位随机生成 用户和图书关联的唯一标识',
  `book_number` int(11) NOT NULL COMMENT '图书编号 图书唯一标识',
  `borrow_date` datetime NOT NULL COMMENT '借阅日期',
  `close_date` datetime NOT NULL COMMENT '截止日期',
  `return_date` datetime NULL DEFAULT NULL COMMENT '归还日期',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`borrow_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment`  (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '留言表唯一标识',
  `comment_avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '留言的头像 链接',
  `comment_barrage_style` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '弹幕的高度(样式)',
  `comment_message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '弹幕的内容',
  `comment_time` int(11) NOT NULL COMMENT '留言的时间(控制速度)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_notice
-- ----------------------------
DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice`  (
  `notice_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '公告表唯一标识',
  `notice_title` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公告题目',
  `notice_content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公告内容',
  `notice_admin_id` int(11) NOT NULL COMMENT '发布公告的管理员id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_users
-- ----------------------------
DROP TABLE IF EXISTS `t_users`;
CREATE TABLE `t_users`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户表的唯一标识',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码 MD5加密',
  `card_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '真实姓名',
  `card_number` bigint(11) NOT NULL COMMENT '借阅证编号 固定11位随机生成 非空',
  `rule_number` int(11) NOT NULL COMMENT '规则编号 可以自定义也就是权限功能',
  `status` int(1) NOT NULL COMMENT '1表示可用 0表示禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2544 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_violation
-- ----------------------------
DROP TABLE IF EXISTS `t_violation`;
CREATE TABLE `t_violation`  (
  `violation_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '违章表唯一标识',
  `card_number` bigint(11) NOT NULL COMMENT '借阅证编号 11位 随机生成',
  `book_number` int(11) NOT NULL COMMENT '图书编号 图书唯一标识',
  `borrow_date` datetime NOT NULL COMMENT '借阅日期',
  `close_date` datetime NOT NULL COMMENT '截止日期',
  `return_date` datetime NULL DEFAULT NULL COMMENT '归还日期',
  `violation_message` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '违章信息',
  `violation_admin_id` int(11) NOT NULL COMMENT '违章信息管理员的id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`violation_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
