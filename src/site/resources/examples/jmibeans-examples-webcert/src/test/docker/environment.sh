#!/bin/sh

default_ssh_public_key="\$HOME/.ssh/id_rsa.pub"
default_jdwp_suspend="n"
default_jdwp_timeout="60000"

original_pwd=$PWD
cd $(dirname $0)

. ./environment.conf

rm -f .env

echo "ARTIFACT_NAME=$ARTIFACT_NAME" >>.env

echo "FTP_PASS=$(tr -dc A-Za-z0-9 </dev/urandom | head -c 20; echo)" >>.env

read -p "SSH public key [$default_ssh_public_key]: " ssh_public_key
test -z "$ssh_public_key" && ssh_public_key=$default_ssh_public_key
echo "DEV_SSH_PUBKEY=$ssh_public_key" >>.env

read -p "JDWP suspend (y/n) [n]: " jdwp_suspend
if [ "$jdwp_suspend" = "y" ]
then
  read -p "JDWP timeout milliseconds [$default_jdwp_timeout]: " jdwp_timeout
  test -z "$jdwp_timeout" && jdwp_timeout=$default_jdwp_timeout
  echo "JDWP_SERVER_PARAMS=suspend=y,timeout=$jdwp_timeout" >>.env
else
  echo "JDWP_SERVER_PARAMS=suspend=n" >>.env
fi

cd $original_pwd
