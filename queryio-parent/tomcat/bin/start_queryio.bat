set JRE_HOME=/Users/api-dev-123/QueryIO/.install4j/jre.bundle/Contents/Home/jre
set QUERYIO_HOME=/Users/api-dev-123/QueryIO
set TOMCAT_HOME=%QUERYIO_HOME%\tomcat
set CATALINA_HOME=%QUERYIO_HOME%\tomcat

$BASE_DIR$
cd %TOMCAT_HOME%\bin
catalina.bat run > ../logs/out.log
