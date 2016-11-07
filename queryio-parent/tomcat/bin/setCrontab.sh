EDITOR=ed
export EDITOR
crontab -e << EOF > /dev/null
a
@reboot $USER_INSTALL_DIR$/tomcat/bin/service_QueryIO.sh >> $USER_INSTALL_DIR$/tomcat/bin/out.log
.
w
q
EOF
#echo "Crontab Entry Inserted Successfully"


