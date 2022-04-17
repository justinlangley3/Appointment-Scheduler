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
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointment` (
  `appointmentId` int(10) NOT NULL AUTO_INCREMENT,
  `customerId` int(10) NOT NULL,
  `userId` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `summary` text NOT NULL,
  `location` text NOT NULL,
  `contact` text NOT NULL,
  `category` text NOT NULL,
  `url` varchar(255) NOT NULL,
  `beginTime` datetime NOT NULL,
  `endTime` datetime NOT NULL,
  `created` datetime NOT NULL,
  `createdBy` varchar(32) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updatedBy` varchar(32) NOT NULL,
  PRIMARY KEY (`appointmentId`),
  KEY `userId` (`userId`),
  KEY `appointment_ibfk_1` (`customerId`),
  CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `customer` (`customerId`),
  CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointment`
--

LOCK TABLES `appointment` WRITE;
/*!40000 ALTER TABLE `appointment` DISABLE KEYS */;
INSERT INTO `appointment` VALUES (1,1,1,'test','test','test','test','Consultation','test','2021-05-19 13:00:00','2021-05-19 13:15:00','2021-05-18 23:29:27','test','2021-05-18 23:29:27','test'),(2,2,1,'Online museum tour software','The client wants to develop an online tool for exploring their museum from home','Remote','test','Consultation','https://zoom.us/j/1234567890','2021-05-20 13:00:00','2021-05-20 17:30:00','2021-05-18 23:39:05','test','2021-05-18 23:39:05','test'),(3,3,1,'Ticketing software','The client wants to develop a new system for ticketing customers. Special Needs:   - Ability to control how many people are allowed entry at any given time   - Tracker to inform customers of peak \"busy\" hours','Remote','test','Consultation','https://zoom.us/j/1234567890','2021-05-21 13:00:00','2021-05-21 15:30:00','2021-05-18 23:42:57','test','2021-05-19 02:01:30','test'),(4,4,1,'Museum Directory App','The client would like to create a museum directory smartphone app','Remote','test','Consultation','https://zoom.us/j/1234567890','2021-05-22 14:45:00','2021-05-22 16:45:00','2021-05-19 00:01:13','test','2021-05-19 00:01:13','test'),(5,2,1,'Museum Tour Software','Follow-up from the consultation visit to discuss steps moving forward','Remote','test','Deployment','https://zoom.us/j/1234567890','2021-05-24 13:00:00','2021-05-24 14:45:00','2021-05-19 00:03:39','test','2021-05-19 00:06:33','test'),(6,3,1,'Museum Ticketing Software','Follow-up from the initial consultation to discuss steps moving forward','Remote','test','Design','https://zoom.us/j/1234567890','2021-05-25 17:00:00','2021-05-25 19:00:00','2021-05-19 00:04:30','test','2021-05-19 00:04:30','test'),(7,4,1,'Museum Directory Smartphone App','Follow-up from consultation visit to discuss steps moving forward.','Remote','test','Design','https://zoom.us/j/1234567890','2021-05-26 15:45:00','2021-05-26 17:00:00','2021-05-19 00:07:53','test','2021-05-19 00:07:53','test'),(8,2,2,'Museum Explorer Software Design','The client is eager to work with us, we will be meeting to discuss existing application designs for what they want to achieve, capture their feedback, and develop a prototype suitable for their needs.','Remote','Design Team','Design','https://zoom.us/j/1234567890','2021-05-28 14:45:00','2021-05-28 15:00:00','2021-05-19 00:10:49','Design Team','2021-05-19 00:10:49','Design Team'),(9,3,2,'Museum Ticketing Software Design','The client has expressed interest in working with us. The design team will be meeting with them to discuss strategies and approaches to building this software solution, then tailoring to their needs as we receive feedback.','Remote','Design Team','Design','https://zoom.us/j/1234567890','2021-05-27 16:30:00','2021-05-27 19:30:00','2021-05-19 09:19:03','Design Team','2021-05-19 09:19:03','Design Team'),(10,4,2,'Museum Directory App Design','The client would like to move forward. They have offered up some ideas they like that other museums have implemented. We will compare and design a custom UI to fit their needs before moving to the next step. We will adjust from here based on their feedback.','Remote','Design Team','Design','https://zoom.us/j/1234567890','2021-05-31 19:00:00','2021-05-31 21:00:00','2021-05-19 09:21:25','Design Team','2021-05-19 09:21:25','Design Team'),(11,2,3,'Online Museum Explorer','The design meeting went great and the client loved what we had to offer. We will be building a prototype and meeting regularly to receive feedback on our progress.','Remote','Prototyping Team','Prototyping','https://zoom.us/j/1234567890','2021-06-02 14:00:00','2021-06-02 17:30:00','2021-05-19 09:23:54','Prototyping Team','2021-05-19 09:23:54','Prototyping Team'),(12,3,3,'Ticketing Software','The client enjoyed our proposal after working through several iterations. We have the go ahead to initiate the prototyping phase of the project.','Remote','Prototyping Team','Prototyping','https://zoom.us/j/1234567890','2021-06-03 16:15:00','2021-06-03 19:30:00','2021-05-19 09:25:11','Prototyping Team','2021-05-19 09:25:11','Prototyping Team'),(13,4,3,'Museum Directory App Prototype','After our past several meeting we will be moving to the prototyping phase to build a minimum useful application to capture the features the client would like to see. We will iterate from there in the future.','Remote','Prototyping Team','Prototyping','https://zoom.us/j/1234567890','2021-06-04 16:45:00','2021-06-04 18:30:00','2021-05-19 09:26:44','Prototyping Team','2021-05-19 09:26:44','Prototyping Team'),(14,1,3,'In-House Meeting','We will be handing over several projects we started recently to the planning team to get a jump start on building these software solutions at scale.','Remote','Prototyping Team','Planning','https://zoom.us/j/1234567890','2021-06-05 14:15:00','2021-06-05 15:30:00','2021-05-19 09:28:00','Prototyping Team','2021-05-19 09:28:00','Prototyping Team'),(15,2,4,'Heard Museum, Museum Tour Software','We will be meeting with all developers assigned to this project in the Phoenix area, so we may delegate tasks and begin work creating the software solution for the Heard Museum.','Remote','Dev Team','Development','https://zoom.us/j/1234567890','2021-06-07 13:00:00','2021-06-07 15:00:00','2021-05-19 09:31:12','Dev Team','2021-05-19 09:31:12','Dev Team'),(16,3,4,'Gulliver\'s Gate, Ticketing Software','We will be meeting with our developers in the NYC area to delegate tasks and begin work on the client\'s project.','Remote','Dev Team','Development','https://zoom.us/j/1234567890','2021-06-08 13:00:00','2021-06-08 14:30:00','2021-05-19 09:32:22','Dev Team','2021-05-19 09:32:22','Dev Team'),(17,4,4,'Museum of London, Museum Directory App','The planning team has passed us documentation of client requirements for the end result of this product.\nWe will be meeting with developers in the London area to designate work packages to our developer leads, who may then delegate tasks as necessary to our junior developers.','Remote','Dev Team','Development','https://zoom.us/j/1234567890','2021-06-09 15:30:00','2021-06-09 17:30:00','2021-05-19 09:34:41','Dev Team','2021-05-19 09:39:31','Dev Team'),(18,2,5,'Heard Museum Delivery','All work packages have been completed and we will be handing over all deliverables to the Heard Museum.','Remote','Deployment','Deployment','https://zoom.us/j/1234567890','2021-06-14 14:45:00','2021-06-14 16:30:00','2021-05-19 09:37:34','Deployment','2021-05-19 09:37:34','Deployment'),(19,3,5,'Gulliver\'s Gate Delivery','All work packages have been completed for the client sponsored project and we will be handing over all deliverables.','Remote','Deployment','Deployment','https://zoom.us/j/1234567890','2021-06-16 13:30:00','2021-06-16 15:00:00','2021-05-19 09:38:35','Deployment','2021-05-19 09:38:35','Deployment'),(20,4,5,'Museum of London, App Delivery','All work packages have been completed and we will be handing over all deliverables to the client.','Remote','Deployment','Deployment','https://zoom.us/j/1234567890','2021-06-18 14:15:00','2021-06-18 16:00:00','2021-05-19 09:40:44','Deployment','2021-05-19 09:40:44','Deployment');
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-05-19  6:03:10
