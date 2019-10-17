/*
Navicat MySQL Data Transfer

Source Server         : 本地连接
Source Server Version : 50721
Source Host           : localhost:3306
Source Database       : db_medical

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2018-09-20 13:41:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for hospital
-- ----------------------------
DROP TABLE IF EXISTS `hospital`;
CREATE TABLE `hospital` (
  `province` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hospital` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for oa_employee
-- ----------------------------
DROP TABLE IF EXISTS `oa_employee`;
CREATE TABLE `oa_employee` (
  `phone` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` smallint(6) DEFAULT NULL,
  `authority` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_login_date` date DEFAULT NULL,
  PRIMARY KEY (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `order_id` varchar(64) NOT NULL,
  `create_user` varchar(64) NOT NULL,
  `create_time` datetime(6) DEFAULT '0000-00-00 00:00:00.000000',
  `last_update_time` datetime DEFAULT NULL,
  `order_status` smallint(6) DEFAULT NULL,
  `records` text,
  `name` varchar(16) DEFAULT NULL,
  `sex` varchar(10) DEFAULT NULL,
  `idcard` varchar(64) DEFAULT NULL,
  `hospital` varchar(64) CHARACTER SET gb2312 DEFAULT NULL,
  `hospital_area` varchar(32) DEFAULT NULL,
  `mrNo` varchar(64) DEFAULT NULL,
  `department` varchar(64) DEFAULT NULL,
  `doctor` varchar(64) DEFAULT NULL,
  `bedNo` varchar(64) DEFAULT NULL COMMENT '留言',
  `diseases` varchar(255) DEFAULT NULL,
  `out_date` varchar(64) DEFAULT NULL,
  `address` text,
  `detail` varchar(64) DEFAULT NULL,
  `provice` varchar(64) DEFAULT NULL,
  `city` varchar(64) DEFAULT NULL,
  `district` varchar(64) DEFAULT '0000-00-00 00:00:00.000000',
  `adr_title` varchar(64) DEFAULT '0',
  `phone` varchar(32) DEFAULT NULL,
  `concat_name` varchar(16) DEFAULT NULL,
  `concat_phone` varchar(32) DEFAULT NULL,
  `idcard_front` varchar(255) DEFAULT NULL,
  `idcard_back` varchar(255) DEFAULT NULL,
  `deliveryNo` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`order_id`,`create_user`),
  KEY `create_time_index` (`hospital`),
  KEY `campus_order` (`idcard`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` varchar(64) NOT NULL,
  `phone` varchar(64) DEFAULT NULL,
  `type` smallint(6) DEFAULT NULL,
  `create_time` date NOT NULL COMMENT '注册时间',
  `nickname` varchar(255) DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `last_login_date` date DEFAULT NULL,
  `sex` smallint(1) DEFAULT NULL,
  `academy` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Event structure for delteOrders
-- ----------------------------
DROP EVENT IF EXISTS `delteOrders`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` EVENT `delteOrders` ON SCHEDULE EVERY 1 DAY STARTS '2018-09-20 04:00:01' ON COMPLETION NOT PRESERVE ENABLE DO DELETE
FROM
	orders
WHERE
	order_status = 9
AND DATE_SUB(CURDATE(), INTERVAL 3 DAY) > date(last_update_time)
;;
DELIMITER ;
