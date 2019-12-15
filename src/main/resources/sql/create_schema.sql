DROP DATABASE IF EXISTS my_collection;
CREATE DATABASE  my_collection /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE my_collection.item (
  id char(36) NOT NULL,
  name varchar(45) NOT NULL,
  lead varchar(140) NOT NULL,
  item_type varchar(25) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name_UNIQUE (name, item_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# CREATE USER IF NOT EXISTS 'my_collection'@'%' IDENTIFIED BY 'MyCollection';
# GRANT SELECT, INSERT, UPDATE, DELETE ON my_collection.* TO 'MyCollection'@'%';
# FLUSH PRIVILEGES;

