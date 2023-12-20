CREATE TABLE IF NOT EXISTS `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(20) DEFAULT NULL,
  `news_count` int DEFAULT '0',
  `build_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `news` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(20) DEFAULT NULL,
  `sub_category` varchar(20) DEFAULT NULL,
  `news_title` varchar(100) DEFAULT NULL,
  `news_sub_title` varchar(20) DEFAULT NULL,
  `release_time` date DEFAULT NULL,
  `content` varchar(2000) DEFAULT NULL,
  `build_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `sub_category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sub_category` varchar(20) DEFAULT NULL,
  `sub_category_news_count` int DEFAULT NULL,
  `category` varchar(20) DEFAULT NULL,
  `build_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);
