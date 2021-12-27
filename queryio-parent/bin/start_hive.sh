#!/bin/sh

HOME=$HOME/QueryIOPackage
hiveClass=org.apache.hive.service.server.HiveServer2

pid=$(ps -ef | grep -v grep | grep $hiveClass | awk '{print $2}')

if [ -n "$pid" ] && [ "$pid" -gt 0 ]; then
	echo "Hive server is already running. Hive Process ID: $pid".
else
	export HIVEHOME="$HOME"/hive-3.1.2/bin
	nohup "$HIVEHOME"/hive --service hiveserver2 -hiveconf hive.root.logger=FATAL,console >> \
		"$HIVEHOME"/../logs/out.log 2>&1 &
fi
