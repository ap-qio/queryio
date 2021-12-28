HADOOP_LIB_HOME=../../Hadoop-3.1.1/share/hadoop/common/lib
QUERYIO_AGENT_LIB_HOME=../webapps/agentqueryio/WEB-INF/classes:../webapps/agentqueryio/WEB-INF/lib/QueryIOAgent.jar:../webapps/agentqueryio/WEB-INF/lib/QueryIOCommonAgent.jar
YARN_LIB_HOME=../../Hadoop-3.1.1/share/hadoop/yarn
CLASSPATH=$YARN_LIB_HOME/hadoop-yarn-common-3.1.1.jar:$CLASSPATH
CLASSPATH=$HADOOP_LIB_HOME/guava-11.0.2.jar:$HADOOP_LIB_HOME/slf4j-log4j12-1.7.10.jar:$HADOOP_LIB_HOME/commons-configuration-1.6.jar:$HADOOP_LIB_HOME/slf4j-api-1.7.25.jar:$HADOOP_LIB_HOME/commons-codec-1.4.jar:$HADOOP_LIB_HOME/commons-lang-2.6.jar:$HADOOP_LIB_HOME/commons-logging-1.1.3.jar:$HADOOP_LIB_HOME/jsp-api-2.1.jar:$HADOOP_LIB_HOME/servlet-api-2.5.jar:$HADOOP_LIB_HOME/log4j-1.2.17.jar:$HADOOP_LIB_HOME/commons-collections-3.2.2.jar:$CLASSPATH
CLASSPATH=../../QueryIOServers/webapps/hdfs-over-ftp/WEB-INF/lib/hadoop-common-3.1.1.jar:../../QueryIOServers/lib/tika-app-1.2.jar:../../QueryIOServers/lib/commons-dbcp-1.2.2.jar:../../QueryIOServers/lib/commons-pool-1.4.jar:../../QueryIOServers/lib/FsImageUtils.jar:$QUERYIO_AGENT_LIB_HOME:$CLASSPATH
CLASSPATH=../../Hadoop-3.1.1/share/hadoop/common/QueryIOPlugins.jar:$CLASSPATH

CLASSPATH=$HADOOP_LIB_HOME/jetty-server-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-security-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-servlet-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-webapp-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-io-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-http-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-util-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-xml-9.3.19.v20170502.jar:$CLASSPATH
CLASSPATH=$HADOOP_LIB_HOME/javax.servlet-api-3.1.0.jar:$CLASSPATH

nohup java -cp $CLASSPATH com.queryio.agent.core.server.QueryIOAgent $1 $2 >> $1/webapps/agentqueryio/AgentStatus.log 2>&1 &