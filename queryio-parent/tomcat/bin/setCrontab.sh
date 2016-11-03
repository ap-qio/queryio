EDITOR=ed
export EDITOR
crontab -e << EOF > /dev/null
a
@reboot /Users/api-dev-123/QueryIO/tomcat/bin/service_QueryIO.sh >> /Users/api-dev-123/QueryIO/tomcat/bin/out.log
.
w
q
EOF
#echo "Crontab Entry Inserted Successfully"


