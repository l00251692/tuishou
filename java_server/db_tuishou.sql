/*
Navicat MySQL Data Transfer

Source Server         : 本地连接
Source Server Version : 50721
Source Host           : localhost:3306
Source Database       : db_tuishou

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2019-10-21 20:24:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for collect
-- ----------------------------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect` (
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `project_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `up_user_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `status` int(11) DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `files` text COLLATE utf8mb4_unicode_ci,
  `record` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`project_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text CHARACTER SET utf8,
  `imgs` text COLLATE utf8mb4_unicode_ci,
  `phone` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
  `project_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `follower_id` varchar(64) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `project_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '',
  `rule` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `salary` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
  `contact` varchar(64) CHARACTER SET utf8 DEFAULT '',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_userid` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `head_img` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `start_time` date DEFAULT NULL,
  `deadline_time` date DEFAULT NULL,
  `region` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `add_imgs` text CHARACTER SET utf8 COLLATE utf8_bin,
  `follow` int(10) DEFAULT NULL,
  `status` int(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` varchar(64) NOT NULL,
  `phone` varchar(64) DEFAULT '',
  `type` smallint(6) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `last_login_date` date DEFAULT NULL,
  `sex` smallint(1) DEFAULT NULL,
  `latitude` varchar(64) DEFAULT '',
  `longitude` varchar(64) DEFAULT '',
  `province` varchar(32) DEFAULT '',
  `city` varchar(32) DEFAULT '',
  `district` varchar(32) DEFAULT '',
  `address` varchar(65) DEFAULT NULL,
  `balance` float(10,2) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for withdraw
-- ----------------------------
DROP TABLE IF EXISTS `withdraw`;
CREATE TABLE `withdraw` (
  `id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `apply_user_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `money` float DEFAULT NULL,
  `apply_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `status` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- View structure for follower_project
-- ----------------------------
DROP VIEW IF EXISTS `follower_project`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `follower_project` AS select `follow`.`follower_id` AS `follower_id`,`project`.`project_id` AS `project_id`,`project`.`type` AS `type`,`project`.`title` AS `title`,`project`.`salary` AS `salary`,`project`.`create_time` AS `create_time`,`project`.`create_userid` AS `create_userid`,`project`.`deadline_time` AS `deadline_time`,`project`.`rule` AS `rule`,`project`.`region` AS `region` from (`follow` left join `project` on((`follow`.`project_id` = `project`.`project_id`))) where ((`project`.`project_id` <> 0) and (`project`.`status` <> 0)) ;
