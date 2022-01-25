HOME=$USER_PACKAGE_INSTALL_DIR$
ServiceHome=$HOME/QueryIOServers
HADOOP_LIB_HOME=$HOME/Hadoop-3.1.1/share/hadoop/common/lib
QUERYIO_AGENT_LIB_HOME=$HOME/QueryIOAgent/webapps/agentqueryio/WEB-INF/classes:$HOME/QueryIOAgent/webapps/agentqueryio/WEB-INF/lib/QueryIOAgent.jar:$HOME/QueryIOAgent/webapps/agentqueryio/WEB-INF/lib/QueryIOCommonAgent.jar

CLASSPATH=$HADOOP_LIB_HOME/commons-collections-3.2.2.jar:$HADOOP_LIB_HOME/htrace-core-3.1.0-incubating.jar:$HADOOP_LIB_HOME/guava-11.0.2.jar:$HADOOP_LIB_HOME/slf4j-log4j12-1.7.10.jar:$HADOOP_LIB_HOME/commons-configuration-1.6.jar:$HADOOP_LIB_HOME/commons-io-2.4.jar:$HADOOP_LIB_HOME/slf4j-api-1.7.25.jar:$HADOOP_LIB_HOME/commons-codec-1.4.jar:$HADOOP_LIB_HOME/commons-lang-2.6.jar:$HADOOP_LIB_HOME/commons-logging-1.1.3.jar:$HADOOP_LIB_HOME/jsp-api-2.1.jar:$HADOOP_LIB_HOME/log4j-1.2.17.jar:$HADOOP_LIB_HOME/snappy-java-1.0.4.1.jar:$HADOOP_LIB_HOME/LZ4Java.jar:$HADOOP_LIB_HOME/jsch-0.1.42.jar:$HADOOP_LIB_HOME/servlet-api-2.5.jar:$HADOOP_LIB_HOME/jsp-api-2.1.jar:$HADOOP_LIB_HOME/protobuf-java-2.5.0.jar:$HADOOP_LIB_HOME/commons-cli-1.2.jar:$HADOOP_LIB_HOME/jasper-compiler-5.5.23.jar:$HADOOP_LIB_HOME/jasper-runtime-5.5.23.jar:$QUERYIO_AGENT_LIB_HOME:$HADOOP_LIB_HOME/../QueryIOPlugins.jar:$HADOOP_LIB_HOME/../QueryIOCommon.jar:$CLASSPATH
CLASSPATH=$HADOOP_LIB_HOME/jetty-server-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-security-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-servlet-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-webapp-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-io-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-http-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-util-9.3.19.v20170502.jar:$HADOOP_LIB_HOME/jetty-xml-9.3.19.v20170502.jar:$CLASSPATH
CLASSPATH=$HADOOP_LIB_HOME/javax.servlet-api-3.1.0.jar:$CLASSPATH

CLASSPATH=$ServiceHome/lib/commons-dbcp-1.2.2.jar:$ServiceHome/lib/commons-pool-1.4.jar:$ServiceHome/webapps/hdfs-over-ftp/WEB-INF/lib/hadoop-common-3.1.1.jar:$ServiceHome/lib/json_simple-1.1.jar:$ServiceHome/lib/hsqldb-2.2.8.jar:$ServiceHome/lib/servlet-api.jar:$ServiceHome/lib/tika-app-1.3-modified.jar:../../UserLibs/Plugins/DataTagParser.jar:$ServiceHome/lib/commons-jexl-2.1.1.jar:$CLASSPATH
java -Dorg.xerial.snappy.lib.name=libsnappyjava.jnilib -Dorg.xerial.snappy.tempdir=/tmp -cp $CLASSPATH com.queryio.agent.core.server.QueryIOServers $ServiceHome $2 $3 $4

exit 0