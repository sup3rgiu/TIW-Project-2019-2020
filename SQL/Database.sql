-- MySQL dump 10.13  Distrib 8.0.20, for Win64 (x86_64)
--
-- Host: localhost    Database: dbgalleriaimmagini
-- ------------------------------------------------------
-- Server version	8.0.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `album`
--

DROP TABLE IF EXISTS `album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `title_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
INSERT INTO `album` VALUES (1,'Album 1','2021-11-11'),(2,'Album 2','2021-09-11'),(3,'Album 3','2019-07-07'),(4,'Album 4','2001-12-12'),(5,'Album 5','2006-09-12'),(6,'Album 6','2020-12-12'),(7,'Album 7','2019-11-12');
/*!40000 ALTER TABLE `album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `text` text NOT NULL,
  `image_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idimage_idx` (`image_id`),
  KEY `iduser_idx` (`user_id`),
  CONSTRAINT `idimage` FOREIGN KEY (`image_id`) REFERENCES `image` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `iduser` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,'Bella immagine!',5,1),(2,'Mi piace!',13,1),(3,'Nice!',2,1),(4,'Good!',1,1),(5,'Bella',15,1),(6,'Prova',5,1),(7,'Pro s',5,1),(8,'Test',5,11);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `image` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(400) NOT NULL,
  `description` text NOT NULL,
  `filepath` varchar(500) NOT NULL,
  `date` date NOT NULL,
  `album_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `albumid_idx` (`album_id`),
  CONSTRAINT `albumid` FOREIGN KEY (`album_id`) REFERENCES `album` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--

LOCK TABLES `image` WRITE;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
INSERT INTO `image` VALUES (1,'Image 0','La mia zeresima immagine!','images/0.png','2008-11-11',1),(2,'Image 1','La mia prima immagine!','images/1.png','2009-11-11',1),(3,'Image 2','La mia seconda immagine!','images/2.png','2006-11-11',1),(4,'Image 3','La mia terza immagine!','images/3.png','2010-11-11',1),(5,'Image 4','La mia quarta immagine!','images/4.png','2022-11-11',1),(6,'Image 5','La mia quinta immagine!','images/5.png','1980-11-11',1),(7,'Image 6','La mia sesta immagine!','images/6.png','1976-11-11',1),(8,'Image 7','La mia settima immagine!','images/7.png','2006-11-11',2),(9,'Image 8','La mia ottava immagine!','images/8.png','2020-11-11',2),(10,'Image 2','La mia seconda immagine!','images/2.png','2006-11-11',1),(11,'Image 4','La mia quarta immagine!','images/4.png','2006-11-11',1),(12,'Image 7','La mia settima immagine!','images/7.png','2006-11-11',1),(13,'Image 8','La mia ottava immagine!','images/8.png','2020-11-11',1),(14,'Image 8','La mia ottava immagine!','images/8.png','2015-11-11',3),(15,'Image 3','La mia terza immagine!','images/3.png','2017-11-22',3),(16,'Image 2','La mia seconda immagine!','images/2.png','2010-10-09',4),(17,'Image 1','La mia prima immagine!','images/1.png','2017-02-15',4),(18,'Image 5','La mia quinta immagine!','images/5.png','2019-04-25',5),(19,'Image 6','La mia setta immagine!','images/6.png','2020-12-01',6),(20,'Image 7','La mia settima immagine!','images/7.png','2019-11-11',6),(21,'Image 8','La mia ottava immagine!','images/8.png','2007-11-11',3);
/*!40000 ALTER TABLE `image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `image_comments_view`
--

DROP TABLE IF EXISTS `image_comments_view`;
/*!50001 DROP VIEW IF EXISTS `image_comments_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `image_comments_view` AS SELECT 
 1 AS `idimage`,
 1 AS `idcomment`,
 1 AS `text`,
 1 AS `username`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(45) NOT NULL,
  `albumsOrder` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_user_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'andrea','andrea@email.com','1234',NULL),(2,'luca','luca@email.com','1234',NULL),(11,'pippo','pippo@email.com','1234','[4,2,6,7,3,5,1]');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `image_comments_view`
--

/*!50001 DROP VIEW IF EXISTS `image_comments_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `image_comments_view` AS select `i`.`id` AS `idimage`,`c`.`id` AS `idcomment`,`c`.`text` AS `text`,`u`.`username` AS `username` from ((`image` `i` join `comment` `c`) join `user` `u`) where ((`i`.`id` = `c`.`image_id`) and (`u`.`id` = `c`.`user_id`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-06-22 11:44:17
