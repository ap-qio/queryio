#!/bin/sh

QUERYIO_HOME=$USER_INSTALL_DIR$
TOMCAT_HOME=$QUERYIO_HOME/tomcat

cd "$TOMCAT_HOME"/bin || exit
./catalina.sh stop -config conf/server.xml
