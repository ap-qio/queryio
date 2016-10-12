export DB_HOME=$USER_INSTALL_DIR$/database
export CLASSPATH=$USER_INSTALL_DIR$/tomcat/webapps/queryio/jdbcJars/hsqldb-2.3.2.jar:$CLASSPATH

if [ "$#" -eq 2 ]; then
	export QUERYIO_DATABASE=$DB_HOME/$2
	$TOMCAT_JAVA_HOME$/bin/java -Xms256m -Xmx2048m org.hsqldb.Server -port $1 -database.0 $QUERYIO_DATABASE -dbname.0 $2  
elif [ "$#" -eq 3 ]; then
	export QUERYIO_DATABASE1=$DB_HOME/$2
	export QUERYIO_DATABASE2=$DB_HOME/$3
	$TOMCAT_JAVA_HOME$/bin/java -Xms256m -Xmx2048m org.hsqldb.Server -port $1 -database.0 $QUERYIO_DATABASE1 -dbname.0 $2 -database.1 $QUERYIO_DATABASE2 -dbname.1 $3
else
	echo "Invalid argument"
	echo "Usage startdatabase.sh <port> <dbname>"
	echo "Usage startdatabase.sh <port> <dbname1> <dbname2>"
fi
