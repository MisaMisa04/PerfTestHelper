CREATE TABLE IF NOT EXISTS `operation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ctt` double DEFAULT NULL,
  `sla` double DEFAULT NULL,
  `calculate_method` int(11) NOT NULL DEFAULT 0 CHECK (`calculate_method` between 0 and 2),
  `gens_amount` int(11) DEFAULT NULL,
  `is_distributed` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `rps` int(11) DEFAULT NULL,
  `threads_amount` int(11) DEFAULT NULL,
  `scenario_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfnb1l0rhtnrii8uxh4c5wioyt` (`scenario_id`),
  CONSTRAINT `FKfnb1l0rhtnrii8uxh4c5wioyt` FOREIGN KEY (`scenario_id`) REFERENCES `scenario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci