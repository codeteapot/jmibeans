#!/bin/sh

/ssh-server-init

service apache2 start
tail -f /var/log/apache2/access.log
