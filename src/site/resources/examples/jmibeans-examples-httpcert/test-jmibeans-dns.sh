#!/bin/sh

s1_name='test-docker-httpd-s1-1'
s1_ip=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $s1_name)

echo "Updating s1 to $s1_ip"

ssh -l bind-adm -p 2204 localhost \
sudo -u bind nsupdate << EOF
server localhost
zone jmibeans-examples.net
update add s1.jmibeans-examples.net 300 A $s1_ip
send
EOF
ssh -l bind-adm -p 2204 localhost \
cat /var/log/bind/update_debug.log

sleep 800

traceroute s1.jmibeans-examples.net
