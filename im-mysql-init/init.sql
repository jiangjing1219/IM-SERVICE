/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : localhost:3306
 Source Schema         : im

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 18/06/2024 21:59:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_user
-- ----------------------------
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user`  (
  `user_id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `mobile` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` bigint NULL DEFAULT NULL,
  `update_time` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_user
-- ----------------------------
INSERT INTO `app_user` VALUES ('311968820887553', 'jiangjing', '123456', NULL, 1695059126009, NULL);
INSERT INTO `app_user` VALUES ('312144459464705', 'jiangjing2', '123456', NULL, 1695142877622, NULL);
INSERT INTO `app_user` VALUES ('324431782084609', 'test@', '123456', NULL, 1701001929877, NULL);
INSERT INTO `app_user` VALUES ('325657571622913', 'test04', '123456', NULL, 1701586431242, NULL);
INSERT INTO `app_user` VALUES ('331810133245953', 'test03', '123456', NULL, 1704520201040, NULL);
INSERT INTO `app_user` VALUES ('331813675335681', 'test05', '123456', NULL, 1704521890572, NULL);

-- ----------------------------
-- Table structure for im_conversation_set
-- ----------------------------
DROP TABLE IF EXISTS `im_conversation_set`;
CREATE TABLE `im_conversation_set`  (
  `conversation_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `conversation_type` int NULL DEFAULT NULL COMMENT '0 单聊 1群聊 2机器人 3公众号',
  `from_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `to_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `is_mute` int NULL DEFAULT NULL COMMENT '是否免打扰 1免打扰',
  `is_top` int NULL DEFAULT NULL COMMENT '是否置顶 1置顶',
  `sequence` bigint NULL DEFAULT NULL COMMENT 'sequence',
  `readed_sequence` bigint NULL DEFAULT NULL,
  `app_id` int NOT NULL,
  PRIMARY KEY (`app_id`, `conversation_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_conversation_set
-- ----------------------------
INSERT INTO `im_conversation_set` VALUES ('0_311968820887553_312144459464705', 0, '311968820887553', '312144459464705', 0, 0, 158, 720, 10000);
INSERT INTO `im_conversation_set` VALUES ('0_311968820887553_324431782084609', 0, '311968820887553', '324431782084609', 0, 0, 163, 9, 10000);
INSERT INTO `im_conversation_set` VALUES ('0_311968820887553_331810133245953', 0, '311968820887553', '331810133245953', 0, 0, 159, 2, 10000);
INSERT INTO `im_conversation_set` VALUES ('0_312144459464705_311968820887553', 0, '312144459464705', '311968820887553', 0, 0, 157, 719, 10000);
INSERT INTO `im_conversation_set` VALUES ('0_312144459464705_324431782084609', 0, '312144459464705', '324431782084609', 0, 0, 150, 8, 10000);
INSERT INTO `im_conversation_set` VALUES ('0_324431782084609_311968820887553', 0, '324431782084609', '311968820887553', 0, 0, 161, 7, 10000);
INSERT INTO `im_conversation_set` VALUES ('0_324431782084609_312144459464705', 0, '324431782084609', '312144459464705', 0, 0, 152, 10, 10000);
INSERT INTO `im_conversation_set` VALUES ('0_331810133245953_311968820887553', 0, '331810133245953', '311968820887553', 0, 0, 160, 3, 10000);

-- ----------------------------
-- Table structure for im_friendship
-- ----------------------------
DROP TABLE IF EXISTS `im_friendship`;
CREATE TABLE `im_friendship`  (
  `app_id` int NOT NULL COMMENT 'app_id',
  `from_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'from_id',
  `to_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'to_id',
  `remark` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int NULL DEFAULT NULL COMMENT '状态 1正常 2删除',
  `black` int NULL DEFAULT NULL COMMENT '1正常 2拉黑',
  `create_time` bigint NULL DEFAULT NULL,
  `friend_sequence` bigint NULL DEFAULT NULL,
  `black_sequence` bigint NULL DEFAULT NULL,
  `add_source` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '来源',
  `extra` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '来源',
  PRIMARY KEY (`app_id`, `from_id`, `to_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_friendship
-- ----------------------------
INSERT INTO `im_friendship` VALUES (10000, '311968820887553', '312144459464705', 'JIANGJING2', 1, NULL, 1718554771889, 72, NULL, '1', NULL);
INSERT INTO `im_friendship` VALUES (10000, '311968820887553', '324431782084609', 'TEST', 1, NULL, 1718554837736, 73, NULL, '1', NULL);
INSERT INTO `im_friendship` VALUES (10000, '311968820887553', '331810133245953', 'TEST03', 1, NULL, 1718629715820, 75, NULL, '1', NULL);
INSERT INTO `im_friendship` VALUES (10000, '312144459464705', '311968820887553', '', 1, 1, 1718554771897, 72, NULL, '1', NULL);
INSERT INTO `im_friendship` VALUES (10000, '312144459464705', '324431782084609', '', 1, 1, 1718554937719, 74, NULL, '1', NULL);
INSERT INTO `im_friendship` VALUES (10000, '324431782084609', '311968820887553', '', 1, 1, 1718554837743, 73, NULL, '1', NULL);
INSERT INTO `im_friendship` VALUES (10000, '324431782084609', '312144459464705', 'JIANGJING2', 1, NULL, 1718554937716, 74, NULL, '1', NULL);
INSERT INTO `im_friendship` VALUES (10000, '331810133245953', '311968820887553', '', 1, 1, 1718629715820, 75, NULL, '1', NULL);

-- ----------------------------
-- Table structure for im_friendship_group
-- ----------------------------
DROP TABLE IF EXISTS `im_friendship_group`;
CREATE TABLE `im_friendship_group`  (
  `app_id` int NULL DEFAULT NULL COMMENT 'app_id',
  `from_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'from_id',
  `group_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `group_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `sequence` bigint NULL DEFAULT NULL,
  `create_time` bigint NULL DEFAULT NULL,
  `update_time` bigint NULL DEFAULT NULL,
  `del_flag` int NULL DEFAULT NULL,
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_friendship_group
-- ----------------------------

-- ----------------------------
-- Table structure for im_friendship_group_member
-- ----------------------------
DROP TABLE IF EXISTS `im_friendship_group_member`;
CREATE TABLE `im_friendship_group_member`  (
  `group_id` bigint NOT NULL,
  `to_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_friendship_group_member
-- ----------------------------

-- ----------------------------
-- Table structure for im_friendship_request
-- ----------------------------
DROP TABLE IF EXISTS `im_friendship_request`;
CREATE TABLE `im_friendship_request`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_id` int NULL DEFAULT NULL COMMENT 'app_id',
  `from_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'from_id',
  `to_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'to_id',
  `remark` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  `read_status` int NULL DEFAULT NULL COMMENT '是否已读 1已读',
  `add_source` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '好友来源',
  `add_wording` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '好友验证信息',
  `approve_status` int NULL DEFAULT NULL COMMENT '审批状态 1同意 2拒绝',
  `create_time` bigint NULL DEFAULT NULL,
  `update_time` bigint NULL DEFAULT NULL,
  `sequence` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_friendship_request
-- ----------------------------
INSERT INTO `im_friendship_request` VALUES (43, 10000, '311968820887553', '312144459464705', 'JIANGJING2', 0, '1', 'JIANGJING2', 1, 1718554766148, 1718554771880, 124);
INSERT INTO `im_friendship_request` VALUES (44, 10000, '311968820887553', '324431782084609', 'TEST', 0, '1', 'j11111111111111111', 1, 1718554832293, 1718554837732, 126);
INSERT INTO `im_friendship_request` VALUES (45, 10000, '324431782084609', '312144459464705', 'JIANGJING2', 0, '1', '2222', 1, 1718554931988, 1718554937710, 128);
INSERT INTO `im_friendship_request` VALUES (46, 10000, '311968820887553', '331810133245953', 'TEST03', 0, '1', 'TEST03', 1, 1718629710570, 1718629715778, 130);

-- ----------------------------
-- Table structure for im_group
-- ----------------------------
DROP TABLE IF EXISTS `im_group`;
CREATE TABLE `im_group`  (
  `app_id` int NOT NULL COMMENT 'app_id',
  `group_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'group_id',
  `owner_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '群主\r\n',
  `group_type` int NULL DEFAULT NULL COMMENT '群类型 1私有群（类似微信） 2公开群(类似qq）',
  `group_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `mute` int NULL DEFAULT NULL COMMENT '是否全员禁言，0 不禁言；1 全员禁言',
  `apply_join_type` int NULL DEFAULT NULL COMMENT '//    申请加群选项包括如下几种：\r\n//    0 表示禁止任何人申请加入\r\n//    1 表示需要群主或管理员审批\r\n//    2 表示允许无需审批自由加入群组',
  `photo` varchar(300) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `max_member_count` int NULL DEFAULT NULL,
  `introduction` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '群简介',
  `notification` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '群公告',
  `status` int NULL DEFAULT NULL COMMENT '群状态 0正常 1解散',
  `sequence` bigint NULL DEFAULT NULL,
  `create_time` bigint NULL DEFAULT NULL,
  `extra` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '来源',
  `update_time` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`app_id`, `group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_group
-- ----------------------------
INSERT INTO `im_group` VALUES (10000, 'ad72daaa2e33426cbb20ab20a8292fa9', '311968820887553', 1, 'testGroupName', 0, 0, '', 100, '群简介', '群公告', 1, 11, 1718643967703, NULL, NULL);

-- ----------------------------
-- Table structure for im_group_member
-- ----------------------------
DROP TABLE IF EXISTS `im_group_member`;
CREATE TABLE `im_group_member`  (
  `group_member_id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'group_id',
  `app_id` int NULL DEFAULT NULL,
  `member_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '成员id\r\n',
  `role` int NULL DEFAULT NULL COMMENT '群成员类型，0 普通成员, 1 管理员, 2 群主， 3 禁言，4 已经移除的成员',
  `speak_date` bigint NULL DEFAULT NULL,
  `mute` int NULL DEFAULT NULL COMMENT '是否全员禁言，0 不禁言；1 全员禁言',
  `alias` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '群昵称',
  `join_time` bigint NULL DEFAULT NULL COMMENT '加入时间',
  `introduction` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '群简介',
  `notification` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '群公告',
  `leave_time` bigint NULL DEFAULT NULL COMMENT '离开时间',
  `join_type` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '加入类型',
  `extra` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `speak_flag` int NULL DEFAULT NULL COMMENT '禁言状态',
  PRIMARY KEY (`group_member_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_group_member
-- ----------------------------
INSERT INTO `im_group_member` VALUES (40, 'ad72daaa2e33426cbb20ab20a8292fa9', 10000, '311968820887553', 2, 0, NULL, 'jiangjing553', 1718643967720, NULL, NULL, NULL, '0', NULL, 0);
INSERT INTO `im_group_member` VALUES (41, 'ad72daaa2e33426cbb20ab20a8292fa9', 10000, '312144459464705', 0, 0, NULL, 'jingjing4075', 1718643967731, NULL, NULL, NULL, '0', NULL, 0);
INSERT INTO `im_group_member` VALUES (42, 'ad72daaa2e33426cbb20ab20a8292fa9', 10000, '324431782084609', 0, 0, NULL, 'test', 1718643967742, NULL, NULL, NULL, '0', NULL, 0);
INSERT INTO `im_group_member` VALUES (43, 'ad72daaa2e33426cbb20ab20a8292fa9', 10000, '331810133245953', 0, 0, NULL, 'test03', 1718643967767, NULL, NULL, NULL, '0', NULL, 0);

-- ----------------------------
-- Table structure for im_group_message_history
-- ----------------------------
DROP TABLE IF EXISTS `im_group_message_history`;
CREATE TABLE `im_group_message_history`  (
  `app_id` int NOT NULL COMMENT 'app_id',
  `from_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'from_id',
  `group_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'group_id',
  `message_key` bigint NOT NULL COMMENT 'messageBodyId',
  `create_time` bigint NULL DEFAULT NULL,
  `sequence` bigint NULL DEFAULT NULL,
  `message_random` int NULL DEFAULT NULL,
  `message_time` bigint NULL DEFAULT NULL COMMENT '来源',
  PRIMARY KEY (`app_id`, `group_id`, `message_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_group_message_history
-- ----------------------------
INSERT INTO `im_group_message_history` VALUES (10000, '324431782084609', '2f5a4ef0209240e1ba84ec7ad783335c', 333286450659329, 1705224164357, 1, NULL, 1705224159000);
INSERT INTO `im_group_message_history` VALUES (10000, '324431782084609', '2f5a4ef0209240e1ba84ec7ad783335c', 333287222411265, 1705224532108, 2, NULL, 1705224532000);
INSERT INTO `im_group_message_history` VALUES (10000, '324431782084609', '2f5a4ef0209240e1ba84ec7ad783335c', 333288161935361, 1705224980050, 3, NULL, 1705224980000);
INSERT INTO `im_group_message_history` VALUES (10000, '324431782084609', '2f5a4ef0209240e1ba84ec7ad783335c', 333289594290177, 1705225663716, 4, NULL, 1705225664000);
INSERT INTO `im_group_message_history` VALUES (10000, '324431782084609', '2f5a4ef0209240e1ba84ec7ad783335c', 333289753673729, 1705225739526, 5, NULL, 1705225739000);
INSERT INTO `im_group_message_history` VALUES (10000, '324431782084609', '2f5a4ef0209240e1ba84ec7ad783335c', 333289944514561, 1705225830772, 6, NULL, 1705225831000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333290470899713, 1705226081311, 7, NULL, 1705226081000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333291181834241, 1705226420913, 8, NULL, 1705226421000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333291347509249, 1705226499634, 9, NULL, 1705226500000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333291825659905, 1705226727420, 10, NULL, 1705226727000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333292110872577, 1705226863668, 11, NULL, 1705226864000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333292339462145, 1705226972090, 12, NULL, 1705226972000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333292379308033, 1705226991349, 13, NULL, 1705226991000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333292446416897, 1705227023975, 14, NULL, 1705227024000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333292714852353, 1705227151770, 15, NULL, 1705227152000);
INSERT INTO `im_group_message_history` VALUES (10000, '331810133245953', '2f5a4ef0209240e1ba84ec7ad783335c', 333292744212481, 1705227165806, 16, NULL, 1705227166000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333292863750145, 1705227222616, 17, NULL, 1705227223000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '2f5a4ef0209240e1ba84ec7ad783335c', 333293188808705, 1705227377312, 18, NULL, 1705227377000);
INSERT INTO `im_group_message_history` VALUES (10000, '324431782084609', '2f5a4ef0209240e1ba84ec7ad783335c', 333293211877377, 1705227388487, 19, NULL, 1705227388000);
INSERT INTO `im_group_message_history` VALUES (10000, '331810133245953', '2f5a4ef0209240e1ba84ec7ad783335c', 333293241237505, 1705227402191, 20, NULL, 1705227402000);
INSERT INTO `im_group_message_history` VALUES (10000, '331813675335681', '4176570309204a2f839228ccc1f5b162', 333293513867265, 1705227532794, 1, NULL, 1705227533000);

-- ----------------------------
-- Table structure for im_message_body
-- ----------------------------
DROP TABLE IF EXISTS `im_message_body`;
CREATE TABLE `im_message_body`  (
  `app_id` int NOT NULL,
  `message_key` bigint NOT NULL,
  `message_body` varchar(5000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `security_key` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `message_time` bigint NULL DEFAULT NULL,
  `create_time` bigint NULL DEFAULT NULL,
  `extra` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `del_flag` int NULL DEFAULT NULL,
  PRIMARY KEY (`app_id`, `message_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_message_body
-- ----------------------------
INSERT INTO `im_message_body` VALUES (10000, 332055116251137, '{\"type\":1,\"content\":\"我是test03,n你好test\"}', '', 1704637018000, 1704637018133, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 332055267246081, '{\"type\":1,\"content\":\"我是test05,你好test\"}', '', 1704637091000, 1704637090480, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 332055353229313, '{\"type\":1,\"content\":\"1111\"}', '', 1704637132000, 1704637131483, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 332055370006529, '{\"type\":1,\"content\":\"4444\"}', '', 1704637140000, 1704637139769, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 332055374200833, '{\"type\":1,\"content\":\"111\"}', '', 1704637142000, 1704637141806, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 332055430823937, '{\"type\":1,\"content\":\"你好test05,我是test\"}', '', 1704637169000, 1704637168489, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333274777911297, '{\"type\":1,\"content\":\"1\"}', '', 1705218598000, 1705218598883, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333286450659329, '{\"type\":1,\"content\":\"222\"}', '', 1705224159000, 1705224164292, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333287222411265, '{\"type\":1,\"content\":\"1\"}', '', 1705224532000, 1705224532093, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333288161935361, '{\"type\":1,\"content\":\"2\"}', '', 1705224980000, 1705224980043, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333289594290177, '{\"type\":1,\"content\":\"22\"}', '', 1705225664000, 1705225663703, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333289753673729, '{\"type\":1,\"content\":\"2\"}', '', 1705225739000, 1705225739510, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333289944514561, '{\"type\":1,\"content\":\"1\"}', '', 1705225831000, 1705225830762, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333290470899713, '{\"type\":1,\"content\":\"1\"}', '', 1705226081000, 1705226081298, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333291181834241, '{\"type\":1,\"content\":\"222\"}', '', 1705226421000, 1705226420903, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333291347509249, '{\"type\":1,\"content\":\"e\"}', '', 1705226500000, 1705226499626, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333291825659905, '{\"type\":1,\"content\":\"33\"}', '', 1705226727000, 1705226727411, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333292110872577, '{\"type\":1,\"content\":\"2\"}', '', 1705226864000, 1705226863656, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333292339462145, '{\"type\":1,\"content\":\"1\"}', '', 1705226972000, 1705226972084, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333292379308033, '{\"type\":1,\"content\":\"1\"}', '', 1705226991000, 1705226991341, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333292446416897, '{\"type\":1,\"content\":\"11\"}', '', 1705227024000, 1705227023967, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333292714852353, '{\"type\":1,\"content\":\"hello\"}', '', 1705227152000, 1705227151762, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333292744212481, '{\"type\":1,\"content\":\"he\"}', '', 1705227166000, 1705227165791, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333292863750145, '{\"type\":1,\"content\":\"hah\"}', '', 1705227223000, 1705227222605, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333293188808705, '{\"type\":1,\"content\":\"test\"}', '', 1705227377000, 1705227377296, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333293211877377, '{\"type\":1,\"content\":\"00\"}', '', 1705227388000, 1705227388482, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333293241237505, '{\"type\":1,\"content\":\"11\"}', '', 1705227402000, 1705227402186, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 333293513867265, '{\"type\":1,\"content\":\"A\"}', '', 1705227533000, 1705227532785, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 353532460924929, '{\"type\":1,\"content\":\"2222\"}', '', 1714878213000, 1714878214164, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 353532817440769, '{\"type\":1,\"content\":\"333\"}', '', 1714878374000, 1714878384505, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 353535331926017, '{\"type\":1,\"content\":\"88\"}', '', 1714879583000, 1714879583338, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 353595006386177, '{\"type\":1,\"content\":\"@\"}', '', 1714908038000, 1714908038415, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 353595585200129, '{\"type\":1,\"content\":\"#\"}', '', 1714908314000, 1714908314562, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 353595939618817, '{\"type\":1,\"content\":\"￥\"}', '', 1714908483000, 1714908483131, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 353595973173249, '{\"type\":1,\"content\":\"&&&\"}', '', 1714908499000, 1714908499528, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047278624769, '{\"type\":1,\"content\":\"\"}', '', 1717507884000, 1717507884974, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047282819073, '{\"type\":1,\"content\":\"\"}', '', 1717507886000, 1717507886630, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047360413697, '{\"type\":1,\"content\":\"vue3\"}', '', 1717507923000, 1717507923614, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047425425409, '{\"type\":1,\"content\":\"你好胖\"}', '', 1717507954000, 1717507954495, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047452688385, '{\"type\":1,\"content\":\"vue3\"}', '', 1717507967000, 1717507967232, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047452688386, '{\"type\":1,\"content\":\"vue3\"}', '', 1717507967000, 1717507967422, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047490437121, '{\"type\":1,\"content\":\"完美的对话\"}', '', 1717507985000, 1717507985630, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047714832385, '{\"type\":1,\"content\":\"\"}', '', 1717508092000, 1717508092880, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047719026689, '{\"type\":1,\"content\":\"\"}', '', 1717508094000, 1717508094391, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047721123841, '{\"type\":1,\"content\":\"\"}', '', 1717508095000, 1717508095583, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359047731609601, '{\"type\":1,\"content\":\"\"}', '', 1717508100000, 1717508100465, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359070601052161, '{\"type\":1,\"content\":\"11111\"}', '', 1717519005000, 1717519005286, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359071129534465, '{\"type\":1,\"content\":\"你好\"}', '', 1717519257000, 1717519257326, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359071641239553, '{\"type\":1,\"content\":\"你好呀\"}', '', 1717519501000, 1717519501777, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359074799550465, '{\"type\":1,\"content\":\"你好\"}', '', 1717521007000, 1717521007867, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359076263362561, '{\"type\":1,\"content\":\"你好\"}', '', 1717521705000, 1717521705393, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359076663918593, '{\"type\":1,\"content\":\"你好\"}', '', 1717521896000, 1717521896275, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359077265801217, '{\"type\":1,\"content\":\"你好\"}', '', 1717522183000, 1717522183222, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359078612172801, '{\"type\":1,\"content\":\"@@@\"}', '', 1717522825000, 1717522825794, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359079337787393, '{\"type\":1,\"content\":\"你好111\"}', '', 1717523171000, 1717523171219, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359081694986241, '{\"type\":1,\"content\":\"你好\"}', '', 1717524295000, 1717524295904, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359081726443521, '{\"type\":1,\"content\":\"22222\"}', '', 1717524310000, 1717524310703, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359081940353025, '{\"type\":1,\"content\":\"###3\"}', '', 1717524412000, 1717524412208, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082007461889, '{\"type\":1,\"content\":\"&&&&\"}', '', 1717524444000, 1717524444467, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082387046401, '{\"type\":1,\"content\":\"1\"}', '', 1717524625000, 1717524625624, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082563207169, '{\"type\":1,\"content\":\"2\"}', '', 1717524709000, 1717524709553, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082691133441, '{\"type\":1,\"content\":\"3\"}', '', 1717524770000, 1717524770086, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082735173633, '{\"type\":1,\"content\":\"你好这是一条测试消息1\"}', '', 1717524791000, 1717524791462, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082749853697, '{\"type\":1,\"content\":\"你好这是一条测试消息2\"}', '', 1717524798000, 1717524798116, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082772922369, '{\"type\":1,\"content\":\"你好这是一条测试消息3\"}', '', 1717524809000, 1717524809257, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082783408129, '{\"type\":1,\"content\":\"你好这是一条测试消息4\"}', '', 1717524814000, 1717524814778, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082814865409, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524829000, 1717524829069, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082902945793, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524871000, 1717524871790, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082905042945, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524872000, 1717524872527, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082905042946, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524872000, 1717524872659, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082905042947, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524872000, 1717524872825, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082938597377, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524887000, 1717524888018, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082938597378, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524888000, 1717524888276, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082938597379, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524888000, 1717524888434, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082938597380, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524888000, 1717524888576, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082938597381, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524888000, 1717524888745, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082938597382, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524888000, 1717524888890, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082940694529, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524889000, 1717524889925, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082942791681, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524890000, 1717524890066, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082942791682, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524890000, 1717524890492, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082951180289, '{\"type\":1,\"content\":\"你好这是一条测试消息1\"}', '', 1717524894000, 1717524894788, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082970054657, '{\"type\":1,\"content\":\"你好这是一条测试消息1\"}', '', 1717524903000, 1717524903815, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359082982637569, '{\"type\":1,\"content\":\"你好这是一条测试消息1\"}', '', 1717524909000, 1717524909342, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083081203713, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524956000, 1717524956092, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083083300865, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524957000, 1717524957214, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083085398017, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524958000, 1717524958264, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083087495169, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524959000, 1717524959122, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083087495170, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524959000, 1717524959404, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083087495171, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524959000, 1717524959592, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083089592321, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524959000, 1717524960643, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083089592322, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524959000, 1717524960648, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083089592323, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524960000, 1717524960654, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083089592324, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524960000, 1717524960809, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083089592325, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524960000, 1717524960812, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083102175233, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524966000, 1717524966382, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083102175234, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524966000, 1717524966557, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083102175235, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524966000, 1717524966994, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083106369537, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524967000, 1717524968038, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083106369538, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524967000, 1717524968041, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083106369539, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524967000, 1717524968091, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083112660993, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524971000, 1717524971540, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083112660994, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524971000, 1717524971688, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083112660995, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524971000, 1717524971928, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083114758145, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524972000, 1717524972177, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083114758146, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524972000, 1717524972341, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083114758147, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524972000, 1717524972487, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083123146753, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524976000, 1717524976144, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083125243905, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524977000, 1717524977343, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083127341057, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524978000, 1717524978515, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083129438209, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524979000, 1717524979416, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083131535361, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524980000, 1717524980601, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083131535362, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524980000, 1717524980752, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083131535363, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524980000, 1717524980909, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083133632513, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524981000, 1717524981066, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083133632514, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524981000, 1717524981216, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083135729665, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524982000, 1717524982759, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083135729666, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524982000, 1717524982924, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083137826817, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524983000, 1717524983221, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083137826818, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524983000, 1717524983404, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083137826819, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524983000, 1717524983533, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083137826820, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524983000, 1717524983629, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083137826821, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524983000, 1717524983765, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083144118273, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524986000, 1717524987003, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083148312577, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524987000, 1717524988039, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083148312578, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524987000, 1717524988040, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083148312579, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524987000, 1717524988048, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083148312580, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524987000, 1717524988049, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083148312581, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524987000, 1717524988124, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083150409729, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524989000, 1717524989671, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083152506881, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524990000, 1717524990092, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083152506882, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524990000, 1717524990231, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083152506883, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524990000, 1717524990361, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083152506884, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524990000, 1717524990526, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083154604033, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524991000, 1717524991599, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083154604034, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524991000, 1717524991769, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083156701185, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524992000, 1717524992084, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083156701186, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524992000, 1717524992240, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083156701187, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524992000, 1717524992510, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083156701188, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524992000, 1717524992650, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083158798337, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524993000, 1717524993567, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083158798338, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524993000, 1717524993651, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083158798339, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524993000, 1717524993834, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083160895489, '{\"type\":1,\"content\":\"你好这是一条测试消息，完美\"}', '', 1717524994000, 1717524994050, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083200741377, '{\"type\":1,\"content\":\"聊天啦\"}', '', 1717525013000, 1717525013635, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359083223810049, '{\"type\":1,\"content\":\"睡觉！！！！\"}', '', 1717525024000, 1717525024288, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359233618968577, '{\"type\":1,\"content\":\"你好\"}', '', 1717596738000, 1717596738976, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359233642037249, '{\"type\":1,\"content\":\"你好\"}', '', 1717596749000, 1717596749729, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359236708073473, '{\"type\":1,\"content\":\"111\"}', '', 1717598211000, 1717598211442, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359236919885825, '{\"type\":1,\"content\":\"111\"}', '', 1717598312000, 1717598312071, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359237098143745, '{\"type\":1,\"content\":\"444\"}', '', 1717598397000, 1717598397159, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359237259624449, '{\"type\":1,\"content\":\"22\"}', '', 1717598474000, 1717598474670, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359237555322881, '{\"type\":1,\"content\":\"44\"}', '', 1717598615000, 1717598615628, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359237689540609, '{\"type\":1,\"content\":\"4\"}', '', 1717598679000, 1717598679193, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359237794398209, '{\"type\":1,\"content\":\"5\"}', '', 1717598729000, 1717598729848, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359237903450113, '{\"type\":1,\"content\":\"6\"}', '', 1717598781000, 1717598781308, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359238077513729, '{\"type\":1,\"content\":\"7\"}', '', 1717598864000, 1717598864799, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359238148816897, '{\"type\":1,\"content\":\"8\"}', '', 1717598898000, 1717598898720, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359238169788417, '{\"type\":1,\"content\":\"你好胖\"}', '', 1717598908000, 1717598908137, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359238182371329, '{\"type\":1,\"content\":\"5555\"}', '', 1717598914000, 1717598914937, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359244366872577, '{\"type\":1,\"content\":\"是\"}', '', 1717601863000, 1717601863643, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359244373164033, '{\"type\":1,\"content\":\"你好\"}', '', 1717601866000, 1717601866351, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359251631407105, '{\"type\":1,\"content\":\"你好\"}', '', 1717605327000, 1717605327551, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359255876042753, '{\"type\":1,\"content\":\"33\"}', '', 1717607351000, 1717607351659, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359256385650689, '{\"type\":1,\"content\":\"q\"}', '', 1717607594000, 1717607594925, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359260831612929, '{\"type\":1,\"content\":\"nihao \"}', '', 1717609714000, 1717609714373, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359412862550017, '{\"type\":1,\"content\":\"111\"}', '', 1717682208000, 1717682208798, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359412873035777, '{\"type\":1,\"content\":\"nihao \"}', '', 1717682213000, 1717682214005, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359413095333889, '{\"type\":1,\"content\":\"nihao \"}', '', 1717682319000, 1717682319536, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359413198094337, '{\"type\":1,\"content\":\"你好\"}', '', 1717682368000, 1717682368498, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359413206482945, '{\"type\":1,\"content\":\"nihao \"}', '', 1717682372000, 1717682372226, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359413219065857, '{\"type\":1,\"content\":\"测试消息\"}', '', 1717682378000, 1717682378411, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359413525250049, '{\"type\":1,\"content\":\"完美\"}', '', 1717682524000, 1717682524505, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359413581873153, '{\"type\":1,\"content\":\"vue2 牛逼\"}', '', 1717682551000, 1717682551849, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359413602844673, '{\"type\":1,\"content\":\"vue2\"}', '', 1717682561000, 1717682561345, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359413607038977, '{\"type\":1,\"content\":\"vue3\"}', '', 1717682563000, 1717682563898, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359413615427585, '{\"type\":1,\"content\":\"是的、\"}', '', 1717682567000, 1717682567726, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359413630107649, '{\"type\":1,\"content\":\"啊啊啊啊\"}', '', 1717682574000, 1717682574731, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359419739111425, '{\"type\":1,\"content\":\"test\"}', '', 1717685487000, 1717685487099, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359434979115009, '{\"type\":1,\"content\":\"11\"}', '', 1717692754000, 1717692754663, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359435042029569, '{\"type\":1,\"content\":\"222\"}', '', 1717692784000, 1717692784085, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 359435050418177, '{\"type\":1,\"content\":\"333\"}', '', 1717692788000, 1717692788749, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360087317118977, '{\"type\":1,\"content\":\"nihao \"}', '', 1718003811000, 1718003813036, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360310443606017, '{\"type\":1,\"content\":\"你好\"}', '', 1718110208000, 1718110208711, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360310454091777, '{\"type\":1,\"content\":\"hello\"}', '', 1718110213000, 1718110213722, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360317024468993, '{\"type\":1,\"content\":\"1\"}', '', 1718113346000, 1718113346907, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360495945089025, '{\"type\":1,\"content\":\"nihao \"}', '', 1718198661000, 1718198662031, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360496014295041, '{\"type\":1,\"content\":\"你好\"}', '', 1718198695000, 1718198695249, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360497872371713, '{\"type\":1,\"content\":\"完美调试\"}', '', 1718199581000, 1718199581603, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360670512021505, '{\"type\":1,\"content\":\"你好\"}', '', 1718281902000, 1718281902176, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360670526701569, '{\"type\":1,\"content\":\"你好\"}', '', 1718281909000, 1718281909326, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360670570741761, '{\"type\":1,\"content\":\"雍正王朝\"}', '', 1718281930000, 1718281930758, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360670581227521, '{\"type\":1,\"content\":\"哈哈\"}', '', 1718281935000, 1718281935399, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360670595907585, '{\"type\":1,\"content\":\"收到！】\"}', '', 1718281942000, 1718281942718, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360670616879105, '{\"type\":1,\"content\":\"我不喜欢\"}', '', 1718281952000, 1718281952209, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360670646239233, '{\"type\":1,\"content\":\"在线登陆\"}', '', 1718281966000, 1718281966600, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360670665113601, '{\"type\":1,\"content\":\"最后阅读\"}', '', 1718281975000, 1718281975542, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360670683987969, '{\"type\":1,\"content\":\"三小时前\"}', '', 1718281984000, 1718281984020, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360670713348097, '{\"type\":1,\"content\":\"你看着办吧\"}', '', 1718281998000, 1718281998288, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360671900336129, '{\"type\":1,\"content\":\"1\"}', '', 1718282564000, 1718282564180, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360671904530433, '{\"type\":1,\"content\":\"2\"}', '', 1718282565000, 1718282566253, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360671904530434, '{\"type\":1,\"content\":\"3\"}', '', 1718282565000, 1718282566267, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360671906627585, '{\"type\":1,\"content\":\"4\"}', '', 1718282566000, 1718282567283, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360671908724737, '{\"type\":1,\"content\":\"5\"}', '', 1718282567000, 1718282568534, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360671908724738, '{\"type\":1,\"content\":\"5\"}', '', 1718282568000, 1718282568544, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360671908724739, '{\"type\":1,\"content\":\"5\"}', '', 1718282568000, 1718282568571, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360671910821889, '{\"type\":1,\"content\":\"67\"}', '', 1718282568000, 1718282569691, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360671910821890, '{\"type\":1,\"content\":\"7\"}', '', 1718282569000, 1718282569695, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360671912919041, '{\"type\":1,\"content\":\"8\"}', '', 1718282569000, 1718282570866, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672219103233, '{\"type\":1,\"content\":\"1\"}', '', 1718282716000, 1718282716312, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672221200385, '{\"type\":1,\"content\":\"2\"}', '', 1718282716000, 1718282717357, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672221200386, '{\"type\":1,\"content\":\"2\"}', '', 1718282716000, 1718282717372, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672221200387, '{\"type\":1,\"content\":\"3\"}', '', 1718282717000, 1718282717375, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672223297537, '{\"type\":1,\"content\":\"4\"}', '', 1718282717000, 1718282718471, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672223297538, '{\"type\":1,\"content\":\"4\"}', '', 1718282717000, 1718282718479, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672223297539, '{\"type\":1,\"content\":\"54\"}', '', 1718282718000, 1718282718482, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672223297540, '{\"type\":1,\"content\":\"56\"}', '', 1718282718000, 1718282718633, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672223297541, '{\"type\":1,\"content\":\"56\"}', '', 1718282718000, 1718282718643, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672225394689, '{\"type\":1,\"content\":\"6\"}', '', 1718282719000, 1718282719720, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672225394690, '{\"type\":1,\"content\":\"6\"}', '', 1718282719000, 1718282719751, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672225394691, '{\"type\":1,\"content\":\"7\"}', '', 1718282719000, 1718282719754, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672227491841, '{\"type\":1,\"content\":\"8\"}', '', 1718282719000, 1718282720878, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672231686145, '{\"type\":1,\"content\":\"9\"}', '', 1718282722000, 1718282722538, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672309280769, '{\"type\":1,\"content\":\"1\"}', '', 1718282759000, 1718282759768, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672311377921, '{\"type\":1,\"content\":\"2\"}', '', 1718282760000, 1718282760799, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672311377922, '{\"type\":1,\"content\":\"2\"}', '', 1718282760000, 1718282760809, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672311377923, '{\"type\":1,\"content\":\"3\"}', '', 1718282760000, 1718282760812, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672313475073, '{\"type\":1,\"content\":\"4\"}', '', 1718282761000, 1718282761908, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672315572225, '{\"type\":1,\"content\":\"5\"}', '', 1718282762000, 1718282762976, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672342835201, '{\"type\":1,\"content\":\"7\"}', '', 1718282775000, 1718282775796, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672344932353, '{\"type\":1,\"content\":\"8\"}', '', 1718282776000, 1718282776822, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672344932354, '{\"type\":1,\"content\":\"8\"}', '', 1718282776000, 1718282776835, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672347029505, '{\"type\":1,\"content\":\"9\"}', '', 1718282777000, 1718282777873, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672347029506, '{\"type\":1,\"content\":\"9\"}', '', 1718282777000, 1718282777887, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672351223809, '{\"type\":1,\"content\":\"0\"}', '', 1718282779000, 1718282779978, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672791625729, '{\"type\":1,\"content\":\"1\"}', '', 1718282989000, 1718282989200, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672793722881, '{\"type\":1,\"content\":\"2\"}', '', 1718282989000, 1718282990219, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672793722882, '{\"type\":1,\"content\":\"3\"}', '', 1718282989000, 1718282990224, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672795820033, '{\"type\":1,\"content\":\"4\"}', '', 1718282990000, 1718282991230, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672795820034, '{\"type\":1,\"content\":\"5\"}', '', 1718282991000, 1718282991247, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 360672797917185, '{\"type\":1,\"content\":\"6\"}', '', 1718282991000, 1718282992276, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361037121454081, '{\"type\":1,\"content\":\"你好\"}', '', 1718456714000, 1718456715427, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361037385695233, '{\"type\":1,\"content\":\"二\"}', '', 1718456841000, 1718456841682, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361037400375297, '{\"type\":1,\"content\":\"蠢猪\"}', '', 1718456848000, 1718456848797, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361037427638273, '{\"type\":1,\"content\":\"你好\"}', '', 1718456861000, 1718456861632, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361037461192705, '{\"type\":1,\"content\":\"没有实现多端同步\"}', '', 1718456877000, 1718456877950, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361037488455681, '{\"type\":1,\"content\":\"两百年都是是西安额的\"}', '', 1718456890000, 1718456890510, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361037505232897, '{\"type\":1,\"content\":\"zgebiand e\"}', '', 1718456898000, 1718456898058, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361037851262977, '{\"type\":1,\"content\":\"ok\"}', '', 1718457062000, 1718457063268, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361037899497473, '{\"type\":1,\"content\":\"恰饭去了\"}', '', 1718457086000, 1718457086684, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240115281921, '{\"type\":1,\"content\":\"你好 test\"}', '', 1718553510000, 1718553510706, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240134156289, '{\"type\":1,\"content\":\"你好 test\"}', '', 1718553519000, 1718553519368, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240167710721, '{\"type\":1,\"content\":\"你好\"}', '', 1718553535000, 1718553535679, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240218042369, '{\"type\":1,\"content\":\"1111\"}', '', 1718553559000, 1718553559825, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240230625281, '{\"type\":1,\"content\":\"3333\"}', '', 1718553565000, 1718553565232, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240375328769, '{\"type\":1,\"content\":\"5555\"}', '', 1718553634000, 1718553634563, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240396300289, '{\"type\":1,\"content\":\"你好\"}', '', 1718553644000, 1718553644933, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240671027201, '{\"type\":1,\"content\":\"222\"}', '', 1718553775000, 1718553775561, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240700387329, '{\"type\":1,\"content\":\"你好\"}', '', 1718553789000, 1718553789740, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240828313601, '{\"type\":1,\"content\":\"你好\"}', '', 1718553850000, 1718553850069, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240834605057, '{\"type\":1,\"content\":\"我是 test\"}', '', 1718553853000, 1718553853905, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240914296833, '{\"type\":1,\"content\":\"44444\"}', '', 1718553891000, 1718553891798, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361240962531329, '{\"type\":1,\"content\":\"6666\"}', '', 1718553914000, 1718553914943, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361241103040513, '{\"type\":1,\"content\":\"你好\"}', '', 1718553981000, 1718553981442, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361241518276609, '{\"type\":1,\"content\":\"7777777\"}', '', 1718554179000, 1718554179456, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361241744769025, '{\"type\":1,\"content\":\"22222\"}', '', 1718554287000, 1718554287084, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361242168393729, '{\"type\":1,\"content\":\"你好705\"}', '', 1718554489000, 1718554489998, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361242220822529, '{\"type\":1,\"content\":\"你好609\"}', '', 1718554514000, 1718554514376, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361242789150721, '{\"type\":1,\"content\":\"NIHAO \"}', '', 1718554785000, 1718554785585, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361242820608001, '{\"type\":1,\"content\":\"success\"}', '', 1718554800000, 1718554800951, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361242925465601, '{\"type\":1,\"content\":\"@@@@@@@@@@@@@@@@\"}', '', 1718554850000, 1718554850051, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361242975797249, '{\"type\":1,\"content\":\"你好啊，test\"}', '', 1718554874000, 1718554874383, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361243170832385, '{\"type\":1,\"content\":\"66666\"}', '', 1718554967000, 1718554967956, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361243187609601, '{\"type\":1,\"content\":\"NIHAO , jiangjign2\"}', '', 1718554975000, 1718554975652, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361243206483969, '{\"type\":1,\"content\":\"success\"}', '', 1718554984000, 1718554984448, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361243231649793, '{\"type\":1,\"content\":\"⁰00\"}', '', 1718554995000, 1718554996059, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361243269398529, '{\"type\":1,\"content\":\"单聊完美切换\"}', '', 1718555014000, 1718555014432, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361243307147265, '{\"type\":1,\"content\":\"没错\"}', '', 1718555032000, 1718555032161, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361243344896001, '{\"type\":1,\"content\":\"随便起\"}', '', 1718555050000, 1718555050502, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361399054237697, '{\"type\":1,\"content\":\"你好\"}', '', 1718629298000, 1718629298226, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361399071014913, '{\"type\":1,\"content\":\"你好\"}', '', 1718629306000, 1718629306456, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361399970693121, '{\"type\":1,\"content\":\"你好 test03\"}', '', 1718629735000, 1718629735837, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361400004247553, '{\"type\":1,\"content\":\"你好 jiangjing\"}', '', 1718629751000, 1718629751930, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361575221297153, '{\"type\":1,\"content\":\"你好\"}', '', 1718713300000, 1718713301103, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361575238074369, '{\"type\":1,\"content\":\"接受了\"}', '', 1718713309000, 1718713309418, NULL, 0);
INSERT INTO `im_message_body` VALUES (10000, 361575254851585, '{\"type\":1,\"content\":\"哇妹妹de\"}', '', 1718713317000, 1718713317217, NULL, 0);

-- ----------------------------
-- Table structure for im_message_history
-- ----------------------------
DROP TABLE IF EXISTS `im_message_history`;
CREATE TABLE `im_message_history`  (
  `app_id` int NOT NULL COMMENT 'app_id',
  `from_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'from_id',
  `to_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'to_id\r\n',
  `owner_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'owner_id\r\n',
  `message_key` bigint NOT NULL COMMENT 'messageBodyId',
  `create_time` bigint NULL DEFAULT NULL,
  `sequence` bigint NULL DEFAULT NULL,
  `message_random` int NULL DEFAULT NULL,
  `message_time` bigint NULL DEFAULT NULL COMMENT '来源',
  PRIMARY KEY (`app_id`, `owner_id`, `message_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_message_history
-- ----------------------------
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 353532460924929, 1714878214845, 551, NULL, 1714878213000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 353532817440769, 1714878384514, 552, NULL, 1714878374000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 353535331926017, 1714879583372, 553, NULL, 1714879583000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 353595006386177, 1714908039080, 554, NULL, 1714908038000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 353595585200129, 1714908314576, 555, NULL, 1714908314000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 353595939618817, 1714908483140, 556, NULL, 1714908483000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 353595973173249, 1714908499534, 557, NULL, 1714908499000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359047278624769, 1717507885645, 558, NULL, 1717507884000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359047282819073, 1717507886641, 559, NULL, 1717507886000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359047360413697, 1717507923620, 560, NULL, 1717507923000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359047425425409, 1717507954502, 561, NULL, 1717507954000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359047452688385, 1717507967236, 562, NULL, 1717507967000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359047452688386, 1717507967429, 563, NULL, 1717507967000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359047490437121, 1717507985634, 564, NULL, 1717507985000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359047714832385, 1717508092886, 565, NULL, 1717508092000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359047719026689, 1717508094396, 566, NULL, 1717508094000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359047721123841, 1717508095594, 567, NULL, 1717508095000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359047731609601, 1717508100469, 568, NULL, 1717508100000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359070601052161, 1717519005442, 569, NULL, 1717519005000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359071129534465, 1717519257342, 570, NULL, 1717519257000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359071641239553, 1717519501796, 571, NULL, 1717519501000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359074799550465, 1717521007975, 572, NULL, 1717521007000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359076263362561, 1717521705468, 573, NULL, 1717521705000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359076663918593, 1717521896293, 574, NULL, 1717521896000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359077265801217, 1717522183239, 575, NULL, 1717522183000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359078612172801, 1717522825871, 576, NULL, 1717522825000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359079337787393, 1717523171239, 577, NULL, 1717523171000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359081694986241, 1717524296023, 578, NULL, 1717524295000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359081726443521, 1717524310718, 579, NULL, 1717524310000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359081940353025, 1717524412230, 580, NULL, 1717524412000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359082007461889, 1717524444477, 581, NULL, 1717524444000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359082387046401, 1717524625636, 582, NULL, 1717524625000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359082563207169, 1717524709570, 583, NULL, 1717524709000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359082691133441, 1717524770092, 584, NULL, 1717524770000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082735173633, 1717524791476, 585, NULL, 1717524791000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359082749853697, 1717524798124, 586, NULL, 1717524798000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359082772922369, 1717524809262, 587, NULL, 1717524809000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359082783408129, 1717524814783, 588, NULL, 1717524814000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082814865409, 1717524829097, 589, NULL, 1717524829000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082902945793, 1717524871797, 590, NULL, 1717524871000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082905042945, 1717524872534, 591, NULL, 1717524872000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082905042946, 1717524872663, 592, NULL, 1717524872000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082905042947, 1717524872828, 593, NULL, 1717524872000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082938597377, 1717524888022, 594, NULL, 1717524887000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082938597378, 1717524888280, 595, NULL, 1717524888000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082938597379, 1717524888440, 596, NULL, 1717524888000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082938597380, 1717524888585, 597, NULL, 1717524888000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082938597381, 1717524888760, 598, NULL, 1717524888000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082938597382, 1717524888918, 599, NULL, 1717524888000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082940694529, 1717524889930, 600, NULL, 1717524889000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082942791681, 1717524890101, 601, NULL, 1717524890000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359082942791682, 1717524890495, 602, NULL, 1717524890000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359082951180289, 1717524894801, 603, NULL, 1717524894000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359082970054657, 1717524903818, 604, NULL, 1717524903000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359082982637569, 1717524909350, 605, NULL, 1717524909000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083081203713, 1717524956101, 606, NULL, 1717524956000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083083300865, 1717524957275, 607, NULL, 1717524957000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083085398017, 1717524958268, 608, NULL, 1717524958000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083087495169, 1717524959128, 609, NULL, 1717524959000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083087495170, 1717524959410, 610, NULL, 1717524959000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083087495171, 1717524959608, 611, NULL, 1717524959000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083089592321, 1717524960655, 612, NULL, 1717524959000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083089592322, 1717524960659, 613, NULL, 1717524959000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083089592323, 1717524960677, 614, NULL, 1717524960000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083089592324, 1717524960818, 615, NULL, 1717524960000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083089592325, 1717524960819, 616, NULL, 1717524960000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083102175233, 1717524966387, 617, NULL, 1717524966000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083102175234, 1717524966569, 618, NULL, 1717524966000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083102175235, 1717524967006, 619, NULL, 1717524966000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083106369537, 1717524968043, 620, NULL, 1717524967000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083106369538, 1717524968051, 621, NULL, 1717524967000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083106369539, 1717524968098, 622, NULL, 1717524967000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083112660993, 1717524971548, 623, NULL, 1717524971000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083112660994, 1717524971698, 624, NULL, 1717524971000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083112660995, 1717524971933, 625, NULL, 1717524971000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083114758145, 1717524972184, 626, NULL, 1717524972000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083114758146, 1717524972348, 627, NULL, 1717524972000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083114758147, 1717524972500, 628, NULL, 1717524972000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083123146753, 1717524976151, 629, NULL, 1717524976000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083125243905, 1717524977348, 630, NULL, 1717524977000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083127341057, 1717524978520, 631, NULL, 1717524978000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083129438209, 1717524979420, 632, NULL, 1717524979000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083131535361, 1717524980605, 633, NULL, 1717524980000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083131535362, 1717524980760, 634, NULL, 1717524980000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083131535363, 1717524980913, 635, NULL, 1717524980000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083133632513, 1717524981071, 636, NULL, 1717524981000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083133632514, 1717524981219, 637, NULL, 1717524981000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083135729665, 1717524982764, 638, NULL, 1717524982000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083135729666, 1717524982929, 639, NULL, 1717524982000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083137826817, 1717524983226, 640, NULL, 1717524983000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083137826818, 1717524983412, 641, NULL, 1717524983000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083137826819, 1717524983554, 642, NULL, 1717524983000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083137826820, 1717524983635, 643, NULL, 1717524983000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083137826821, 1717524983786, 644, NULL, 1717524983000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083144118273, 1717524987008, 645, NULL, 1717524986000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083148312577, 1717524988044, 646, NULL, 1717524987000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083148312578, 1717524988046, 647, NULL, 1717524987000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083148312579, 1717524988063, 648, NULL, 1717524987000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083148312580, 1717524988055, 649, NULL, 1717524987000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083148312581, 1717524988136, 650, NULL, 1717524987000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083150409729, 1717524989675, 651, NULL, 1717524989000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083152506881, 1717524990094, 652, NULL, 1717524990000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083152506882, 1717524990234, 653, NULL, 1717524990000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083152506883, 1717524990366, 654, NULL, 1717524990000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083152506884, 1717524990530, 655, NULL, 1717524990000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083154604033, 1717524991602, 656, NULL, 1717524991000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083154604034, 1717524991787, 657, NULL, 1717524991000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083156701185, 1717524992092, 658, NULL, 1717524992000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083156701186, 1717524992244, 659, NULL, 1717524992000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083156701187, 1717524992515, 660, NULL, 1717524992000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083156701188, 1717524992654, 661, NULL, 1717524992000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083158798337, 1717524993571, 662, NULL, 1717524993000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083158798338, 1717524993655, 663, NULL, 1717524993000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083158798339, 1717524993840, 664, NULL, 1717524993000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083160895489, 1717524994058, 665, NULL, 1717524994000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083200741377, 1717525013640, 666, NULL, 1717525013000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359083223810049, 1717525024293, 667, NULL, 1717525024000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359233618968577, 1717596739615, 606, NULL, 1717596738000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359233642037249, 1717596749743, 607, NULL, 1717596749000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359236708073473, 1717598211461, 608, NULL, 1717598211000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359236919885825, 1717598312081, 609, NULL, 1717598312000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359237098143745, 1717598397160, 610, NULL, 1717598397000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359237259624449, 1717598474682, 611, NULL, 1717598474000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359237555322881, 1717598615651, 612, NULL, 1717598615000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359237689540609, 1717598679204, 613, NULL, 1717598679000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359237794398209, 1717598729855, 614, NULL, 1717598729000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359237903450113, 1717598781330, 615, NULL, 1717598781000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359238077513729, 1717598864808, 616, NULL, 1717598864000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359238148816897, 1717598898726, 617, NULL, 1717598898000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359238169788417, 1717598908143, 618, NULL, 1717598908000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359238182371329, 1717598914946, 619, NULL, 1717598914000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359244366872577, 1717601863647, 620, NULL, 1717601863000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359244373164033, 1717601866365, 621, NULL, 1717601866000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359251631407105, 1717605327670, 622, NULL, 1717605327000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359255876042753, 1717607351700, 623, NULL, 1717607351000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359256385650689, 1717607594954, 624, NULL, 1717607594000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359260831612929, 1717609714691, 625, NULL, 1717609714000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359412862550017, 1717682209895, 625, NULL, 1717682208000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359412873035777, 1717682214014, 626, NULL, 1717682213000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359413095333889, 1717682319545, 627, NULL, 1717682319000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359413198094337, 1717682368509, 628, NULL, 1717682368000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359413206482945, 1717682372233, 629, NULL, 1717682372000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359413219065857, 1717682378422, 630, NULL, 1717682378000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359413525250049, 1717682524527, 631, NULL, 1717682524000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359413581873153, 1717682551855, 632, NULL, 1717682551000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359413602844673, 1717682561350, 633, NULL, 1717682561000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359413607038977, 1717682563904, 634, NULL, 1717682563000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359413615427585, 1717682567733, 635, NULL, 1717682567000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359413630107649, 1717682574742, 636, NULL, 1717682574000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359419739111425, 1717685487117, 637, NULL, 1717685487000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359434979115009, 1717692754740, 638, NULL, 1717692754000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 359435042029569, 1717692784094, 639, NULL, 1717692784000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 359435050418177, 1717692788765, 640, NULL, 1717692788000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360087317118977, 1718003814184, 639, NULL, 1718003811000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360310443606017, 1718110209482, 640, NULL, 1718110208000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360310454091777, 1718110213731, 641, NULL, 1718110213000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360317024468993, 1718113346997, 642, NULL, 1718113346000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360495945089025, 1718198663021, 643, NULL, 1718198661000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360496014295041, 1718198695269, 644, NULL, 1718198695000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360497872371713, 1718199581609, 645, NULL, 1718199581000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360670512021505, 1718281902899, 646, NULL, 1718281902000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360670526701569, 1718281909343, 647, NULL, 1718281909000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360670570741761, 1718281930767, 648, NULL, 1718281930000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360670581227521, 1718281935407, 649, NULL, 1718281935000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360670595907585, 1718281942726, 650, NULL, 1718281942000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360670616879105, 1718281952232, 651, NULL, 1718281952000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360670646239233, 1718281966612, 652, NULL, 1718281966000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360670665113601, 1718281975552, 653, NULL, 1718281975000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360670683987969, 1718281984032, 654, NULL, 1718281984000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360670713348097, 1718281998294, 655, NULL, 1718281998000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360671900336129, 1718282564190, 656, NULL, 1718282564000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360671904530433, 1718282566278, 657, NULL, 1718282565000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360671904530434, 1718282566292, 658, NULL, 1718282565000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360671906627585, 1718282567289, 659, NULL, 1718282566000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360671908724737, 1718282568549, 660, NULL, 1718282567000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360671908724738, 1718282568565, 661, NULL, 1718282568000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360671908724739, 1718282568577, 662, NULL, 1718282568000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360671910821889, 1718282569697, 663, NULL, 1718282568000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360671910821890, 1718282569710, 664, NULL, 1718282569000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 360671912919041, 1718282570871, 665, NULL, 1718282569000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672219103233, 1718282716326, 666, NULL, 1718282716000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672221200385, 1718282717367, 667, NULL, 1718282716000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672221200386, 1718282717380, 668, NULL, 1718282716000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672221200387, 1718282717382, 669, NULL, 1718282717000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672223297537, 1718282718474, 670, NULL, 1718282717000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672223297538, 1718282718484, 671, NULL, 1718282717000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672223297539, 1718282718486, 672, NULL, 1718282718000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672223297540, 1718282718638, 673, NULL, 1718282718000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672223297541, 1718282718648, 674, NULL, 1718282718000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672225394689, 1718282719735, 675, NULL, 1718282719000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672225394690, 1718282719761, 676, NULL, 1718282719000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672225394691, 1718282719761, 677, NULL, 1718282719000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672227491841, 1718282720878, 678, NULL, 1718282719000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672231686145, 1718282722543, 679, NULL, 1718282722000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672309280769, 1718282759779, 680, NULL, 1718282759000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672311377921, 1718282760805, 681, NULL, 1718282760000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672311377922, 1718282760818, 682, NULL, 1718282760000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672311377923, 1718282760816, 683, NULL, 1718282760000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672313475073, 1718282761908, 684, NULL, 1718282761000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672315572225, 1718282762976, 685, NULL, 1718282762000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672342835201, 1718282775806, 686, NULL, 1718282775000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672344932353, 1718282776831, 687, NULL, 1718282776000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672344932354, 1718282776841, 688, NULL, 1718282776000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672347029505, 1718282777873, 689, NULL, 1718282777000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672347029506, 1718282777889, 690, NULL, 1718282777000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672351223809, 1718282779978, 691, NULL, 1718282779000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672791625729, 1718282989215, 692, NULL, 1718282989000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672793722881, 1718282990224, 693, NULL, 1718282989000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672793722882, 1718282990224, 694, NULL, 1718282989000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672795820033, 1718282991243, 695, NULL, 1718282990000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672795820034, 1718282991253, 696, NULL, 1718282991000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 360672797917185, 1718282992289, 697, NULL, 1718282991000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361037121454081, 1718456716746, 698, NULL, 1718456714000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361037385695233, 1718456841697, 699, NULL, 1718456841000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361037400375297, 1718456848804, 700, NULL, 1718456848000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 361037427638273, 1718456861639, 701, NULL, 1718456861000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 361037461192705, 1718456877958, 702, NULL, 1718456877000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 361037488455681, 1718456890516, 703, NULL, 1718456890000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361037505232897, 1718456898071, 704, NULL, 1718456898000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361037851262977, 1718457063278, 705, NULL, 1718457062000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361037899497473, 1718457086690, 706, NULL, 1718457086000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '311968820887553', 361240115281921, 1718553510887, 1, NULL, 1718553510000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '331810133245953', '311968820887553', 361240134156289, 1718553519380, 1, NULL, 1718553519000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361240167710721, 1718553535688, 707, NULL, 1718553535000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361240218042369, 1718553559841, 708, NULL, 1718553559000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361240230625281, 1718553565238, 709, NULL, 1718553565000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361240375328769, 1718553634573, 710, NULL, 1718553634000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361240396300289, 1718553644979, 711, NULL, 1718553644000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 361240671027201, 1718553775579, 712, NULL, 1718553775000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361240700387329, 1718553789746, 713, NULL, 1718553789000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '311968820887553', 361241103040513, 1718553981447, 2, NULL, 1718553981000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '311968820887553', 361242220822529, 1718554514385, 3, NULL, 1718554514000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361242789150721, 1718554785605, 714, NULL, 1718554785000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 361242820608001, 1718554800959, 715, NULL, 1718554800000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '311968820887553', '311968820887553', 361242925465601, 1718554850055, 4, NULL, 1718554850000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '311968820887553', 361242975797249, 1718554874395, 5, NULL, 1718554874000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 361243231649793, 1718554996068, 716, NULL, 1718554995000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361243269398529, 1718555014439, 717, NULL, 1718555014000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '311968820887553', '311968820887553', 361243307147265, 1718555032165, 6, NULL, 1718555032000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361243344896001, 1718555050507, 718, NULL, 1718555050000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '311968820887553', 361399054237697, 1718629298590, 719, NULL, 1718629298000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '311968820887553', 361399071014913, 1718629306464, 720, NULL, 1718629306000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '331810133245953', '311968820887553', 361399970693121, 1718629735846, 2, NULL, 1718629735000);
INSERT INTO `im_message_history` VALUES (10000, '331810133245953', '311968820887553', '311968820887553', 361400004247553, 1718629751939, 3, NULL, 1718629751000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '311968820887553', '311968820887553', 361575221297153, 1718713301859, 7, NULL, 1718713300000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '311968820887553', 361575238074369, 1718713309429, 8, NULL, 1718713309000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '311968820887553', 361575254851585, 1718713317224, 9, NULL, 1718713317000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 353532460924929, 1714878214845, 551, NULL, 1714878213000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 353532817440769, 1714878384514, 552, NULL, 1714878374000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 353535331926017, 1714879583372, 553, NULL, 1714879583000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 353595006386177, 1714908039080, 554, NULL, 1714908038000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 353595585200129, 1714908314576, 555, NULL, 1714908314000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 353595939618817, 1714908483140, 556, NULL, 1714908483000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 353595973173249, 1714908499534, 557, NULL, 1714908499000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359047278624769, 1717507885645, 558, NULL, 1717507884000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359047282819073, 1717507886641, 559, NULL, 1717507886000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359047360413697, 1717507923620, 560, NULL, 1717507923000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359047425425409, 1717507954502, 561, NULL, 1717507954000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359047452688385, 1717507967236, 562, NULL, 1717507967000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359047452688386, 1717507967429, 563, NULL, 1717507967000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359047490437121, 1717507985634, 564, NULL, 1717507985000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359047714832385, 1717508092889, 565, NULL, 1717508092000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359047719026689, 1717508094396, 566, NULL, 1717508094000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359047721123841, 1717508095594, 567, NULL, 1717508095000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359047731609601, 1717508100469, 568, NULL, 1717508100000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359070601052161, 1717519005442, 569, NULL, 1717519005000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359071129534465, 1717519257342, 570, NULL, 1717519257000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359071641239553, 1717519501796, 571, NULL, 1717519501000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359074799550465, 1717521007975, 572, NULL, 1717521007000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359076263362561, 1717521705468, 573, NULL, 1717521705000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359076663918593, 1717521896293, 574, NULL, 1717521896000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359077265801217, 1717522183239, 575, NULL, 1717522183000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359078612172801, 1717522825816, 576, NULL, 1717522825000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359079337787393, 1717523171239, 577, NULL, 1717523171000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359081694986241, 1717524296023, 578, NULL, 1717524295000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359081726443521, 1717524310718, 579, NULL, 1717524310000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359081940353025, 1717524412230, 580, NULL, 1717524412000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359082007461889, 1717524444477, 581, NULL, 1717524444000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359082387046401, 1717524625636, 582, NULL, 1717524625000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359082563207169, 1717524709570, 583, NULL, 1717524709000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359082691133441, 1717524770092, 584, NULL, 1717524770000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082735173633, 1717524791476, 585, NULL, 1717524791000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359082749853697, 1717524798124, 586, NULL, 1717524798000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359082772922369, 1717524809262, 587, NULL, 1717524809000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359082783408129, 1717524814783, 588, NULL, 1717524814000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082814865409, 1717524829097, 589, NULL, 1717524829000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082902945793, 1717524871797, 590, NULL, 1717524871000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082905042945, 1717524872534, 591, NULL, 1717524872000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082905042946, 1717524872663, 592, NULL, 1717524872000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082905042947, 1717524872828, 593, NULL, 1717524872000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082938597377, 1717524888022, 594, NULL, 1717524887000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082938597378, 1717524888280, 595, NULL, 1717524888000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082938597379, 1717524888440, 596, NULL, 1717524888000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082938597380, 1717524888585, 597, NULL, 1717524888000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082938597381, 1717524888760, 598, NULL, 1717524888000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082938597382, 1717524888918, 599, NULL, 1717524888000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082940694529, 1717524889930, 600, NULL, 1717524889000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082942791681, 1717524890101, 601, NULL, 1717524890000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359082942791682, 1717524890495, 602, NULL, 1717524890000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359082951180289, 1717524894801, 603, NULL, 1717524894000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359082970054657, 1717524903818, 604, NULL, 1717524903000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359082982637569, 1717524909350, 605, NULL, 1717524909000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083081203713, 1717524956101, 606, NULL, 1717524956000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083083300865, 1717524957275, 607, NULL, 1717524957000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083085398017, 1717524958268, 608, NULL, 1717524958000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083087495169, 1717524959128, 609, NULL, 1717524959000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083087495170, 1717524959410, 610, NULL, 1717524959000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083087495171, 1717524959608, 611, NULL, 1717524959000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083089592321, 1717524960655, 612, NULL, 1717524959000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083089592322, 1717524960659, 613, NULL, 1717524959000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083089592323, 1717524960677, 614, NULL, 1717524960000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083089592324, 1717524960818, 615, NULL, 1717524960000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083089592325, 1717524960819, 616, NULL, 1717524960000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083102175233, 1717524966387, 617, NULL, 1717524966000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083102175234, 1717524966569, 618, NULL, 1717524966000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083102175235, 1717524967006, 619, NULL, 1717524966000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083106369537, 1717524968043, 620, NULL, 1717524967000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083106369538, 1717524968051, 621, NULL, 1717524967000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083106369539, 1717524968098, 622, NULL, 1717524967000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083112660993, 1717524971548, 623, NULL, 1717524971000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083112660994, 1717524971698, 624, NULL, 1717524971000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083112660995, 1717524971933, 625, NULL, 1717524971000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083114758145, 1717524972184, 626, NULL, 1717524972000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083114758146, 1717524972348, 627, NULL, 1717524972000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083114758147, 1717524972500, 628, NULL, 1717524972000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083123146753, 1717524976151, 629, NULL, 1717524976000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083125243905, 1717524977348, 630, NULL, 1717524977000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083127341057, 1717524978520, 631, NULL, 1717524978000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083129438209, 1717524979420, 632, NULL, 1717524979000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083131535361, 1717524980605, 633, NULL, 1717524980000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083131535362, 1717524980760, 634, NULL, 1717524980000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083131535363, 1717524980913, 635, NULL, 1717524980000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083133632513, 1717524981071, 636, NULL, 1717524981000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083133632514, 1717524981219, 637, NULL, 1717524981000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083135729665, 1717524982764, 638, NULL, 1717524982000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083135729666, 1717524982929, 639, NULL, 1717524982000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083137826817, 1717524983226, 640, NULL, 1717524983000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083137826818, 1717524983412, 641, NULL, 1717524983000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083137826819, 1717524983554, 642, NULL, 1717524983000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083137826820, 1717524983635, 643, NULL, 1717524983000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083137826821, 1717524983786, 644, NULL, 1717524983000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083144118273, 1717524987008, 645, NULL, 1717524986000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083148312577, 1717524988044, 646, NULL, 1717524987000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083148312578, 1717524988046, 647, NULL, 1717524987000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083148312579, 1717524988063, 648, NULL, 1717524987000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083148312580, 1717524988055, 649, NULL, 1717524987000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083148312581, 1717524988136, 650, NULL, 1717524987000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083150409729, 1717524989675, 651, NULL, 1717524989000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083152506881, 1717524990094, 652, NULL, 1717524990000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083152506882, 1717524990234, 653, NULL, 1717524990000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083152506883, 1717524990366, 654, NULL, 1717524990000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083152506884, 1717524990530, 655, NULL, 1717524990000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083154604033, 1717524991602, 656, NULL, 1717524991000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083154604034, 1717524991787, 657, NULL, 1717524991000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083156701185, 1717524992092, 658, NULL, 1717524992000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083156701186, 1717524992244, 659, NULL, 1717524992000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083156701187, 1717524992515, 660, NULL, 1717524992000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083156701188, 1717524992654, 661, NULL, 1717524992000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083158798337, 1717524993571, 662, NULL, 1717524993000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083158798338, 1717524993655, 663, NULL, 1717524993000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083158798339, 1717524993840, 664, NULL, 1717524993000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083160895489, 1717524994058, 665, NULL, 1717524994000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083200741377, 1717525013640, 666, NULL, 1717525013000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359083223810049, 1717525024293, 667, NULL, 1717525024000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359233618968577, 1717596739615, 606, NULL, 1717596738000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359233642037249, 1717596749743, 607, NULL, 1717596749000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359236708073473, 1717598211461, 608, NULL, 1717598211000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359236919885825, 1717598312081, 609, NULL, 1717598312000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359237098143745, 1717598397160, 610, NULL, 1717598397000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359237259624449, 1717598474682, 611, NULL, 1717598474000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359237555322881, 1717598615651, 612, NULL, 1717598615000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359237689540609, 1717598679198, 613, NULL, 1717598679000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359237794398209, 1717598729855, 614, NULL, 1717598729000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359237903450113, 1717598781330, 615, NULL, 1717598781000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359238077513729, 1717598864808, 616, NULL, 1717598864000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359238148816897, 1717598898726, 617, NULL, 1717598898000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359238169788417, 1717598908143, 618, NULL, 1717598908000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359238182371329, 1717598914946, 619, NULL, 1717598914000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359244366872577, 1717601863647, 620, NULL, 1717601863000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359244373164033, 1717601866365, 621, NULL, 1717601866000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359251631407105, 1717605327670, 622, NULL, 1717605327000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359255876042753, 1717607351700, 623, NULL, 1717607351000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359256385650689, 1717607594954, 624, NULL, 1717607594000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359260831612929, 1717609714691, 625, NULL, 1717609714000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359412862550017, 1717682209895, 625, NULL, 1717682208000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359412873035777, 1717682214014, 626, NULL, 1717682213000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359413095333889, 1717682319545, 627, NULL, 1717682319000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359413198094337, 1717682368509, 628, NULL, 1717682368000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359413206482945, 1717682372233, 629, NULL, 1717682372000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359413219065857, 1717682378422, 630, NULL, 1717682378000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359413525250049, 1717682524527, 631, NULL, 1717682524000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359413581873153, 1717682551857, 632, NULL, 1717682551000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359413602844673, 1717682561350, 633, NULL, 1717682561000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359413607038977, 1717682563904, 634, NULL, 1717682563000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359413615427585, 1717682567733, 635, NULL, 1717682567000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359413630107649, 1717682574742, 636, NULL, 1717682574000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359419739111425, 1717685487117, 637, NULL, 1717685487000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359434979115009, 1717692754740, 638, NULL, 1717692754000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 359435042029569, 1717692784094, 639, NULL, 1717692784000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 359435050418177, 1717692788765, 640, NULL, 1717692788000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360087317118977, 1718003814184, 639, NULL, 1718003811000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360310443606017, 1718110209482, 640, NULL, 1718110208000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360310454091777, 1718110213731, 641, NULL, 1718110213000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360317024468993, 1718113346997, 642, NULL, 1718113346000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360495945089025, 1718198663021, 643, NULL, 1718198661000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360496014295041, 1718198695269, 644, NULL, 1718198695000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360497872371713, 1718199581609, 645, NULL, 1718199581000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360670512021505, 1718281902899, 646, NULL, 1718281902000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360670526701569, 1718281909343, 647, NULL, 1718281909000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360670570741761, 1718281930767, 648, NULL, 1718281930000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360670581227521, 1718281935407, 649, NULL, 1718281935000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360670595907585, 1718281942726, 650, NULL, 1718281942000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360670616879105, 1718281952232, 651, NULL, 1718281952000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360670646239233, 1718281966612, 652, NULL, 1718281966000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360670665113601, 1718281975550, 653, NULL, 1718281975000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360670683987969, 1718281984032, 654, NULL, 1718281984000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360670713348097, 1718281998294, 655, NULL, 1718281998000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360671900336129, 1718282564190, 656, NULL, 1718282564000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360671904530433, 1718282566278, 657, NULL, 1718282565000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360671904530434, 1718282566292, 658, NULL, 1718282565000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360671906627585, 1718282567289, 659, NULL, 1718282566000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360671908724737, 1718282568549, 660, NULL, 1718282567000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360671908724738, 1718282568565, 661, NULL, 1718282568000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360671908724739, 1718282568577, 662, NULL, 1718282568000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360671910821889, 1718282569697, 663, NULL, 1718282568000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360671910821890, 1718282569710, 664, NULL, 1718282569000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 360671912919041, 1718282570871, 665, NULL, 1718282569000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672219103233, 1718282716326, 666, NULL, 1718282716000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672221200385, 1718282717367, 667, NULL, 1718282716000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672221200386, 1718282717380, 668, NULL, 1718282716000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672221200387, 1718282717382, 669, NULL, 1718282717000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672223297537, 1718282718474, 670, NULL, 1718282717000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672223297538, 1718282718484, 671, NULL, 1718282717000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672223297539, 1718282718486, 672, NULL, 1718282718000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672223297540, 1718282718638, 673, NULL, 1718282718000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672223297541, 1718282718648, 674, NULL, 1718282718000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672225394689, 1718282719735, 675, NULL, 1718282719000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672225394690, 1718282719761, 676, NULL, 1718282719000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672225394691, 1718282719761, 677, NULL, 1718282719000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672227491841, 1718282720878, 678, NULL, 1718282719000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672231686145, 1718282722543, 679, NULL, 1718282722000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672309280769, 1718282759779, 680, NULL, 1718282759000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672311377921, 1718282760805, 681, NULL, 1718282760000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672311377922, 1718282760818, 682, NULL, 1718282760000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672311377923, 1718282760816, 683, NULL, 1718282760000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672313475073, 1718282761908, 684, NULL, 1718282761000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672315572225, 1718282762976, 685, NULL, 1718282762000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672342835201, 1718282775806, 686, NULL, 1718282775000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672344932353, 1718282776831, 687, NULL, 1718282776000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672344932354, 1718282776841, 688, NULL, 1718282776000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672347029505, 1718282777873, 689, NULL, 1718282777000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672347029506, 1718282777889, 690, NULL, 1718282777000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672351223809, 1718282779978, 691, NULL, 1718282779000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672791625729, 1718282989215, 692, NULL, 1718282989000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672793722881, 1718282990224, 693, NULL, 1718282989000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672793722882, 1718282990224, 694, NULL, 1718282989000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672795820033, 1718282991243, 695, NULL, 1718282990000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672795820034, 1718282991253, 696, NULL, 1718282991000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 360672797917185, 1718282992289, 697, NULL, 1718282991000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361037121454081, 1718456716746, 698, NULL, 1718456714000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361037385695233, 1718456841697, 699, NULL, 1718456841000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361037400375297, 1718456848804, 700, NULL, 1718456848000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 361037427638273, 1718456861639, 701, NULL, 1718456861000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 361037461192705, 1718456877958, 702, NULL, 1718456877000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 361037488455681, 1718456890516, 703, NULL, 1718456890000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361037505232897, 1718456898071, 704, NULL, 1718456898000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361037851262977, 1718457063282, 705, NULL, 1718457062000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361037899497473, 1718457086690, 706, NULL, 1718457086000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361240167710721, 1718553535688, 707, NULL, 1718553535000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361240218042369, 1718553559841, 708, NULL, 1718553559000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361240230625281, 1718553565238, 709, NULL, 1718553565000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361240375328769, 1718553634573, 710, NULL, 1718553634000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361240396300289, 1718553644979, 711, NULL, 1718553644000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 361240671027201, 1718553775568, 712, NULL, 1718553775000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361240700387329, 1718553789747, 713, NULL, 1718553789000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '312144459464705', 361240828313601, 1718553850076, 1, NULL, 1718553850000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '312144459464705', 361240834605057, 1718553853910, 2, NULL, 1718553853000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '312144459464705', 361240914296833, 1718553891808, 3, NULL, 1718553891000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '312144459464705', 361240962531329, 1718553914949, 4, NULL, 1718553914000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '312144459464705', 361241518276609, 1718554179461, 5, NULL, 1718554179000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '312144459464705', 361241744769025, 1718554287089, 6, NULL, 1718554287000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '312144459464705', 361242168393729, 1718554490020, 7, NULL, 1718554489000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361242789150721, 1718554785605, 714, NULL, 1718554785000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 361242820608001, 1718554800959, 715, NULL, 1718554800000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '324431782084609', '312144459464705', 361243170832385, 1718554967962, 8, NULL, 1718554967000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '312144459464705', 361243187609601, 1718554975657, 9, NULL, 1718554975000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '312144459464705', 361243206483969, 1718554984452, 10, NULL, 1718554984000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 361243231649793, 1718554996068, 716, NULL, 1718554995000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361243269398529, 1718555014439, 717, NULL, 1718555014000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361243344896001, 1718555050507, 718, NULL, 1718555050000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '311968820887553', '312144459464705', 361399054237697, 1718629298590, 719, NULL, 1718629298000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '312144459464705', '312144459464705', 361399071014913, 1718629306464, 720, NULL, 1718629306000);
INSERT INTO `im_message_history` VALUES (10000, '331810133245953', '324431782084609', '324431782084609', 332055116251137, 1704637018144, 12, NULL, 1704637018000);
INSERT INTO `im_message_history` VALUES (10000, '331813675335681', '324431782084609', '324431782084609', 332055267246081, 1704637090490, 25, NULL, 1704637091000);
INSERT INTO `im_message_history` VALUES (10000, '331813675335681', '324431782084609', '324431782084609', 332055353229313, 1704637131488, 26, NULL, 1704637132000);
INSERT INTO `im_message_history` VALUES (10000, '331813675335681', '324431782084609', '324431782084609', 332055370006529, 1704637139776, 27, NULL, 1704637140000);
INSERT INTO `im_message_history` VALUES (10000, '331813675335681', '324431782084609', '324431782084609', 332055374200833, 1704637141813, 28, NULL, 1704637142000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '331813675335681', '324431782084609', 332055430823937, 1704637168494, 29, NULL, 1704637169000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '331810133245953', '324431782084609', 333274777911297, 1705218599302, 13, NULL, 1705218598000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '324431782084609', 361240115281921, 1718553510887, 1, NULL, 1718553510000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '324431782084609', 361240828313601, 1718553850076, 1, NULL, 1718553850000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '324431782084609', 361240834605057, 1718553853910, 2, NULL, 1718553853000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '324431782084609', 361240914296833, 1718553891808, 3, NULL, 1718553891000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '324431782084609', 361240962531329, 1718553914949, 4, NULL, 1718553914000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '324431782084609', 361241103040513, 1718553981447, 2, NULL, 1718553981000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '324431782084609', 361241518276609, 1718554179461, 5, NULL, 1718554179000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '324431782084609', 361241744769025, 1718554287089, 6, NULL, 1718554287000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '324431782084609', 361242168393729, 1718554490020, 7, NULL, 1718554489000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '324431782084609', 361242220822529, 1718554514385, 3, NULL, 1718554514000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '311968820887553', '324431782084609', 361242925465601, 1718554850055, 4, NULL, 1718554850000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '324431782084609', 361242975797249, 1718554874395, 5, NULL, 1718554874000);
INSERT INTO `im_message_history` VALUES (10000, '312144459464705', '324431782084609', '324431782084609', 361243170832385, 1718554967962, 8, NULL, 1718554967000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '324431782084609', 361243187609601, 1718554975657, 9, NULL, 1718554975000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '312144459464705', '324431782084609', 361243206483969, 1718554984452, 10, NULL, 1718554984000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '311968820887553', '324431782084609', 361243307147265, 1718555032165, 6, NULL, 1718555032000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '311968820887553', '324431782084609', 361575221297153, 1718713301859, 7, NULL, 1718713300000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '324431782084609', 361575238074369, 1718713309429, 8, NULL, 1718713309000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '324431782084609', '324431782084609', 361575254851585, 1718713317224, 9, NULL, 1718713317000);
INSERT INTO `im_message_history` VALUES (10000, '331810133245953', '324431782084609', '331810133245953', 332055116251137, 1704637018144, 12, NULL, 1704637018000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '331810133245953', '331810133245953', 333274777911297, 1705218599302, 13, NULL, 1705218598000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '331810133245953', '331810133245953', 361240134156289, 1718553519380, 1, NULL, 1718553519000);
INSERT INTO `im_message_history` VALUES (10000, '311968820887553', '331810133245953', '331810133245953', 361399970693121, 1718629735846, 2, NULL, 1718629735000);
INSERT INTO `im_message_history` VALUES (10000, '331810133245953', '311968820887553', '331810133245953', 361400004247553, 1718629751939, 3, NULL, 1718629751000);
INSERT INTO `im_message_history` VALUES (10000, '331813675335681', '324431782084609', '331813675335681', 332055267246081, 1704637090490, 25, NULL, 1704637091000);
INSERT INTO `im_message_history` VALUES (10000, '331813675335681', '324431782084609', '331813675335681', 332055353229313, 1704637131488, 26, NULL, 1704637132000);
INSERT INTO `im_message_history` VALUES (10000, '331813675335681', '324431782084609', '331813675335681', 332055370006529, 1704637139776, 27, NULL, 1704637140000);
INSERT INTO `im_message_history` VALUES (10000, '331813675335681', '324431782084609', '331813675335681', 332055374200833, 1704637141813, 28, NULL, 1704637142000);
INSERT INTO `im_message_history` VALUES (10000, '324431782084609', '331813675335681', '331813675335681', 332055430823937, 1704637168494, 29, NULL, 1704637169000);

-- ----------------------------
-- Table structure for im_user_data
-- ----------------------------
DROP TABLE IF EXISTS `im_user_data`;
CREATE TABLE `im_user_data`  (
  `user_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `app_id` int NOT NULL,
  `nick_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `photo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `user_sex` int NULL DEFAULT NULL,
  `birth_day` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '生日',
  `location` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '地址',
  `self_signature` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '个性签名',
  `friend_allow_type` int NOT NULL DEFAULT 1 COMMENT '加好友验证类型（Friend_AllowType） 1无需验证 2需要验证',
  `forbidden_flag` int NOT NULL DEFAULT 0 COMMENT '禁用标识 1禁用',
  `disable_add_friend` int NOT NULL DEFAULT 0 COMMENT '管理员禁止用户添加加好友：0 未禁用 1 已禁用',
  `silent_flag` int NOT NULL DEFAULT 0 COMMENT '禁言标识 1禁言',
  `user_type` int NOT NULL DEFAULT 1 COMMENT '用户类型 1普通用户 2客服 3机器人',
  `del_flag` int NOT NULL DEFAULT 0,
  `extra` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`app_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_user_data
-- ----------------------------
INSERT INTO `im_user_data` VALUES ('311968820887553', 10000, 'jiangjing553', NULL, NULL, NULL, NULL, NULL, '在线 · 最后阅读：三小时前', 2, 0, 0, 0, 1, 0, NULL);
INSERT INTO `im_user_data` VALUES ('312144459464705', 10000, 'jingjing4075', NULL, NULL, NULL, NULL, NULL, '在线 · 最后阅读：三小时前', 2, 0, 0, 0, 1, 0, NULL);
INSERT INTO `im_user_data` VALUES ('324431782084609', 10000, 'test', NULL, NULL, NULL, NULL, NULL, '不负韶华！', 2, 0, 0, 0, 1, 0, NULL);
INSERT INTO `im_user_data` VALUES ('325657571622913', 10000, 'test04', NULL, NULL, NULL, NULL, NULL, '不负韶华！不负韶华！', 2, 0, 0, 0, 1, 0, NULL);
INSERT INTO `im_user_data` VALUES ('331810133245953', 10000, 'test03', NULL, NULL, NULL, NULL, NULL, '在线 · 最后阅读：三小时前', 2, 0, 0, 0, 1, 0, NULL);
INSERT INTO `im_user_data` VALUES ('331813675335681', 10000, 'test05', NULL, NULL, NULL, NULL, NULL, '在线 · 最后阅读：三小时前', 2, 0, 0, 0, 1, 0, NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `password` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `username` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('zs', 'aaaaa', 'zs');

SET FOREIGN_KEY_CHECKS = 1;
