# find /root/QueryIOPackage/Hadoop-2.7.3/etc -name "namenode-conf_*" -exec cat {} \;
# find /root/QueryIOPackage/Hadoop-2.7.3/etc -name "namenode-conf_*" -exec cat {}/core-site.xml \;

HOME=$USER_INSTALL_DIR$
echo $HOME

for i in $(find $HOME/Hadoop-2.7.3/etc -name "namenode-conf_*"); do
        echo $i
        ftpPortString=$(grep -n queryio.hdfsoverftp.port $i/core-site.xml)
        ftpPort=$(echo $ftpPortString | cut -d'<' -f 5 |cut -d '>' -f 2)
        s3PortString=$(grep -n queryio.s3server.port $i/core-site.xml)
        s3Port=$(echo $s3PortString | cut -d'<' -f 5 |cut -d '>' -f 2)
        echo "ftpPort"
        echo $ftpPort
        echo "s3Port"
        echo $s3Port
		nohup sh $HOME/QueryIOServers/bin/start_servers_manually.sh $HOME $ftpPort $s3Port $i >> $HOME/bin/start_service.log 2>&1 &
done



# ftpPort=$(find /root/QueryIOPackage/Hadoop-2.7.3/etc -name "namenode-conf_*" -exec grep -n queryio.ftpserver.port {}/core-site.xml \;)
# echo "FTP Port"
# echo $ftpPort


# echo '4:<property><name>queryio.ftpserver.port</name><value>5660</value><source>programatically</source></property>' | cut -d'<' -f 5 |cut -d '>' -f 2

# grep -n queryio.ftpserver.port core-site.xml 
# grep -n queryio.s3server.port core-site.xml 
