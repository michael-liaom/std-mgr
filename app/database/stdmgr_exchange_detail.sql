CREATE DATABASE  IF NOT EXISTS `stdmgr` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `stdmgr`;
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: LocalHost    Database: stdmgr
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
-- Table structure for table `exchange_detail`
--

DROP TABLE IF EXISTS `exchange_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exchange_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `subject_id` int(11) NOT NULL,
  `direction` varchar(4) NOT NULL,
  `content` varchar(255) NOT NULL,
  `create` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='学生、辅导员交流细节';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exchange_detail`
--

LOCK TABLES `exchange_detail` WRITE;
/*!40000 ALTER TABLE `exchange_detail` DISABLE KEYS */;
INSERT INTO `exchange_detail` VALUES (1,4,'from','很多人都喜欢养狗，小编曾经在论坛中看到一句话，很形象地描述了养狗的人。他说：养狗是中国人劣根性的表现！这样一句话，小编看上去也会感觉非常的不舒服，但是，他为什么会这么说？养狗是为了什么','2018-08-16 12:43:31'),(2,5,'to','测试\n很多人都喜欢养狗，小编曾经在论坛中看到一句话，很形象地描述了养狗的人。他说：养狗是中国人劣根性的表现！这样一句话，小编看上去也会感觉非常的不舒服，但是，他为什么会这么说？养狗是为了什么','2018-08-16 12:57:16'),(4,5,'to','很多人都喜欢养狗，小编曾经在论坛中看到一句话，很形象地描述了养狗的人。他说：养狗是中国人劣根性的表现！这样一句话，小编看上去也会感觉非常的不舒服，但是，他为什么会这么说？养狗是为了什么','2018-08-16 16:35:57'),(5,5,'to','很多人都喜欢养狗，小编曾经在论坛中看到一句话，很形象地描述了养狗的人。他说：养狗是中国人劣根性的表现！这样一句话，小编看上去也会感觉非常的不舒服，但是，他为什么会这么说？养狗是为了什么','2018-08-16 16:36:02'),(6,5,'to','很多人都喜欢养狗，小编曾经在论坛中看到一句话，很形象地描述了养狗的人。他说：养狗是中国人劣根性的表现！这样一句话，小编看上去也会感觉非常的不舒服，但是，他为什么会这么说？养狗是为了什么','2018-08-16 16:36:43'),(7,5,'to','很多人都喜欢养狗，小编曾经在论坛中看到一句话，很形象地描述了养狗的人。他说：养狗是中国人劣根性的表现！这样一句话，小编看上去也会感觉非常的不舒服，但是，他为什么会这么说？养狗是为了什么','2018-08-16 16:38:13'),(8,5,'to','很多人都喜欢养狗，小编曾经在论坛中看到一句话，很形象地描述了养狗的人。他说：养狗是中国人劣根性的表现！这样一句话，小编看上去也会感觉非常的不舒服，但是，他为什么会这么说？养狗是为了什么','2018-08-16 16:38:25'),(9,5,'to','很多人都喜欢养狗，小编曾经在论坛中看到一句话，很形象地描述了养狗的人。他说：养狗是中国人劣根性的表现！这样一句话，小编看上去也会感觉非常的不舒服，但是，他为什么会这么说？养狗是为了什么','2018-08-16 16:38:25'),(10,5,'to','测试','2018-08-16 17:18:01'),(11,5,'from','测试2','2018-08-16 17:21:25'),(12,5,'from','测试3','2018-08-16 17:43:11'),(13,5,'from','测试4','2018-08-16 18:16:14');
/*!40000 ALTER TABLE `exchange_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-08-16 18:46:07
