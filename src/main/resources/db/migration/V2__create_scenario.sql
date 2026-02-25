CREATE TABLE IF NOT EXISTS `scenario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4hymmasa2py1ghs9d7ld60c95` (`user_id`),
  CONSTRAINT `FK4hymmasa2py1ghs9d7ld60c95` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
)