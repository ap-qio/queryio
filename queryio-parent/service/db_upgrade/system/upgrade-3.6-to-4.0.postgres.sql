--Hive upgrade from 0.13.0 tp 1.2.0 (hiveserver1 to hiveserver2)

UPDATE HADOOPCONFIG SET DEFAULT_VALUE='org.apache.hive.jdbc.HiveDriver' WHERE HADOOPKEY='queryio.hive.connection.driver';
UPDATE HADOOPCONFIG SET DEFAULT_VALUE='jdbc:hive2://0.0.0.0:10000/default' WHERE HADOOPKEY='queryio.hive.connection.url';

 