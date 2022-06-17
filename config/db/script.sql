CREATE DATABASE quarkus_social;

CREATE TABLE USER (
    id integer AUTO_INCREMENT PRIMARY KEY,
    name varchar(100) NOT NULL,
    age integer NOT NULL
);
