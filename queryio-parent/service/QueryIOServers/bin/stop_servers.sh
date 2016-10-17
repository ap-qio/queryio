#! /bin/sh
# On Mac OS X, Please open a shell and execute this script from a shell to stop the server
#
# IMPORTANT: This script does not require administrator permissions.
#
../catalina.sh stop -config ../conf/server_service.xml

# start DOLLARDOLLARDOLLARDOLLARDOLLARTOMCAT_HOME -Djava.io.tmpdir=$TOMCAT_HOME/temp org.apache.catalina.startup.Bootstrap -config $TOMCAT_HOME/conf/server_service.xml stop

