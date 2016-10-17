
export HOME_DIR=$1
echo $1
cd $HOME_DIR/bin

java -cp $HOME_DIR/bin/RemoteUninstaller.jar com.queryio.uninstall.kill.RemoveRemotePackage $1
#rm -rf $HOME_DIR
