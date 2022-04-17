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
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `addressId` int(10) NOT NULL AUTO_INCREMENT,
  `address` varchar(64) NOT NULL,
  `address2` varchar(64) DEFAULT NULL,
  `cityId` int(10) NOT NULL,
  `postalCode` varchar(32) NOT NULL,
  `phone` varchar(32) NOT NULL,
  `created` datetime NOT NULL,
  `createdBy` varchar(32) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updatedBy` varchar(32) NOT NULL,
  PRIMARY KEY (`addressId`),
  KEY `cityId` (`cityId`),
  CONSTRAINT `address_ibfk_1` FOREIGN KEY (`cityId`) REFERENCES `city` (`cityId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'test','test',1,'test','test','2021-05-18 23:28:58','test','2021-05-18 23:28:58','test'),(2,'Heard Museum','2301 North Central Avenue',2,'85004','+1 (602) 252 8840','2021-05-18 23:32:19','test','2021-05-18 23:32:19','test'),(3,'Gulliver\'s Gate','216 W 44th St',3,'10036','+1 (212) 235 2016','2021-05-18 23:33:58','test','2021-05-18 23:33:58','test'),(4,'Museum of London','150 London Wall, Barbican',4,'EC2Y 5HN','+44 20 7001 9844','2021-05-18 23:34:48','test','2021-05-18 23:34:48','test');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-05-19  6:03:08
