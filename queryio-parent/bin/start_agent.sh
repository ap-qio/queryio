#!/bin/sh

cd "$HOME/QueryIOPackage/QueryIOAgent/bin" || exit
echo 'Starting QueryIO agent'
sh startQIOAgent.sh $HOME/QueryIOPackage/QueryIOAgent 6680
