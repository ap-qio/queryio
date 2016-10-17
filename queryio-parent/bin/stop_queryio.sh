QUERYIO_HOME=$USER_INSTALL_DIR$
TOMCAT_HOME=$QUERYIO_HOME/tomcat

cd $TOMCAT_HOME/bin
./catalina.sh stop -config conf/server.xml
