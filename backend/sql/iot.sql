-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: library
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
drop database if exists library;
CREATE DATABASE IF NOT EXISTS library;
--
-- Table structure for table `permission`
--
use library;
DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;

CREATE TABLE `permission` (
  `id` bigint NOT NULL COMMENT '主键',
  `permission_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名',
  `permission_value` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限值',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL COMMENT '主键',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名',
  `role_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色码',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1818533634202714114,'管理员','admin','2024-07-31 14:26:20','2024-07-31 14:26:20'),(1818537563934179329,'普通用户','user','2024-07-31 14:41:57','2024-07-31 14:41:57');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permission` (
  `id` bigint NOT NULL COMMENT '主键',
  `role_id` bigint NOT NULL COMMENT '角色主键',
  `permission_id` bigint NOT NULL COMMENT '权限主键',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT '主键',
  `account` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户名',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户姓名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `invite_code` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邀请码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

ALTER TABLE user ADD UNIQUE INDEX idx_account_unique (account);
CREATE TABLE `user_event` (
                        `id` bigint NOT NULL COMMENT '主键',
                        `account` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户名',
                        `event` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件',
                        PRIMARY KEY (id),
                        FOREIGN KEY (account) REFERENCES user(account) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1818533804097191937,'admin','$2a$10$WQ3TRwDD9.Mp7uJB.J1wZeyp6rcUCvp6clVQ9ro8CuhGGZpuHpIbm','admin','2024-07-31 14:27:00','2024-07-31 14:27:00','FAM-SM31Q1');
INSERT INTO `user` VALUES (2818533804097191937,'@180','$2a$10$MPPPO3yg/eu6v.ABgficAetyLM9bo5l9zl1DBconSZH8/o9PyPaKu','zyh','2024-07-31 14:27:00','2024-07-31 14:27:00','FAM-SM31Q1');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `user_event` WRITE;
/*!40000 ALTER TABLE `user_event` DISABLE KEYS */;
INSERT INTO `user_event` VALUES (181853380409719,'admin','吃饭');
INSERT INTO `user_event` VALUES (081853380409719,'admin','玩火影忍者');
/*!40000 ALTER TABLE `user_event` ENABLE KEYS */;
UNLOCK TABLES;




--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户主键',
  `role_id` bigint NOT NULL COMMENT '角色主键',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1818533804164300802,1818533804097191937,1818533634202714114,'2024-07-31 14:27:00','2024-07-31 14:27:00');
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;


DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
-- CREATE TABLE `device` (
--                          `id` bigint NOT NULL COMMENT '主键',
--                          `device_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名',
--                          `isbn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ISBN号',
--                          `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类别',
--                          `borrowed` boolean NOT NULL COMMENT '借阅状态',
--                          `create_time` datetime NOT NULL COMMENT '创建时间',
--                          `update_time` datetime NOT NULL COMMENT '更新时间',
--                          PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';


CREATE TABLE `device` (
                          `id` INT NOT NULL COMMENT '主键',
                          `device_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名',
                          `device_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ISBN号',
                          `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类别',
                          `status` boolean NOT NULL COMMENT '状态', #0为开启1为关闭
                          `create_time` datetime NOT NULL COMMENT '创建时间',
                          `update_time` datetime NOT NULL COMMENT '更新时间',
                          `detail` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详情',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';



LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO device VALUES (111, '客厅空调', 'AC001', '空调', 1, '2024-07-31 14:27:00', '2024-07-31 14:27:00', '22°C 制冷模式');
INSERT INTO device VALUES (112, '智能门锁', 'LK002', '门锁', 0, '2024-07-31 14:27:00', '2024-07-31 14:27:00', '已锁定');
INSERT INTO device VALUES (113, '安防系统', 'SF003', '安防', 1, '2024-07-31 14:27:00', '2024-07-31 14:27:00', '布防中');
INSERT INTO device VALUES (114, '客厅灯', 'LT004', '灯', 1, '2024-07-31 14:27:00', '2024-07-31 14:27:00', '亮度75%');
INSERT INTO device VALUES (115, '厨房灯', 'KT005', '灯', 0, '2024-07-31 14:27:00', '2024-07-31 14:27:00', '关闭');
INSERT INTO device VALUES (116, '卧室灯', 'BD006', '灯', 1, '2024-07-31 14:27:00', '2024-07-31 14:27:00', '暖光模式');

/*!40000 ALTER TABLE `device` DISABLE KEYS */;
UNLOCK TABLES;

-- LOCK TABLES `aircon_status` WRITE;
-- /*!40000 ALTER TABLE `device` DISABLE KEYS */;
-- INSERT INTO `aircon_status` VALUES (123123124,25,'123-456','空调',1,'2024-07-31 14:27:00','2024-07-31 14:27:00');
-- INSERT INTO `aircon_status` VALUES (123123125,21,'123-457','灯泡',0,'2024-07-31 14:27:00','2024-07-31 14:27:00');
-- /*!40000 ALTER TABLE `device` ENABLE KEYS */;
-- UNLOCK TABLES;
DROP TABLE IF EXISTS aircon;
CREATE TABLE aircon (
                               id INT PRIMARY KEY,
                               temperature DOUBLE NOT NULL,
                               device_id VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备id',

                               mode VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL CHECK (mode IN ('制冷','制热','除湿','送风')),
                               status BOOLEAN NOT NULL DEFAULT FALSE,
                               fan_level INT NOT NULL CHECK (fan_level BETWEEN 1 AND 5),
                               timer INT NOT NULL DEFAULT 0,
                               FOREIGN KEY (id) REFERENCES device(id) ON DELETE CASCADE
#                                FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);
--                                name VARCHAR(10) NOT NULL,

INSERT INTO aircon (
    id, temperature, device_id, mode, status, fan_level, timer
) VALUES (111, 22.0, 'AC001', '制冷', FALSE, 3, 0 );

DROP TABLE if exists `light`;
CREATE TABLE `light` (
                         `id` INT PRIMARY KEY,
                         `device_id` VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                         `status` BOOLEAN DEFAULT 1,
                         `brightness` INT DEFAULT 80,
                         `color_temp` VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '自然',
                         FOREIGN KEY (id) REFERENCES device(id) ON DELETE CASCADE
#                          FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);






INSERT INTO light (id, device_id, status, brightness, color_temp) VALUES
                                                                      (114, 'LT004', 1, 75, '自然'),
                                                                      (115, 'KT005', 0, 0, '自然'),
                                                                      (116, 'BD006', 1, 80, '暖光');


DROP TABLE IF EXISTS `record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `record` (
                          `id` bigint NOT NULL COMMENT '主键',
                          `device_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名',
                          `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '借设备人',
                          `borrow_time` datetime NOT NULL COMMENT '结束时间',
                          `return_time` datetime NOT NULL COMMENT '记录时间',
                          `create_time` datetime NOT NULL COMMENT '创建时间',
                          `update_time` datetime NOT NULL COMMENT '更新时间',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表';

-- # LOCK TABLES `record` WRITE;
-- # /*!40000 ALTER TABLE `record` DISABLE KEYS */;
-- # INSERT INTO `record` VALUES (123123124,'','admin','2024-07-31 14:27:00','2024-08-31 14:27:00','2024-07-31 14:27:00','2024-07-31 14:27:00');
-- # /*!40000 ALTER TABLE `record` ENABLE KEYS */;
-- # UNLOCK TABLES;










--
-- Dumping routines for database 'library'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-07-31 15:43:10


