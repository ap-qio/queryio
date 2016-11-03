SERVICE='/Users/api-dev-123/QueryIO/tomcat/bin/start_service_nonadmin.sh'
now=$(date);
if ps ax | grep -v grep | grep $SERVICE | grep "java" > /dev/null
then
echo "$SERVICE service running, everything is fine"
else
echo "$SERVICE is not running...Starting $SERVICE";
cd $1
sh /Users/api-dev-123/QueryIO/tomcat/bin/start_queryio.sh 
fi
