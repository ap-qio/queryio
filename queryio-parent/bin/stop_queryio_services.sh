HOME=$HOME/QueryIOPackage
echo $HOME

for i in $(find $HOME/Hadoop-2.8.0/etc -name "namenode-conf_*"); do
        echo $i
        ftpPortString=$(grep -n queryio.hdfsoverftp.port $i/core-site.xml)
        ftpPort=$(echo $ftpPortString | cut -d'<' -f 5 |cut -d '>' -f 2)
        s3PortString=$(grep -n queryio.s3server.port $i/core-site.xml)
        s3Port=$(echo $s3PortString | cut -d'<' -f 5 |cut -d '>' -f 2)
        echo "ftpPort"
        echo $ftpPort
        echo "s3Port"
        echo $s3Port
#       echo "$HOME\ $ftpPort\ $s3Port"
#       servicesString=$(echo "$HOME/QueryIOServers\ $ftpPort\ $s3Port")
#       echo $servicesString
        pid=$(ps -ef | grep -v grep | grep com.queryio.agent.core.server.QueryIOServers | awk '{print $2}')
        echo $pid
        pid=`expr $pid + 0`
        if [ $pid -gt 0 ]
        then
                echo "QueryIOServices Process ID"
                echo $pid
                echo "## Stopping QueryIO Services ##"
                kill -9 $pid
        else
                echo "No Process is running QueryIO Services"
                echo "Is it running?"
        fi
done
