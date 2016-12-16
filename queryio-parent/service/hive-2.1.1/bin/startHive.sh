bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo $bin
mkdir -p $bin/../logs/
nohup $bin/hive --service hiveserver2 -hiveconf hive.root.logger=DEBUG,console >> $bin/../logs/out.log 2>&1 &