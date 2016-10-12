CREATE USER ADMIN PASSWORD ADMIN ADMIN;
SET DATABASE SQL SYNTAX MYS TRUE;
CREATE TABLE HDFS_METADATA(FILEPATH VARCHAR (5000),ACCESSTIME TIMESTAMP,MODIFICATIONTIME TIMESTAMP,OWNER VARCHAR (255),USERGROUP VARCHAR (255),PERMISSION VARCHAR (23),BLOCKSIZE BIGINT,REPLICATION SMALLINT,LEN BIGINT,COMPRESSION_TYPE VARCHAR (64),ENCRYPTION_TYPE VARCHAR (64),BLOCKS VARBINARY(16000));
CREATE TABLE NS_METADATA(KEYPATTERN VARCHAR (1024),VALUE VARCHAR (1024));