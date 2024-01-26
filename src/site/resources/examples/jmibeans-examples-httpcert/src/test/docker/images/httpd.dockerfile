FROM debian:bookworm

RUN apt -y update

RUN apt -y install openssh-server wget
RUN wget -O - https://raw.githubusercontent.com/codeteapot/jmibeans/not-only-ssh-connection/src/site/resources/examples/jmibeans-examples-httpcert/src/test/docker/images/ssh-server/setup.sh | /bin/sh
RUN apt -y remove wget
RUN apt -y autoremove

RUN apt -y install apache2 sudo
COPY ./httpd/server-name.conf /etc/apache2/conf-available/
COPY ./httpd/example.conf /etc/apache2/sites-available/
COPY ./httpd/example-ssl.conf /etc/apache2/sites-available/
COPY ./httpd/index.html /var/www/
COPY ./httpd/jmi.properties.sh /etc/
RUN chmod +x /etc/jmi.properties.sh

RUN useradd apache-adm -m -s /bin/bash
RUN echo 'apache-adm ALL=(ALL) NOPASSWD: ALL' >>/etc/sudoers

RUN chown apache-adm /etc/jmi.properties.sh
RUN mkdir -p /etc/ssl/certs
RUN chown apache-adm /etc/ssl/certs
RUN mkdir -p /etc/ssl/private
RUN chown apache-adm /etc/ssl/private

RUN a2enmod ssl

COPY ./httpd/init.sh /init
RUN chmod +x /init

CMD /init
