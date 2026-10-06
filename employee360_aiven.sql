-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: employee360
-- ------------------------------------------------------
-- Server version	8.0.46

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

--
-- Current Database: `employee360`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `employee360` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `defaultdb`;

--
-- Table structure for table `approval_step`
--

DROP TABLE IF EXISTS `approval_step`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_step` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approver_role` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `step_order` int NOT NULL,
  `approver_id` bigint DEFAULT NULL,
  `leave_request_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKblijx9w3ldbfs5a7wlw0l9xx6` (`approver_id`),
  KEY `FK7pvc5v5o3l80c6sje2p03200l` (`leave_request_id`),
  CONSTRAINT `FK7pvc5v5o3l80c6sje2p03200l` FOREIGN KEY (`leave_request_id`) REFERENCES `leave_request` (`id`),
  CONSTRAINT `FKblijx9w3ldbfs5a7wlw0l9xx6` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_step`
--

LOCK TABLES `approval_step` WRITE;
/*!40000 ALTER TABLE `approval_step` DISABLE KEYS */;
INSERT INTO `approval_step` VALUES (1,'MANAGER','APPROVED',1,1,1),(2,'MANAGER','REJECTED',1,1,2),(3,'MANAGER','APPROVED',1,1,3),(4,'HR','APPROVED',2,3,3),(5,'MANAGER','REJECTED',1,1,4),(6,'HR','WAITING',2,3,4),(7,'MANAGER','APPROVED',1,1,5),(8,'MANAGER','REJECTED',1,1,6),(9,'MANAGER','APPROVED',1,5,7),(10,'DEPARTMENT_HEAD','APPROVED',2,4,7),(11,'HR','APPROVED',3,3,7),(12,'MANAGER','APPROVED',1,5,8),(13,'DEPARTMENT_HEAD','REJECTED',2,4,8),(14,'HR','WAITING',3,3,8),(16,'MANAGER','APPROVED',1,5,10),(17,'MANAGER','PENDING',1,5,11),(18,'MANAGER','APPROVED',1,5,12),(19,'DEPARTMENT_HEAD','APPROVED',2,4,12),(20,'HR','APPROVED',3,3,12),(21,'MANAGER','APPROVED',1,5,13),(22,'DEPARTMENT_HEAD','REJECTED',2,4,13),(23,'HR','WAITING',3,3,13),(24,'MANAGER','APPROVED',1,5,14),(25,'DEPARTMENT_HEAD','REJECTED',2,4,14),(26,'HR','WAITING',3,3,14),(27,'MANAGER','APPROVED',1,5,15),(28,'DEPARTMENT_HEAD','REJECTED',2,4,15),(29,'HR','WAITING',3,3,15),(30,'MANAGER','APPROVED',1,5,16),(31,'HR','PENDING',2,3,16),(32,'MANAGER','APPROVED',1,5,17),(33,'DEPARTMENT_HEAD','APPROVED',2,4,17),(34,'HR','APPROVED',3,3,17),(35,'MANAGER','APPROVED',1,5,18),(36,'DEPARTMENT_HEAD','REJECTED',2,4,18),(37,'HR','WAITING',3,3,18);
/*!40000 ALTER TABLE `approval_step` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) DEFAULT NULL,
  `new_status` varchar(255) DEFAULT NULL,
  `previous_status` varchar(255) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `leave_request_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqhb8xnofj5qy45mula2h4kkv3` (`leave_request_id`),
  KEY `FKqir5ob5q1x1w30jk4j3e4p58c` (`user_id`),
  CONSTRAINT `FKqhb8xnofj5qy45mula2h4kkv3` FOREIGN KEY (`leave_request_id`) REFERENCES `leave_request` (`id`),
  CONSTRAINT `FKqir5ob5q1x1w30jk4j3e4p58c` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,'FINAL_APPROVAL','APPROVED','PENDING','2026-09-16 22:13:44.313987',1,1),(2,'REJECTED','REJECTED','PENDING','2026-09-16 22:16:16.716994',2,1),(3,'APPROVED_STEP','APPROVED','PENDING','2026-09-17 23:53:57.065220',3,1),(4,'FINAL_APPROVAL','APPROVED','PENDING','2026-09-17 23:54:26.618127',3,3),(5,'REJECTED','REJECTED','PENDING','2026-09-17 23:57:05.675913',4,1),(6,'FINAL_APPROVAL','APPROVED','PENDING','2026-09-18 12:28:34.913994',5,1),(7,'REJECTED','REJECTED','PENDING','2026-09-18 12:44:53.517204',6,1),(8,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 14:52:59.343820',7,5),(9,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 15:02:02.698029',7,4),(10,'FINAL_APPROVAL','APPROVED','PENDING','2026-09-18 15:04:05.949899',7,3),(11,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 15:06:13.511791',8,5),(12,'REJECTED','REJECTED','PENDING','2026-09-18 15:07:22.267052',8,4),(13,'FINAL_APPROVAL','APPROVED','PENDING','2026-09-18 19:16:40.236289',10,5),(14,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 22:28:31.871939',12,5),(15,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 22:28:32.324332',12,4),(16,'FINAL_APPROVAL','APPROVED','PENDING','2026-09-18 22:28:32.794141',12,3),(17,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 22:28:33.548241',13,5),(18,'REJECTED','REJECTED','PENDING','2026-09-18 22:28:33.644602',13,4),(19,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 22:30:07.389880',14,5),(20,'REJECTED','REJECTED','PENDING','2026-09-18 22:30:07.424126',14,4),(21,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 22:31:02.510734',15,5),(22,'REJECTED','REJECTED','PENDING','2026-09-18 22:31:02.536013',15,4),(23,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 22:31:51.448270',16,5),(24,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 22:32:49.532823',17,5),(25,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 22:32:49.679828',17,4),(26,'FINAL_APPROVAL','APPROVED','PENDING','2026-09-18 22:32:49.839826',17,3),(27,'APPROVED_STEP','APPROVED','PENDING','2026-09-18 22:32:50.136989',18,5),(28,'REJECTED','REJECTED','PENDING','2026-09-18 22:32:50.161967',18,4);
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES (1,'Computer Science'),(2,'Engineering');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `holiday`
--

DROP TABLE IF EXISTS `holiday`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `holiday` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `holiday`
--

LOCK TABLES `holiday` WRITE;
/*!40000 ALTER TABLE `holiday` DISABLE KEYS */;
INSERT INTO `holiday` VALUES (1,'2026-12-15','Company Foundation Day');
/*!40000 ALTER TABLE `holiday` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leave_balance`
--

