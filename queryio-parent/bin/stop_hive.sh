#!/bin/sh

echo '###  Stopping Hive  ###'

hiveClass=org.apache.hive.service.server.HiveServer2
pid=$(ps -ef | grep -v grep | grep $hiveClass | awk '{print $2}')
echo "Hive Server Process ID"
echo $pid
pid=`expr $pid + 0`
if [ $pid -gt 0 ]
then
	echo "## Stopping Hive Server ##"
	kill -9 $pid
else
	echo "Hive server is not running".
	echo "Is it running?"
fi
