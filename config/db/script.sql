CREATE DATABASE quarkus_social;

CREATE TABLE USER (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    name varchar(100) NOT NULL,
    age integer NOT NULL
);

CREATE TABLE POST (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    post_text varchar(150) NOT NULL,
    date_time timestamp NOT NULL,
    user_id bigint NOT NULL REFERENCES USER(id)
);
