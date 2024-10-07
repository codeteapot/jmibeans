FROM debian:bookworm

RUN apt -y update

RUN apt -y install openssh-server wget
RUN wget -O - https://raw.githubusercontent.com/codeteapot/jmibeans/not-only-ssh-connection/src/site/resources/examples/jmibeans-examples-httpcert/src/test/docker/images/ssh-server/setup.sh | /bin/sh
RUN apt -y remove wget
RUN apt -y autoremove

RUN apt -y install bind9 dnsutils sudo

COPY ./dns/named.conf /etc/bind/
COPY ./dns/named.conf.local /etc/bind/
COPY ./dns/named.conf.log /etc/bind/
COPY ./dns/zones.conf /var/lib/bind/

RUN mkdir -p /var/log/bind

# dnssec-keygen -a HMAC-MD5 -b 512 -n USER ns-ddns_rndc-key
# Not possible. Not enough entropy :(
RUN cp /etc/bind/rndc.key /etc/bind/ns-ddns_rndc.key

RUN chown -R bind:bind /var/lib/bind
RUN chown -R bind:bind /var/log/bind
RUN chown -R bind:bind /var/cache/bind
RUN chown -R bind:bind /etc/bind

RUN useradd bind-adm -m -s /bin/bash
RUN echo 'bind-adm ALL=(ALL) NOPASSWD: ALL' >>/etc/sudoers

COPY ./dns/init.sh /init
RUN chmod +x /init

CMD /init
