#!/bin/sh

/ssh-server-init

a2dissite 000-default >/dev/null
a2ensite example >/dev/null
a2enconf server-name >/dev/null
service apache2 start

tail -f /var/log/apache2/access.log
