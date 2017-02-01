HOME=$HOME/QueryIOPackage
hiveClass=org.apache.hive.service.server.HiveServer2
pid=$(ps -ef | grep -v grep | grep $hiveClass | awk '{print $2}')
echo "Hive Server Process ID"
echo $pid
if [ [$pid+0] -gt 0 ]
then
	echo "Hive server is already running".
else
	export HIVEHOME=$HOME/services/hive-2.1.1/bin
	nohup $HIVEHOME/hive --service hiveserver2 -hiveconf hive.root.logger=FATAL,console >> $HIVEHOME/../logs/out.log 2>&1 &
#else
fi
