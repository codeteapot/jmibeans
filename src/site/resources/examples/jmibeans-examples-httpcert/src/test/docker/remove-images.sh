#!/bin/sh

docker rmi \
  test-docker-httpd-s1:latest \
  test-docker-httpd-s2:latest \
  test-docker-casigner:latest \
  test-docker-dns:latest \
  test-docker-pubkey-repo:latest \
  test-docker-upload-dev-key:latest
