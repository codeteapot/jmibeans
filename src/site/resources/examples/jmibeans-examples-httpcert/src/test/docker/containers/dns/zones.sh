#!/bin/sh

host_ip=$(hostname -I | awk '{print $1}')
host $(echo $host_ip) | awk '{print $1}'

echo '--- /var/lib/bind/zones.conf ---'
echo "zone \"jmibeans-examples.net\" {
    type master;
    file \"/var/lib/bind/db.jmibeans-examples.net\";
    allow-update { key rndc-key; };
};
zone \"20.168.192.in-addr.arpa\" {
    type master;
    file \"/var/lib/bind/db.jmibeans-examples.net.inv\";
    allow-update { key rndc-key; };
};"

echo "--- /var/lib/bind/db.jmibeans-examples.net ---"
echo "\$TTL 3600
@               IN      SOA     ns.jmibeans-examples.net. root.jmibeans-examples.net. (
    2007010401  ; Serial
    3600        ; Refresh [1h]
    600         ; Retry   [10m]
    86400       ; Expire  [1d]
    600         ; Negative Cache TTL [1h]
);

@               IN      NS      ns.jmibeans-examples.net.
@               IN      MX      2 ns.jmibeans-examples.net.

ns              IN      A       $host_ip

disco           IN      CNAME   ns
docker          IN      CNAME   ns
"

echo "--- /var/lib/bind/db.jmibeans-examples.net.inv ---"
echo "@               IN      SOA     ns.jmibeans-examples.net. root.jmibeans-examples.net. (
    2007010401  ; Serial
    3600        ; Refresh [1h]
    600         ; Retry   [10m]
    86400       ; Expire  [1d]
    600         ; Negative Cache TTL [1h]
);

@               IN      NS      ns.jmibeans-examples.net.

2               IN      PTR     ns.jmibeans-examples.net.
"
