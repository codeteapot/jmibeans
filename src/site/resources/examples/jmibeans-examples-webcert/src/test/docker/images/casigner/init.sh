#!/bin/sh

/ssh-server-init

date +%s >/var/casigner/private/serial

tail -f /var/log/casigner.log
