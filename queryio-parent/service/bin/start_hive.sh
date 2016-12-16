HOME=$USER_INSTALL_DIR$
hiveClass=org.apache.hive.service.server.HiveServer2
pid=$(ps -ef | grep -v grep | grep $hiveClass | awk '{print $2}')
echo "Hive Server Process ID"
echo $pid
pid=`expr $pid + 0`
if [ $pid -gt 0 ]
then
	echo "Hive server is already running".
else
	export HIVEHOME=$HOME/hive-2.1.1/bin
	nohup $HIVEHOME/hive --service hiveserver2 -hiveconf hive.root.logger=FATAL,console >> $HIVEHOME/../logs/out.log 2>&1 &
fi
