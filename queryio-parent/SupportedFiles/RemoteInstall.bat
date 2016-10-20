set HOME_DIR=%5
cd %HOME_DIR%
cd ..
java -cp %HOME_DIR%\QueryIORemoteInstaller.jar;%HOME_DIR%\jsch-0.1.41.jar com.queryio.remote.insataller.RemoteInstaller %1 %2 %3 %4 %5 %6 %7 %8
RMDIR /s /q %HOME_DIR%
