#!/bin/sh

agentClass=com.queryio.agent.core.server.QueryIOAgent 

pid=$(ps -ef | grep -v grep | grep $agentClass | awk '{print $2}')
echo "QueryIO agent ID"
echo $pid
pid=`expr $pid + 0`
if [ $pid -gt 0 ]
then
	echo "## Stopping QueryIO Agent ##"
	crontab -r
	kill -9 $pid
else
	echo "QueryIO agent is not running".
	echo "Is it running?"
fi
