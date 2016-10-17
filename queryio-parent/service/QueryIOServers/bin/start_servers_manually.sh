HOME=$USER_INSTALL_DIR$
ServiceHome=$HOME/QueryIOServers
HADOOP_LIB_HOME=$HOME/Hadoop-2.7.1/share/hadoop/common/lib
QUERYIO_AGENT_LIB_HOME=$HOME/QueryIOAgent/webapps/agentqueryio/WEB-INF/classes:$HOME/QueryIOAgent/webapps/agentqueryio/WEB-INF/lib/QueryIOAgent.jar:$HOME/QueryIOAgent/webapps/agentqueryio/WEB-INF/lib/QueryIOCommonAgent.jar

CLASSPATH=$HADOOP_LIB_HOME/commons-collections-3.2.1.jar:$HADOOP_LIB_HOME/htrace-core-3.1.0-incubating.jar:$HADOOP_LIB_HOME/guava-11.0.2.jar:$HADOOP_LIB_HOME/slf4j-log4j12-1.7.5.jar:$HADOOP_LIB_HOME/jetty-6.1.26.jar:$HADOOP_LIB_HOME/jetty-util-6.1.26.jar:$HADOOP_LIB_HOME/commons-configuration-1.6.jar:$HADOOP_LIB_HOME/commons-io-2.4.jar:$HADOOP_LIB_HOME/slf4j-api-1.7.5.jar:$HADOOP_LIB_HOME/commons-codec-1.4.jar:$HADOOP_LIB_HOME/commons-lang-2.6.jar:$HADOOP_LIB_HOME/commons-logging-1.1.3.jar:$HADOOP_LIB_HOME/jsp-api-2.1.jar:$HADOOP_LIB_HOME/log4j-1.2.17.jar:$HADOOP_LIB_HOME/snappy-java-1.0.4.1.jar:$HADOOP_LIB_HOME/LZ4Java.jar:$HADOOP_LIB_HOME/jsch-0.1.42.jar:$HADOOP_LIB_HOME/servlet-api-2.5.jar:$HADOOP_LIB_HOME/jsp-api-2.1.jar:$HADOOP_LIB_HOME/protobuf-java-2.5.0.jar:$HADOOP_LIB_HOME/commons-cli-1.2.jar:$HADOOP_LIB_HOME/jasper-compiler-5.5.23.jar:$HADOOP_LIB_HOME/jasper-runtime-5.5.23.jar:$QUERYIO_AGENT_LIB_HOME:$HADOOP_LIB_HOME/../QueryIOPlugins.jar:$HADOOP_LIB_HOME/../QueryIOCommon.jar:$CLASSPATH

CLASSPATH=$ServiceHome/lib/commons-dbcp-1.2.2.jar:$ServiceHome/lib/commons-pool-1.4.jar:$ServiceHome/lib/hadoop-custom-compiled.jar:$ServiceHome/lib/json_simple-1.1.jar:$ServiceHome/lib/hsqldb-2.2.8.jar:$ServiceHome/lib/servlet-api.jar:$ServiceHome/lib/tika-app-1.3-modified.jar:../../UserLibs/Plugins/DataTagParser.jar:$ServiceHome/lib/commons-jexl-2.1.1.jar:$CLASSPATH
java -Dorg.xerial.snappy.lib.name=libsnappyjava.jnilib -Dorg.xerial.snappy.tempdir=/tmp -cp $CLASSPATH com.queryio.agent.core.server.QueryIOServers $ServiceHome $2 $3 $4

exit 0