CREATE DATABASE  my_catalogue;

CREATE TABLE my_catalogue.item (
  id char(36) NOT NULL,
  name varchar(45) NOT NULL,
  lead varchar(140) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name_UNIQUE (name)
);