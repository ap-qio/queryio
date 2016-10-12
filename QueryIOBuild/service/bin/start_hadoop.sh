HOME=$USER_INSTALL_DIR$
echo $HOME

echo '###  Starting Hadoop  ###'

# Stop NameNodes
pid=$(ps -ef | grep -v grep | grep hdfs.server.namenode.NameNode | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
            echo "NamoNode Process is already running on this host."
        else
            for i in $(find $HOME/Hadoop-2.7.1/etc -name "namenode-conf_*"); do
	        	echo $i
				sh $HOME/Hadoop-2.7.1/sbin/hadoop-daemon.sh --config $i start namenode
			done
            echo "NamoNode Process launched successfully."
        fi


# Stop DataNodes
pid=$(ps -ef | grep -v grep | grep hdfs.server.datanode.DataNode | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
	        echo "DataNode Process is already running on this host."
        else
            for i in $(find $HOME/Hadoop-2.7.1/etc -name "datanode-conf_*"); do
		        echo $i
				sh $HOME/Hadoop-2.7.1/sbin/hadoop-daemon.sh --config $i start datanode
			done
            echo "DataNode Process launched successfully."
        fi


# Stop secondary NameNodes
pid=$(ps -ef | grep -v grep | grep hdfs.server.namenode.SecondaryNameNode | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
	        echo "SecondaryNameNode Process is already running on this host."
        else
			for i in $(find $HOME/Hadoop-2.7.1/etc -name "secondarynamenode-conf_*"); do
       			echo $i
				sh $HOME/Hadoop-2.7.1/sbin/hadoop-daemon.sh --config $i start secondarynamenode
			done
            echo "SecondaryNameNode Process launched successfully."
		fi


# Stop Journal Nodes
pid=$(ps -ef | grep -v grep | grep hdfs.qjournal.server.JournalNode | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
        	echo "JournalNode Process is already running on this host."
        else
			for i in $(find $HOME/Hadoop-2.7.1/etc -name "journalnode-conf_*"); do
		        echo $i
				sh $HOME/Hadoop-2.7.1/sbin/hadoop-daemon.sh --config $i start journalnode
			done
            echo "JournalNode Process launched successfully."			
        fi


# Stop ResourceManager
		pid=$(ps -ef | grep -v grep | grep yarn.server.resourcemanager.ResourceManager | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
	        echo "ResourceManager Process is already running on this host."
        else
			for i in $(find $HOME/Hadoop-2.7.1/etc -name "resourcemanager-conf_*"); do
		        echo $i
				sh $HOME/Hadoop-2.7.1/sbin/yarn-daemon.sh --config $i start resourcemanager
			done
            echo "ResourceManager Process launched successfully."
        fi


# Stop historyserver
		pid=$(ps -ef | grep -v grep | grep mapreduce.v2.hs.JobHistoryServer | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
        	echo "JobHistoryServer Process is already running on this host."
        else
			for i in $(find $HOME/Hadoop-2.7.1/etc -name "resourcemanager-conf_*"); do
		        echo $i
				sh $HOME/Hadoop-2.7.1/sbin/mr-jobhistory-daemon.sh --config $i start historyserver
			done
            echo "JobHistoryServer Process launched successfully."
        fi


# Stop NodeManager
pid=$(ps -ef | grep -v grep | grep yarn.server.nodemanager.NodeManager | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
        	echo "NodeManager Process is already running on this host."
        else
			for i in $(find $HOME/Hadoop-2.7.1/etc -name "nodemanager-conf_*"); do
		        echo $i
				sh $HOME/Hadoop-2.7.1/sbin/yarn-daemon.sh --config $i start nodemanager
			done
            echo "NodeManager Process launched successfully."
        fi
