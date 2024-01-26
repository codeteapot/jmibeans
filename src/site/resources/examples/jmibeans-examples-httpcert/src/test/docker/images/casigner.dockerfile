FROM debian:bookworm

RUN apt -y update

RUN apt -y install openssh-server wget
RUN wget -O - https://raw.githubusercontent.com/codeteapot/jmibeans/not-only-ssh-connection/src/site/resources/examples/jmibeans-examples-httpcert/src/test/docker/images/ssh-server/setup.sh | /bin/sh
RUN apt -y remove wget
RUN apt -y autoremove

RUN apt -y install openssl sudo

RUN useradd casigner -m -b /var -s /bin/bash

COPY ./casigner/jmi.properties.sh /etc/
RUN chmod +x /etc/jmi.properties.sh
RUN chown casigner /etc/jmi.properties.sh

RUN mkdir /var/casigner/certs
RUN mkdir /var/casigner/csr
RUN mkdir /var/casigner/newcerts
RUN mkdir /var/casigner/private
RUN touch /var/casigner/index.txt
RUN touch /var/casigner/index.txt.attr
RUN echo 1000 >/var/casigner/private/serial
COPY ./casigner/root-cert.pem /var/casigner/certs
COPY ./casigner/signer-cert.pem /var/casigner/certs
COPY ./casigner/signer-key.pem /var/casigner/private
COPY ./casigner/signer.cnf /var/casigner
RUN chown -R casigner:casigner /var/casigner

RUN touch /var/log/casigner.log
RUN chown casigner:casigner /var/log/casigner.log

COPY ./casigner/init.sh /init
RUN chmod +x /init

CMD /init
