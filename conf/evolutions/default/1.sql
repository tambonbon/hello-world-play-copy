# Lights schema
-- !Ups

CREATE TABLE  db (
                         id          bigint(20) NOT NULL UNIQUE,
                         lights      varchar(255) NOT NULL
);

-- !Downs

DROP TABLE db