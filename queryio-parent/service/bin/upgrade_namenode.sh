HOME=$USER_INSTALL_DIR$
echo $HOME

echo '###  Starting Hadoop  ###'

pid=$(ps -ef | grep -v grep | grep hdfs.server.namenode.NameNode | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
            echo "NamoNode Process is already running on this host."
        else
            for i in $(find $HOME/Hadoop-2.7.1/etc -name "namenode-conf_*"); do
	        	echo $i
				chmod 766 $i/*
				sh $HOME/Hadoop-2.7.1/sbin/hadoop-daemon.sh --config $i start namenode -upgrade 
			done
            echo "NamoNode Upgrade Process launched successfully."
        fi
