/*
Navicat MySQL Data Transfer

Source Server         : cd-cdb-9lxi49zi.sql.tencentcdb.com
Source Server Version : 50718
Source Host           : cd-cdb-9lxi49zi.sql.tencentcdb.com:62760
Source Database       : db_tuishou

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2019-11-17 21:41:33
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
  `name` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `phone` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `files` text COLLATE utf8mb4_unicode_ci,
  `record` text COLLATE utf8mb4_unicode_ci,
  `latitude` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `longitude` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ad_detail` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`project_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of collect
-- ----------------------------
INSERT INTO `collect` VALUES ('1572867059118', '1572850054565', 'oEAS25B9GOX0g7zY3mv9VIqfzaSE', '2019-11-04 19:31:00', '1', '施晶晶', '13951717977', '测试，加油！', '[\"http://wtoer.com/prj_1572850054565collect_1572867059118_3adb5174862e5741b7e750fcf0991d3.jpg\"]', '[{\"status\":1,\"time\":1572867059118}]', null, null, null, null);

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
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES ('1572451821256', 'oEAS25DCy259ihLHN24lMFPD7VbY', '2019-10-31 00:00:00', '优化建议', '操作不方便', null, '');

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
  `project_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `follower_id` varchar(64) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of follow
-- ----------------------------
INSERT INTO `follow` VALUES ('1572849127503', 'oEAS25Lre-vdJS91ZAtEGFY6PI7A');
INSERT INTO `follow` VALUES ('1572850054565', 'oEAS25AmHL10yqko6lgBTaldNULA');
INSERT INTO `follow` VALUES ('1572850054565', 'oEAS25Lre-vdJS91ZAtEGFY6PI7A');
INSERT INTO `follow` VALUES ('1572850054565', 'oEAS25B9GOX0g7zY3mv9VIqfzaSE');
INSERT INTO `follow` VALUES ('1572850054565', 'oEAS25DCy259ihLHN24lMFPD7VbY');
INSERT INTO `follow` VALUES ('1572850054565', 'oEAS25EjE-i0jy17sUytZw-JJkVE');
INSERT INTO `follow` VALUES ('1572850054565', 'oEAS25CsTmOE4ByxgNJM8oXyRbGY');
INSERT INTO `follow` VALUES ('1573979712163', 'oEAS25Lre-vdJS91ZAtEGFY6PI7A');
INSERT INTO `follow` VALUES ('1573979712163', 'oEAS25Du5AecDOboaP-S8GuPGxbo');
INSERT INTO `follow` VALUES ('1573979712163', 'oEAS25KJiZ7IUg-K3F_IV3d4_STE');
INSERT INTO `follow` VALUES ('1573979712163', 'oEAS25EPbv8P-F_hX8qFIa3tfhYo');
INSERT INTO `follow` VALUES ('1573979712163', 'oEAS25DCy259ihLHN24lMFPD7VbY');
INSERT INTO `follow` VALUES ('1573979712163', 'oEAS25B9GOX0g7zY3mv9VIqfzaSE');

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
  `count` int(64) DEFAULT '0',
  `link` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `follow` int(10) DEFAULT NULL,
  `status` int(10) DEFAULT NULL,
  `qr_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES ('1573979712163', '推手任务', '链信app注册推广', '1. 通过链接手机号注册APP；\n2.下载app并实名，人脑验证；\n3. 动态栏点5个赞，并在“我的”-“知识问答”回答5个问题，领取CCT币；\n4. 截图我的页面上传到推手号，联系微信：tsh-006 审核及发送收益红包', '链信APP推广', '10元/单', 'tsh-006', '2019-11-17 16:35:22', 'oEAS25Lre-vdJS91ZAtEGFY6PI7A', 'https://wtoer.com/prj_1573979712163', '2019-11-17', '2019-12-31', '全国', '', '2000', 'https://lianxin.quarkblockchain.cn/register?code=prrUoS', '0', '1', 'https://wtoer.com/Fr4GdPMXc4se2kAEtBqgaPcClU5j');

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
  `from_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT '',
  `from_project_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT '',
  `from_records` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('oEAS25A5ZFROv4aDF3msvHHm6Tuw', '', '1', '陈碧天', 'https://wx.qlogo.cn/mmhead/Yr1LMYX6KTaOlic5qThibhniaX1T6hiciaLYsFhy1tIC8UQw/132', '2019-11-02', '0', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25AmHL10yqko6lgBTaldNULA', '18162600512', '1', '肖平 | 学会工作室', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83epbRXWAgsmQYWqVLVenDxVbLYVlHLS0ldicOu9MW4S5yyK2Rj1Dqna9iaaFzjUcPiao5cYxIDf9OMUNg/132', '2019-11-04', '1', '30.50448989868164', '114.39932250976562', '湖北省', '武汉市', '洪山区', '光谷资本大厦', '0.00');
INSERT INTO `users` VALUES ('oEAS25B7O-fvxu5dDbJCA_Ry-5bQ', '', '1', '金鑫', 'https://wx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEKDv9KukxZ09z1CzujunhuFvwhYPdgusibx7iasrq4ic0o2XtM6LOsSWV2DsHI4aUZficdjLnU7v00Ejg/132', '2019-11-02', '1', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25B9GOX0g7zY3mv9VIqfzaSE', '13951717977', '1', '施晶晶', 'https://wx.qlogo.cn/mmopen/vi_32/PiajxSqBRaELAibbD0B5M1vk5ib1LdBicRRg5WbrohiafSbiciaWQItckJk2CVxuIK1kRQiaibmiamAoUib9oCBzan99bwvTw/132', '2019-11-02', '1', '32.08604', '118.909775', '江苏省', '南京市', '玄武区', '仙龙湾山庄&#40;南京市玄武区仙隐南路2号)', '0.00');
INSERT INTO `users` VALUES ('oEAS25BYd_WpsvdqeJ8dcou8YTiM', '', '1', '陈美慧', 'https://wx.qlogo.cn/mmhead/TNoNRuDmoGuGCibZQV9BAXaHAhsmUSicI4icu9dJlwFdoU/132', '2019-11-11', '0', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25CsTmOE4ByxgNJM8oXyRbGY', '', '1', '同仁', 'https://wx.qlogo.cn/mmopen/vi_32/iasoo0H80GzoAX55UJhQgLrFdA1DsFUgBfEwGCfdcVOiaXIDt0iasy7slXrR53E7jfuY2ejhvwBfoMarxMS3tniazQ/132', '2019-11-08', '1', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25DCy259ihLHN24lMFPD7VbY', '15895903085', '1', 'xxx、defined', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83epibEleO1FPiaLueU1VhBiaHKmKxLJNvvXWXSomaDVvLURzyE6ZBSoEzib7gXckiavVuiadG0VdSFalkCrA/132', '2019-11-17', '1', '31.99161', '118.7791', '江苏省', '南京市', '雨花台区', '南京市雨花台区人民政府', '100.00');
INSERT INTO `users` VALUES ('oEAS25Du5AecDOboaP-S8GuPGxbo', '18827661105', '1', 'Leila', 'https://wx.qlogo.cn/mmopen/vi_32/5TYqM7pBXYmfAOXERDhu1QenchtEk2pfgCria1djAeMW7LFB96JBQNPfAmfqU2NJqQUG3XHq3KoVCsgcJry1E6Q/132', '2019-11-17', '2', '30.431559', '114.43907', '湖北省', '武汉市', '江夏区', '天纵·水晶郦都&#40;武汉市江夏区)', '0.00');
INSERT INTO `users` VALUES ('oEAS25E0YTciLddz3W0yu8t-rRDE', '', '1', '陈淑媛', 'https://wx.qlogo.cn/mmhead/rneMlzCiabb4AnIG0ibqGC9PwRrf0PSdSpFQehXNwHq0I/132', '2019-11-03', '0', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25EFjXzcvYg0zLVQODhRaRek', '', '1', '满天星', 'https://wx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEKCib8UQlvYbPImVU1kuARslHBgJHZPxiboCT2icICc3ibzW0Zw0jr4V435cXLCaVqShNbDDOnhPkdAOw/132', '2019-10-31', '2', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25EjE-i0jy17sUytZw-JJkVE', '', '1', 'nick', 'https://wx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEJekndyMUq7ibypf3VSJ9bgtfKiadia7NXo5pVskPmIXenbaKficvZsRqVYoFrpiasIZ4pIqwC1NdMtzlg/132', '2019-11-07', '2', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25EPbv8P-F_hX8qFIa3tfhYo', '15895903085', '1', 'SunAlsoRises', 'https://wx.qlogo.cn/mmopen/vi_32/F1cV6tw2ticn8yvdPUKx3UvIpYAPLFInDG7tzpGibAI7rEmQfvPFA7b9tazQsibjAMIAPHWO4KmpK4JCARRvukUjw/132', '2019-11-17', '1', '31.970541', '118.75715', '江苏省', '南京市', '雨花台区', '凤翔新城一期&#40;南京市雨花台区)', '0.00');
INSERT INTO `users` VALUES ('oEAS25InaYWsl0dW0OCkR4zgyQ74', '', '1', '华夏～人保.颜艳萍', 'https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKhkGrbic9Ah14Qm8v2AAGgcR6PNVWIwIv3V0wJeh12ltTPic1sCEibQLkSEDjXMloSVRVia3nrU041iaQ/132', '2019-11-17', '2', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25JeSFSrq0ut32XWQ_r2AvoI', '', '1', 'Ss Yy', 'https://wx.qlogo.cn/mmopen/vi_32/ajNVdqHZLLAFtoLl2IfLwem3LxW8gzXPwzxSyHPjplNNuiaohAZtXntrQGURPwcK4ZISK69fHZrhiaUk1YCHL0Qw/132', '2019-10-28', '2', '', '', '', '', '', 'null', '0.00');
INSERT INTO `users` VALUES ('oEAS25KJiZ7IUg-K3F_IV3d4_STE', '18727845540', '1', 'yan', 'https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTK3h8r4JnicOmPt2SJPPAZY8RZ5s4cjtutAwpRg7c8ULGfsra5O6gs2blpuYfFuJpicWL7cicJjcPicnw/132', '2019-11-17', '0', '30.431791', '114.43918', '湖北省', '武汉市', '江夏区', '天纵·水晶郦都&#40;武汉市江夏区)', '0.00');
INSERT INTO `users` VALUES ('oEAS25Ktky_EenbJGib00r2SE9dE', '', '1', '薛雨阳', 'https://wx.qlogo.cn/mmopen/vi_32/nTfF1SpMrW5fmUXBqFaId07jichltkib8ean0qurV5mLCqHxoXah3sFJQQq6FVMwT5Oib7wH2KEUiaQmN9mbzhrSew/132', '2019-11-05', '1', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25L2X19pKQxIVuYF43qL8RSc', '', '1', '李俊映', 'https://wx.qlogo.cn/mmhead/DSqwErSlwibicWYFluf5xj6JyRugkEuY9T0LHR3F4Bialk/132', '2019-11-03', '0', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25LKooBDffXvSJubLvhrXQNM', '', '1', '辛福', 'https://wx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEKDv9KukxZ09z1CzujunhuF35ic7hEvLiaFAFU4x5SayL0meOxEfAKfnibHElxsUBG43vZqN9kSasZgQ/132', '2019-11-09', '2', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25Lre-vdJS91ZAtEGFY6PI7A', '18651853685', '1', '程云', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqGh4bGJECxayuqEIAAWOU0Ia79CPhXz9F9y0Qnc3OvqkRsDibHhgckgpibHpNnrkWsnicA0UTn9xWFA/132', '2019-11-03', '1', '30.4319253417997', '114.43936080294611', '湖北省', '武汉市', '江夏区', '天纵·水晶郦都&#40;武汉市江夏区)', '0.00');
INSERT INTO `users` VALUES ('oEAS25LzcTeHkjFAJ8aZtOHNIdWE', '', '1', '一如既往', 'https://wx.qlogo.cn/mmopen/vi_32/yDvtnuUfvGMo61mZA9XL8WwoDIBT8m6p9LtergoWJpcVKZuibqrABt3XeYkr6TTmvlIt9Y1PyCra5PoFxMhJDJQ/132', '2019-11-09', '1', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25Mlm-oCbgOdbe6be9z8TUA4', '', '1', '超_越梦想', 'https://wx.qlogo.cn/mmopen/vi_32/yDvtnuUfvGMo61mZA9XL8buR01Y0cHoa4ENVDadYoR9Ypv7dL1n8p0yTf6PjoibjF87NRktpa42Sl4Vq2uclhRA/132', '2019-11-03', '2', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25N48MS6Q8ECs73HuFMlMwqw', '', '1', '许欣怡', 'https://wx.qlogo.cn/mmhead/swE9oSNibMZcvCCx9bf8ZdiaQZPQMcf8PI7DxBFSOZeCk/132', '2019-11-02', '0', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25OlwKQv8W2XykfC1z33c8k4', '', '1', '王涵阳', 'https://wx.qlogo.cn/mmhead/xNKP68NQcmwwgbCoGDRdl7ZqYJiaHMPxLYQcaHuIWPtE/132', '2019-11-05', '0', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oEAS25OSeQK6STT9NFgbLXPYxup8', '', '1', '杨宗翰', 'https://wx.qlogo.cn/mmhead/RILlOftibdwBduOLzWibjmfrT2flQM9g9pibMqrqoeic4s4/132', '2019-10-30', '0', '', '', '', '', '', null, '0.00');
INSERT INTO `users` VALUES ('oMpgn4-rV7kRN4pJQ8exSc4KZoxk', '18651853685', '1', '程云', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83ep5n0vDTSPsooyZd5OlHN3vIxHR9naJFxA9RzD2fOVGtdA9OlaGicw3yFHUzxDSDrEsryWszJWLVfQ/132', '2019-10-21', '1', '30.43166292807817', '114.43914233429041', '湖北省', '武汉市', '江夏区', '天纵·水晶郦都&#40;武汉市江夏区)', '0.00');
INSERT INTO `users` VALUES ('oMpgn4x9Mx-CWMAZy-dMh3rmKH-s', '15895903085', '1', 'xxx、defined', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoxVLOD5FlbB2PiaqcKbP2Kd1HH5203gzxkiaFWyIms9yqL3r3vicKAdia7DicS4aE8NRia9mS8dorNPXbQ/132', '2019-10-27', '1', '32.05838', '118.79647', '江苏省', '南京市', '玄武区', '南京市政府&#40;北京东路)', '0.00');

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
-- Records of withdraw
-- ----------------------------

-- ----------------------------
-- View structure for follower_project
-- ----------------------------
DROP VIEW IF EXISTS `follower_project`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `follower_project` AS select `follow`.`follower_id` AS `follower_id`,`project`.`project_id` AS `project_id`,`project`.`type` AS `type`,`project`.`title` AS `title`,`project`.`salary` AS `salary`,`project`.`create_time` AS `create_time`,`project`.`create_userid` AS `create_userid`,`project`.`start_time` AS `start_time`,`project`.`deadline_time` AS `deadline_time`,`project`.`rule` AS `rule`,`project`.`region` AS `region` from (`follow` left join `project` on((`follow`.`project_id` = `project`.`project_id`))) where ((`project`.`project_id` <> 0) and (`project`.`status` <> 0)) ;
