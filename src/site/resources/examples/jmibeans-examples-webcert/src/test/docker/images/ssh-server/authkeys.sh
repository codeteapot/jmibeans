#!/bin/sh

cat /etc/authkeys 2>/dev/null | grep -oP "^${1}:\K.*$" | xargs cat 2>/dev/null
exit 0 # This is required to avoid status 123
