HOME=$USER_INSTALL_DIR$
echo $HOME
echo '###  Stopping Hadoop  ###'

# Stop NameNodes
pid=$(ps -ef | grep -v grep | grep hdfs.server.namenode.NameNode | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
			echo "Stopping NameNode"
			sh stop_queryio_services.sh
			sh stop_hive.sh
			kill -9 $pid
        else
                echo "NamoNode Process is not running."
                echo "Is it running?"
        fi


# Stop DataNodes
pid=$(ps -ef | grep -v grep | grep hdfs.server.datanode.DataNode | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
			echo "Stopping DataNode"
			kill -9 $pid
        else
            echo "DataNode Process is not running."
            echo "Is it running?"
        fi


# Stop secondary NameNodes
pid=$(ps -ef | grep -v grep | grep hdfs.server.namenode.SecondaryNameNode | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
			echo "Stopping Secondary NameNode"
			kill -9 $pid
        else
			echo "SecondaryNameNode Process is not running."
            echo "Is it running?"
        fi


# Stop Journal Nodes
pid=$(ps -ef | grep -v grep | grep hdfs.qjournal.server.JournalNode | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
			echo "Stopping Journal Node"
			kill -9 $pid
        else
			echo "JournalNode Process is not running."
            echo "Is it running?"
        fi


# Stop ResourceManager
		pid=$(ps -ef | grep -v grep | grep yarn.server.resourcemanager.ResourceManager | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
			echo "Stopping ResourceManager"
			kill -9 $pid
        else
			echo "ResourceManager Process is not running."
            echo "Is it running?"
        fi

# Stop historyserver
		pid=$(ps -ef | grep -v grep | grep mapreduce.v2.hs.JobHistoryServer | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
			echo "Stopping History server"
			kill -9 $pid
        else
			echo "JobHistoryServer Process is not running."
            echo "Is it running?"
        fi


# Stop NodeManager
pid=$(ps -ef | grep -v grep | grep yarn.server.nodemanager.NodeManager | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
			echo "Stopping NodeManager"
			kill -9 $pid	
        else
			echo "NodeManager Process is not running."
            echo "Is it running?"
        fi
