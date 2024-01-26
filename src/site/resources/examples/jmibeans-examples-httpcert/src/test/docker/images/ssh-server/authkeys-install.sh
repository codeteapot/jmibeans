#!/bin/sh

echo "$SSH_PUBLIC_KEYS" | tr ',' '\n' >>/etc/authkeys && unset SSH_PUBLIC_KEYS
