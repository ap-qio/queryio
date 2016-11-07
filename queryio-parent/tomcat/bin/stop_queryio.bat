set JRE_HOME=$USER_INSTALL_DIR$/.install4j/jre.bundle/Contents/Home/jre
set QUERYIO_HOME=$USER_INSTALL_DIR$
set TOMCAT_HOME=%QUERYIO_HOME%\tomcat
set CATALINA_HOME=%QUERYIO_HOME%\tomcat

cd %TOMCAT_HOME%\bin
%TOMCAT_HOME%\bin\catalina.bat stop -config %TOMCAT_HOME%\conf\server.xml

