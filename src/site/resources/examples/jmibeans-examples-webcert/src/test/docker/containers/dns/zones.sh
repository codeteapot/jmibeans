#!/bin/sh

arg_command=$1

case $arg_command in

  enable)
    arg_zone_name=$2
    arg_address=$3

    cat << EOF >/var/lib/bind/db.$arg_zone_name
\$TTL 3600
@               IN      SOA     ns.$arg_zone_name. root.$arg_zone_name. (
    2007010401  ; Serial
    3600        ; Refresh [1h]
    600         ; Retry   [10m]
    86400       ; Expire  [1d]
    600         ; Negative Cache TTL [1h]
);
@               IN      NS      ns.$arg_zone_name.
ns              IN      A       $arg_address
EOF
    
    sudo rndc addzone $arg_zone_name \
      "{ type master; file \"/var/lib/bind/db.$arg_zone_name\"; allow-update { key rndc-key; }; };"
    exit 0
    ;;
  
  *)
    echo "Command $arg_command is not supported"
    exit 1
    ;;

esac
