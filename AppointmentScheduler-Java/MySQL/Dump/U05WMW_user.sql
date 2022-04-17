CREATE DATABASE  IF NOT EXISTS `U05WMW` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `U05WMW`;
-- MySQL dump 10.13  Distrib 5.7.33, for Win64 (x86_64)
--
-- Host: 3.227.166.251    Database: U05WMW
-- ------------------------------------------------------
-- Server version	5.7.34-0ubuntu0.18.04.1

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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `userId` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `pword` varbinary(64) NOT NULL,
  `salt` varbinary(64) NOT NULL,
  `active` tinyint(1) NOT NULL,
  `created` datetime NOT NULL,
  `createdBy` varchar(32) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updatedBy` varchar(32) NOT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'test',_binary '80aaa1e720c5c5e3fc301c1e6feb8f5614d56d59e8da41278bd35caa7ada8180',_binary '86ab680d2db9d3239f59376313d83c909de90a9c5f4ef8810c92847cc3e13650',1,'2021-05-18 23:28:28','test','2021-05-18 23:28:28','test'),(2,'Design Team',_binary 'c6aecbd17fc8127358da1fc1bab0d6a55109703eaa683a439b9d6fa34ab84d34',_binary 'c45a3b7cf233436f80e1fb212a7ad6b715c3dc73eb7229eeb6fa831f668687d4',1,'2021-05-19 00:08:34','Design Team','2021-05-19 00:08:34','Design Team'),(3,'Prototyping Team',_binary 'f858cb6b78a2a7abc0ee5aed5a2b5e352e2ae39446e976830fb03ae51598cacc',_binary '3c26fe20eae892b8502667cc3fd88fd01185779248bd9724a99f598189cce891',1,'2021-05-19 09:22:08','Prototyping Team','2021-05-19 09:22:08','Prototyping Team'),(4,'Dev Team',_binary '95aa91d35036bf9b27961f9209da6fa862465542fead2c2ce74c0c22f98392c1',_binary '9a797791e6cb2fe90f5388ab7b3e9ccce7e3bd5d2192c8a978bc49f2023faef',1,'2021-05-19 09:29:03','Dev Team','2021-05-19 09:29:03','Dev Team'),(5,'Deployment',_binary '31749c2dfdb3a6a2aff78947211bf52dd3b90659478e1580a2155859bdefa9cf',_binary '8c9f1aedc70023c546a2ce84fb554a1ed0a0807a5098bf7370843e58f8eddb3c',1,'2021-05-19 09:35:29','Deployment','2021-05-19 09:35:29','Deployment');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-05-19  6:03:03
