CREATE DATABASE  IF NOT EXISTS `stdmgr` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `stdmgr`;
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: stdmgr
-- ------------------------------------------------------
-- Server version	5.5.60

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
-- Table structure for table `absence_apply`
--

DROP TABLE IF EXISTS `absence_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `absence_apply` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `student_id` varchar(10) NOT NULL,
  `to_teacher_id` varchar(10) NOT NULL,
  `type` varchar(10) NOT NULL,
  `begin` datetime NOT NULL,
  `end` datetime NOT NULL,
  `cause` varchar(100) DEFAULT NULL,
  `approval` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='请假单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `absence_apply`
--

LOCK TABLES `absence_apply` WRITE;
/*!40000 ALTER TABLE `absence_apply` DISABLE KEYS */;
INSERT INTO `absence_apply` VALUES (3,'1','1','病假','2018-07-20 15:23:00','2018-07-20 16:23:00','',NULL),(4,'1','1','病假','2018-07-21 03:06:00','2018-07-21 04:06:00','I\'m sick.',NULL),(5,'1','1','病假','2018-07-21 09:15:00','2018-07-21 11:15:00','I\'m sick.',NULL),(6,'1','1','病假','2018-07-21 09:22:00','2018-07-21 10:22:00','',NULL),(7,'1','1','病假','2018-07-21 09:41:00','2018-07-21 10:41:00','feel sick.',NULL),(8,'1','1','事假','2018-07-21 22:18:00','2018-07-22 00:18:00','不舒服',NULL);
/*!40000 ALTER TABLE `absence_apply` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-07-21 22:25:21
