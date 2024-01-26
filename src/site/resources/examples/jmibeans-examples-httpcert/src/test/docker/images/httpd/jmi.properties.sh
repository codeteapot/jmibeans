#!/bin/sh

server_name=$(sed -ne 's/^ServerName\s\+\(.*\)$/\1/p' /etc/apache2/conf-enabled/server-name.conf)

echo "
serverName=$server_name
siteName=example-ssl
certificatePath=/etc/ssl/certs/ssl-cert.pem
privateKeyAlgorithm=RSA
privateKeySize=2048
privateKeyPath=/etc/ssl/private/ssl-cert.key
"
