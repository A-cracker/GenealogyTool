-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: genealogy
-- ------------------------------------------------------
-- Server version	5.7.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `uploader_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `family`
--

DROP TABLE IF EXISTS `family`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `family` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '标识id',
  `treeId` bigint(20) unsigned NOT NULL,
  `memberId` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `family_memberId_foreign` (`memberId`)
) ENGINE=InnoDB AUTO_INCREMENT=1379 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lunar`
--

DROP TABLE IF EXISTS `lunar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lunar` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` varchar(45) DEFAULT NULL COMMENT '公历 年',
  `lunar_year` varchar(45) DEFAULT NULL COMMENT '农历 年',
  `dynasty` varchar(10) DEFAULT NULL COMMENT '朝代',
  `reign_title` varchar(45) DEFAULT NULL COMMENT '年号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1476 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `member` (
  `id` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL COMMENT '姓名',
  `gender` tinyint(4) NOT NULL DEFAULT '1' COMMENT '性别,0:女,1:男',
  `birthdate` date DEFAULT NULL COMMENT '出生日期',
  `birthplace` varchar(255) DEFAULT NULL COMMENT '出生地',
  `restplace` varchar(255) DEFAULT NULL COMMENT '安息地',
  `is_alive` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0:不在世,1:在世',
  `description` varchar(2000) DEFAULT NULL COMMENT '介绍',
  `img` varchar(40) DEFAULT NULL COMMENT '照片, 大文件ID',
  `generation` varchar(255) DEFAULT NULL COMMENT '字辈',
  `identityId` varchar(100) DEFAULT NULL COMMENT '身份证号',
  `residence` varchar(100) DEFAULT NULL COMMENT '现居住地',
  `phoneNumber` varchar(100) DEFAULT NULL COMMENT '手机号',
  `isUser` int(10) unsigned DEFAULT NULL,
  `deathDate` date DEFAULT NULL,
  `open_identity` tinyint(4) DEFAULT '1' COMMENT '公开身份证: 0:公开,1:后四位,2:不公开',
  `open_phone` tinyint(4) DEFAULT '1' COMMENT '公开手机: 0:公开,1:后四位,2:不公开',
  `album_id` bigint(20) DEFAULT NULL COMMENT '影像库相册ID',
  `birthdate_type` tinyint(4) DEFAULT '0' COMMENT '生日日期类型: 0: 年月日, 1:朝代',
  `deathdate_type` tinyint(4) DEFAULT '0' COMMENT '生日日期类型: 0: 年月日, 1:朝代',
  `lunar_birthdate` varchar(45) DEFAULT NULL COMMENT '农历生日',
  `lunar_deathdate` varchar(45) DEFAULT NULL COMMENT '农历忌日',
  `creator_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `member_FK` (`isUser`),
  CONSTRAINT `member_FK` FOREIGN KEY (`isUser`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pedigree`
--

DROP TABLE IF EXISTS `pedigree`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedigree` (
  `id` bigint(20) unsigned NOT NULL COMMENT '族谱id',
  `doisser` varchar(255) DEFAULT NULL COMMENT '卷宗',
  `faction` varchar(255) DEFAULT NULL COMMENT '派系',
  `branch` varchar(255) DEFAULT NULL COMMENT '房支',
  `name` varchar(255) NOT NULL COMMENT '家谱名',
  `originPlace` varchar(255) DEFAULT NULL COMMENT '谱籍地',
  `description` varchar(255) DEFAULT NULL COMMENT '家谱简介',
  `create_time` datetime DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relation`
--

DROP TABLE IF EXISTS `relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `u1` bigint(20) unsigned NOT NULL,
  `u2` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL,
  `treeId` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `relation_u1_foreign` (`u1`),
  KEY `relation_u2_foreign` (`u2`)
) ENGINE=InnoDB AUTO_INCREMENT=1733 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '姓名',
  `gender` tinyint(4) NOT NULL DEFAULT '1' COMMENT '性别,0:女,1:男',
  `birthdate` date DEFAULT NULL COMMENT '出生日期',
  `birthplace` varchar(255) DEFAULT NULL COMMENT '出生地',
  `description` varchar(255) DEFAULT NULL COMMENT '介绍',
  `img` mediumblob COMMENT '照片',
  `generation` varchar(255) DEFAULT NULL COMMENT '字辈',
  `identityId` varchar(100) NOT NULL COMMENT '身份证号',
  `residence` varchar(100) DEFAULT NULL COMMENT '现居住地',
  `phoneNumber` varchar(100) DEFAULT NULL COMMENT '手机号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4749756 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-03-08 18:55:06
