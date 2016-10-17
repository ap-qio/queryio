HADOOP_LIB_HOME=../../Hadoop-2.7.1/share/hadoop/common/lib
QUERYIO_AGENT_LIB_HOME=../webapps/agentqueryio/WEB-INF/classes:../webapps/agentqueryio/WEB-INF/lib/QueryIOAgent.jar:../webapps/agentqueryio/WEB-INF/lib/QueryIOCommonAgent.jar
YARN_LIB_HOME=../../Hadoop-2.7.1/share/hadoop/yarn
CLASSPATH=$YARN_LIB_HOME/hadoop-yarn-common-2.7.1.jar:$CLASSPATH
CLASSPATH=$HADOOP_LIB_HOME/guava-11.0.2.jar:$HADOOP_LIB_HOME/slf4j-log4j12-1.7.5.jar:$HADOOP_LIB_HOME/jetty-6.1.26.jar:$HADOOP_LIB_HOME/jetty-util-6.1.26.jar:$HADOOP_LIB_HOME/commons-configuration-1.6.jar:$HADOOP_LIB_HOME/slf4j-api-1.7.5.jar:$HADOOP_LIB_HOME/commons-codec-1.4.jar:$HADOOP_LIB_HOME/commons-lang-2.6.jar:$HADOOP_LIB_HOME/commons-logging-1.1.3.jar:$HADOOP_LIB_HOME/jsp-api-2.1.jar:$HADOOP_LIB_HOME/servlet-api-2.5.jar:$HADOOP_LIB_HOME/log4j-1.2.17.jar:$HADOOP_LIB_HOME/commons-collections-3.2.1.jar:$CLASSPATH
CLASSPATH=../../QueryIOServers/lib/hadoop-custom-compiled.jar:../../QueryIOServers/lib/tika-app-1.2.jar:../../QueryIOServers/lib/commons-dbcp-1.2.2.jar:../../QueryIOServers/lib/commons-pool-1.4.jar:../../QueryIOServers/lib/FsImageUtils.jar:$QUERYIO_AGENT_LIB_HOME:$CLASSPATH
CLASSPATH=../../Hadoop-2.7.1/share/hadoop/common/QueryIOPlugins.jar:$CLASSPATH

java -cp $CLASSPATH com.queryio.agent.core.server.QueryIOAgent $1 $2