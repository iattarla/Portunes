CREATE SCHEMA `personnel_test` ;

CREATE TABLE `personnel_test`.`door_control` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `card_no` VARCHAR(45) NULL,
  `door_name` VARCHAR(45) NULL,
  `shift` VARCHAR(10) NULL,
  `enter_date` DATE NULL,
  `enter_time` TIME NULL,
  PRIMARY KEY (`id`));


CREATE TABLE `personnel_test`.`personnel` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `card_no` VARCHAR(45) NULL,
  `name` VARCHAR(45) NULL,
  `surname` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));


