#!/bin/sh

/ssh-server-init

/usr/sbin/named -f -u bind
