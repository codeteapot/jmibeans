#!/bin/bash

mkdir -p /var/lib/ddns/bind

echo "
zone \"pto.dev\" {
    type master;
    file \"/var/lib/bind/db.pto.dev\";
    allow-update { key rndc-key; };
};
zone \"20.168.192.in-addr.arpa\" {
    type master;
    file \"/var/lib/bind/db.pto.dev.inv\";
    allow-update { key rndc-key; };
};
" >> /var/lib/ddns/bind/zones.conf

echo "\$TTL 3600
@               IN      SOA     ns.pto.dev. root.pto.dev. (
    2007010401  ; Serial
    3600        ; Refresh [1h]
    600         ; Retry   [10m]
    86400       ; Expire  [1d]
    600         ; Negative Cache TTL [1h]
);

@               IN      NS      ns.pto.dev.
@               IN      MX      2 ns.pto.dev.

ns              IN      A       192.168.20.2

disco           IN      CNAME   ns
docker          IN      CNAME   ns
" > /var/lib/ddns/bind/db.pto.dev

echo "@               IN      SOA     ns.pto.dev. root.pto.dev. (
    2007010401  ; Serial
    3600        ; Refresh [1h]
    600         ; Retry   [10m]
    86400       ; Expire  [1d]
    600         ; Negative Cache TTL [1h]
);

@               IN      NS      ns.pto.dev.

2               IN      PTR     ns.pto.dev.
" > /var/lib/ddns/bind/db.pto.dev.inv

cd /tmp/ddns
docker build -t pto/ddns .
rm -rf /tmp/ddns

docker run -d \
    -p 80:80 \
    -p 53:53/udp \
    -v /var/lib/ddns/bind:/var/lib/bind \
    --restart=always --name ddns \
    pto/ddns
