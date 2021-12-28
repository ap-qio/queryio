1. Download the latest QueryIOInstaller tar from git repository "https://github.com/ap-qio/queryio.git"
	 There, tar is present in "dist" directory. 
	
2. Untar to location where want to install the QueryIO.
	 Command :
	 tar -xvf QueryIOInstaller_5.1.tar.gz
3. Then go to bin directory :
	cd QueryIOInstaller/bin
	
4. Then you can qio-setup.properties file as per your set up.
	We recommend to keep it as it.
4. Then run install.sh.
		sh install.sh
	Note : There you will asked for IP if default IP selected by QueryIO installer is desired one then press "Enter"
	 otherwise type the required IP Address and Press "Enter"

5. Once installation is done. You can start the QueryIO using "start_queryio.sh"
	Command :
		sh start_queryio.sh
		
6. You can check log in $INSTALL_DIR/tomcat/logs.
		There you can check out.log for QueryIO standard output logs and AppQueryIO.log for Application Logs.