DROP TABLE IF EXISTS `leave_balance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_balance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `remaining_leaves` int NOT NULL,
  `total_leaves` int NOT NULL,
  `used_leaves` int NOT NULL,
  `leave_type_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr7fmdsbyl1l02pt10gdvgkkrq` (`leave_type_id`),
  KEY `FKj25ux8lenkff437dsl0dcgnd` (`user_id`),
  CONSTRAINT `FKj25ux8lenkff437dsl0dcgnd` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKr7fmdsbyl1l02pt10gdvgkkrq` FOREIGN KEY (`leave_type_id`) REFERENCES `leave_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leave_balance`
--

LOCK TABLES `leave_balance` WRITE;
/*!40000 ALTER TABLE `leave_balance` DISABLE KEYS */;
INSERT INTO `leave_balance` VALUES (1,3,12,9,1,2),(2,6,12,6,1,6);
/*!40000 ALTER TABLE `leave_balance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leave_request`
--

DROP TABLE IF EXISTS `leave_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_date` date DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `leave_type_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `rejection_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbsy0iudb8fxpkpoat8bjr29xl` (`leave_type_id`),
  KEY `FK28ykte0n73edocnb1phrnqo3s` (`user_id`),
  CONSTRAINT `FK28ykte0n73edocnb1phrnqo3s` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKbsy0iudb8fxpkpoat8bjr29xl` FOREIGN KEY (`leave_type_id`) REFERENCES `leave_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leave_request`
--

LOCK TABLES `leave_request` WRITE;
/*!40000 ALTER TABLE `leave_request` DISABLE KEYS */;
INSERT INTO `leave_request` VALUES (1,'2026-09-23','Personal work','2026-09-21','APPROVED',1,2,NULL),(2,'2026-09-29','Personal work','2026-09-28','REJECTED',1,2,'Leave cannot be approved due to project requirements.'),(3,'2026-10-08','Family function','2026-10-05','APPROVED',1,2,NULL),(4,'2026-10-15','Medical appointment','2026-10-12','REJECTED',1,2,'Leave cannot be approved due to project requirements.'),(5,'2026-09-25','Personal work','2026-09-24','APPROVED',1,2,NULL),(6,'2026-11-03','Personal work','2026-11-02','REJECTED',1,2,'Leave cannot be approved due to business requirements.'),(7,'2026-12-14','Family function','2026-12-07','APPROVED',1,6,NULL),(8,'2026-12-23','Personal work','2026-12-16','REJECTED',1,6,'Leave cannot be approved due to project requirements.'),(10,'2026-12-29','Personal work','2026-12-28','APPROVED',1,6,NULL),(11,'2027-01-05','personal work','2027-01-04','PENDING',1,6,NULL),(12,'2026-10-19','Core approval workflow verification 6 working days','2026-10-12','APPROVED',1,6,NULL),(13,'2026-11-09','Rejection workflow verification 6 working days','2026-11-02','REJECTED',1,6,'Project deadline conflict'),(14,'2026-11-09','Rejection workflow verification 6 working days','2026-11-02','REJECTED',1,6,'Project deadline conflict'),(15,'2026-11-09','Rejection workflow verification 6 working days','2026-11-02','REJECTED',1,6,'Project deadline conflict'),(16,'2026-12-22','Rejection workflow verification 6 working days','2026-12-15','PENDING',1,6,NULL),(17,'2027-02-08','Core approval workflow verification 6 working days','2027-02-01','APPROVED',1,6,NULL),(18,'2027-03-08','Rejection workflow verification 6 working days','2027-03-01','REJECTED',1,6,'Project deadline conflict');
/*!40000 ALTER TABLE `leave_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leave_type`
--

DROP TABLE IF EXISTS `leave_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `entitlement` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `max_consecutive_leave` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leave_type`
--

LOCK TABLES `leave_type` WRITE;
/*!40000 ALTER TABLE `leave_type` DISABLE KEYS */;
INSERT INTO `leave_type` VALUES (1,12,'Casual Leave',NULL),(2,15,'Sick Leave',5);
/*!40000 ALTER TABLE `leave_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) NOT NULL,
  `message` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `leave_request_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh0pedrbuojoq0r5xi5jcgob2g` (`leave_request_id`),
  KEY `FKb0yvoep4h4k92ipon31wmdf7e` (`user_id`),
  CONSTRAINT `FKb0yvoep4h4k92ipon31wmdf7e` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKh0pedrbuojoq0r5xi5jcgob2g` FOREIGN KEY (`leave_request_id`) REFERENCES `leave_request` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,'2026-09-16 22:13:44.323220',_binary '\0','Your leave request has been fully approved.','LEAVE_APPROVED',1,2),(2,'2026-09-16 22:16:16.718993',_binary '\0','Your leave request has been rejected.','LEAVE_REJECTED',2,2),(3,'2026-09-17 23:53:57.033228',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',3,3),(4,'2026-09-17 23:54:26.625122',_binary '\0','Your leave request has been fully approved.','LEAVE_APPROVED',3,2),(5,'2026-09-17 23:57:05.684545',_binary '\0','Your leave request has been rejected.','LEAVE_REJECTED',4,2),(6,'2026-09-18 12:28:34.931413',_binary '\0','Your leave request has been fully approved.','LEAVE_APPROVED',5,2),(7,'2026-09-18 12:44:53.520208',_binary '\0','Your leave request has been rejected.','LEAVE_REJECTED',6,2),(8,'2026-09-18 14:52:59.329257',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',7,4),(9,'2026-09-18 15:02:02.676495',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',7,3),(10,'2026-09-18 15:04:05.953416',_binary '\0','Your leave request has been fully approved.','LEAVE_APPROVED',7,6),(11,'2026-09-18 15:06:13.507633',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',8,4),(12,'2026-09-18 15:07:22.271031',_binary '\0','Your leave request has been rejected.','LEAVE_REJECTED',8,6),(13,'2026-09-18 19:16:40.248197',_binary '\0','Your leave request has been fully approved.','LEAVE_APPROVED',10,6),(14,'2026-09-18 22:28:31.854097',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',12,4),(15,'2026-09-18 22:28:32.319003',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',12,3),(16,'2026-09-18 22:28:32.799617',_binary '\0','Your leave request has been fully approved.','LEAVE_APPROVED',12,6),(17,'2026-09-18 22:28:33.541974',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',13,4),(18,'2026-09-18 22:28:33.649410',_binary '\0','Your leave request has been rejected.','LEAVE_REJECTED',13,6),(19,'2026-09-18 22:30:07.386447',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',14,4),(20,'2026-09-18 22:30:07.428114',_binary '\0','Your leave request has been rejected.','LEAVE_REJECTED',14,6),(21,'2026-09-18 22:31:02.508594',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',15,4),(22,'2026-09-18 22:31:02.538659',_binary '\0','Your leave request has been rejected.','LEAVE_REJECTED',15,6),(23,'2026-09-18 22:31:51.445474',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',16,3),(24,'2026-09-18 22:32:49.529248',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',17,4),(25,'2026-09-18 22:32:49.677706',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',17,3),(26,'2026-09-18 22:32:49.843208',_binary '\0','Your leave request has been fully approved.','LEAVE_APPROVED',17,6),(27,'2026-09-18 22:32:50.134995',_binary '\0','A leave request is waiting for your approval.','APPROVAL_REQUIRED',18,4),(28,'2026-09-18 22:32:50.163588',_binary '\0','Your leave request has been rejected.','LEAVE_REJECTED',18,6);
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `department_id` bigint DEFAULT NULL,
  `manager_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgkh2fko1e4ydv1y6vtrwdc6my` (`department_id`),
  KEY `FKl9blkgio1nb00hot7kaxoy7q9` (`manager_id`),
  CONSTRAINT `FKgkh2fko1e4ydv1y6vtrwdc6my` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `FKl9blkgio1nb00hot7kaxoy7q9` FOREIGN KEY (`manager_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'John Manager','$2a$10$.LQ7sCJphhdWHu8ZC8b2sOaXqaf2mNmAJNPCl/UXwf/MflbFUyZb2','MANAGER','manager1',1,NULL),(2,'Prakash Employee','$2a$10$.LQ7sCJphhdWHu8ZC8b2sOaXqaf2mNmAJNPCl/UXwf/MflbFUyZb2','EMPLOYEE','employee1',1,1),(3,'HR Admin','$2a$10$.LQ7sCJphhdWHu8ZC8b2sOaXqaf2mNmAJNPCl/UXwf/MflbFUyZb2','HR','hr1',1,NULL),(4,'Engineering Head','$2a$10$.LQ7sCJphhdWHu8ZC8b2sOaXqaf2mNmAJNPCl/UXwf/MflbFUyZb2','DEPARTMENT_HEAD','enghead1',2,NULL),(5,'Engineering Manager','$2a$10$.LQ7sCJphhdWHu8ZC8b2sOaXqaf2mNmAJNPCl/UXwf/MflbFUyZb2','MANAGER','engmanager1',2,4),(6,'Engineering Employee','$2a$10$.LQ7sCJphhdWHu8ZC8b2sOaXqaf2mNmAJNPCl/UXwf/MflbFUyZb2','EMPLOYEE','engemployee1',2,5),(7,'System Admin','$2a$10$.LQ7sCJphhdWHu8ZC8b2sOaXqaf2mNmAJNPCl/UXwf/MflbFUyZb2','ADMIN','admin1',1,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow_rule`
--

DROP TABLE IF EXISTS `workflow_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workflow_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approval_level` int NOT NULL,
  `approver_role` varchar(255) DEFAULT NULL,
  `max_days` int DEFAULT NULL,
  `min_days` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workflow_rule`
--

LOCK TABLES `workflow_rule` WRITE;
/*!40000 ALTER TABLE `workflow_rule` DISABLE KEYS */;
INSERT INTO `workflow_rule` VALUES (1,1,'MANAGER',3,1),(2,1,'MANAGER',5,4),(3,2,'HR',5,4),(4,2,'DEPARTMENT_HEAD',NULL,6),(5,1,'MANAGER',NULL,6),(6,3,'HR',NULL,6);
/*!40000 ALTER TABLE `workflow_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'employee360'
--

--
-- Dumping routines for database 'employee360'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-19  2:18:18

