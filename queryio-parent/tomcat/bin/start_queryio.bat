set QUERYIO_HOME=$USER_INSTALL_DIR$
set JRE_HOME=$QUERYIO_HOME/.install4j/jre.bundle/Contents/Home/jre
set TOMCAT_HOME=%QUERYIO_HOME%\tomcat
set CATALINA_HOME=%QUERYIO_HOME%\tomcat

$BASE_DIR$
cd %TOMCAT_HOME%\bin
catalina.bat run > ../logs/out.log
