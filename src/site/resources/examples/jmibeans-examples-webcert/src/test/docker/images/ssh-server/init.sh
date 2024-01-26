#!/bin/sh

/tmp/authkeys-install
rm /tmp/authkeys-install

/usr/sbin/sshd -D &
