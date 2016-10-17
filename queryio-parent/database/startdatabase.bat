set DB_HOME=$USER_INSTALL_DIR$\database
rem set JAVA_HOME=$USER_INSTALL_DIR$\jre
set CLASSPATH=$USER_INSTALL_DIR$\tomcat\webapps\queryio\jdbcJars\hsqldb-2_2_8.jar
rem set PATH=%JAVA_HOME%\bin;%JAVA_HOME%\lib

set QUERYIO_DATABASE=%DB_HOME%\%1
java -Xms256m -Xmx1024m org.hsqldb.Server -port %2 -database.0 %QUERYIO_DATABASE% -dbname.0 %1
