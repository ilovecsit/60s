/*
 Navicat Premium Dump SQL

 Source Server         : localhost_mysql
 Source Server Type    : MySQL
 Source Server Version : 80038 (8.0.38)
 Source Host           : localhost:3306
 Source Schema         : 60s

 Target Server Type    : MySQL
 Target Server Version : 80038 (8.0.38)
 File Encoding         : 65001

 Date: 28/10/2024 23:14:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for all_group_mes
-- ----------------------------
DROP TABLE IF EXISTS `all_group_mes`;
CREATE TABLE `all_group_mes`  (
  `groupId` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '群聊id，主键,从10000开始',
  `groupName` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '群名字，唯一',
  `groupCreatedId` int UNSIGNED NOT NULL COMMENT '外键，创建者id',
  `groupCreatedTime` datetime NOT NULL COMMENT '群聊创建时间',
  `groupMaxMemNum` int NOT NULL DEFAULT 100 COMMENT '群聊最大人数，默认100',
  `groupManagerMaxNum` int NOT NULL DEFAULT 5 COMMENT '群管理最大人数，默认为5',
  `groupKind` smallint NOT NULL DEFAULT 1 COMMENT '群聊类型，默认为1，0表示系统群聊，1表示公共群聊，2表示私有群聊',
  `groupMemNum` int NOT NULL DEFAULT 1 COMMENT '群聊人数',
  `groupAvatar` varchar(300) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT 'https://t7.baidu.com/it/u=3114364099,3469983323&fm=193&f=GIF' COMMENT '群头像',
  `groupAnnouncement` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '本群未设置群公告' COMMENT '群公告',
  PRIMARY KEY (`groupId`) USING BTREE,
  UNIQUE INDEX `group_name_unique`(`groupName` ASC) USING BTREE,
  INDEX `foreignKey_createrid`(`groupCreatedId` ASC) USING BTREE,
  CONSTRAINT `foreignKey_createrid` FOREIGN KEY (`groupCreatedId`) REFERENCES `usermaininformation` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10172 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '记录所有群聊的关键信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for emailvisitrecord
-- ----------------------------
DROP TABLE IF EXISTS `emailvisitrecord`;
CREATE TABLE `emailvisitrecord`  (
  `ip` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '邮箱服务请求ip',
  `lastVisitDate` date NOT NULL COMMENT '邮箱服务最近一次访问时间',
  `visitTime` smallint NOT NULL COMMENT '在该日期的成功访问次数',
  `maxVisitTime` smallint NOT NULL DEFAULT 2 COMMENT '用户的每日最多访问次数，默认为2',
  PRIMARY KEY (`ip`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '邮箱服务访问记录表\r\n' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_mem
-- ----------------------------
DROP TABLE IF EXISTS `group_mem`;
CREATE TABLE `group_mem`  (
  `groupId` int UNSIGNED NOT NULL COMMENT '外键，群id',
  `groupMemId` int UNSIGNED NOT NULL COMMENT '外键，群成员id',
  `groupMemJoinTime` datetime NOT NULL COMMENT '群成员加入时间',
  `delete` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除？1表示删除，0表示没有删除，默认为0',
  `userKind` smallint NOT NULL DEFAULT 2 COMMENT '总管理者-1 群主0 管理员1 普通成员2 无法发言：3',
  INDEX `foreignKey_group_mem_group`(`groupId` ASC) USING BTREE,
  INDEX `foreignKey_userId`(`groupMemId` ASC) USING BTREE,
  CONSTRAINT `foreignKey_group_mem_group` FOREIGN KEY (`groupId`) REFERENCES `all_group_mes` (`groupId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `foreignKey_userId` FOREIGN KEY (`groupMemId`) REFERENCES `usermaininformation` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '群聊-群成员对应表，用来存储群聊与群成员的对应关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for isinit
-- ----------------------------
DROP TABLE IF EXISTS `isinit`;
CREATE TABLE `isinit`  (
  `isInit` bit(1) NOT NULL COMMENT '数据库初始化标志，初始化后不为空'
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for manager_mes
-- ----------------------------
DROP TABLE IF EXISTS `manager_mes`;
CREATE TABLE `manager_mes`  (
  `managerId` int UNSIGNED NOT NULL COMMENT '外键，群管理id',
  `groupId` int UNSIGNED NOT NULL COMMENT '外键，被管理群聊id',
  INDEX `foreignKey_group`(`groupId` ASC) USING BTREE,
  INDEX `foreignKey_manager`(`managerId` ASC) USING BTREE,
  CONSTRAINT `foreignKey_group` FOREIGN KEY (`groupId`) REFERENCES `all_group_mes` (`groupId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `foreignKey_manager` FOREIGN KEY (`managerId`) REFERENCES `usermaininformation` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '管理员与群聊对应表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for secret_group_codes
-- ----------------------------
DROP TABLE IF EXISTS `secret_group_codes`;
CREATE TABLE `secret_group_codes`  (
  `groupId` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `joinCodes` varchar(1100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '随机的验证码',
  PRIMARY KEY (`groupId`) USING BTREE,
  CONSTRAINT `group_id_group_codes` FOREIGN KEY (`groupId`) REFERENCES `all_group_mes` (`groupId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10171 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for usermaininformation
-- ----------------------------
DROP TABLE IF EXISTS `usermaininformation`;
CREATE TABLE `usermaininformation`  (
  `userId` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id，主键，从10000开始',
  `userPassword` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '用户密码,这里存储的是加密后的密码',
  `userEmail` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户邮箱，一个邮箱只能绑定一个账号',
  `userNickName` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '60s用户' COMMENT '用户昵称',
  `userPhotoUrl` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '%2f2iiE.gif&ehk=jkeGu9zc1Zyymwz0qPZYe8xd75s8l2R74EI2%2fquMe18%3d' COMMENT '用户头像url',
  `userCreateTime` datetime NOT NULL COMMENT '用户账号创建时间',
  `userLastUpdateTime` datetime NOT NULL COMMENT '用户账号最近一次修改时间',
  `userLastLoginTime` datetime NOT NULL COMMENT '用户最近一次登录时间',
  `userRole` bigint NOT NULL DEFAULT 1 COMMENT '用户角色，默认为1，表示普通用户，0表示系统管理员',
  PRIMARY KEY (`userId`) USING BTREE,
  UNIQUE INDEX `userEmailUnique`(`userEmail` ASC) USING BTREE COMMENT '用户邮箱设置为唯一，表示一个邮箱只能绑定一个账号',
  UNIQUE INDEX `userNickNameUnique`(`userNickName` ASC) USING BTREE COMMENT '用户名唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 10019 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '用户主要信息表：用来存储用户的主要信息' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
