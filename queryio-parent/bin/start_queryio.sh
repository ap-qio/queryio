#! /bin/sh
# On Mac OS X, Please open a shell and execute this script from a shell to start the server
#
# IMPORTANT: This script does not require administrator permissions.
#
QUERYIO_HOME=$USER_INSTALL_DIR$
TOMCAT_HOME=$QUERYIO_HOME/tomcat

cd $TOMCAT_HOME/bin
./catalina.sh run -config conf/server.xml >> ../logs/out.log 2>&1 &
