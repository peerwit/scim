# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `email` (`address` VARCHAR(254) NOT NULL PRIMARY KEY,`name` VARCHAR(254) NOT NULL);
create table `group` (`name` VARCHAR(254) NOT NULL PRIMARY KEY,`req` BOOLEAN NOT NULL);
create table `user_group` (`user_name` VARCHAR(254) NOT NULL,`group_name` VARCHAR(254) NOT NULL);
create table `user` (`name` VARCHAR(254) NOT NULL PRIMARY KEY,`nickname` VARCHAR(254) NOT NULL);

# --- !Downs

drop table `user`;
drop table `user_group`;
drop table `group`;
drop table `email`;

