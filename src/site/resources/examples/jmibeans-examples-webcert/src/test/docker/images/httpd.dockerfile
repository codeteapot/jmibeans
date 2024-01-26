FROM debian:bookworm

RUN apt -y update

RUN apt -y install openssh-server wget
RUN wget -O - https://raw.githubusercontent.com/codeteapot/jmibeans/refs/heads/not-only-ssh-connection/src/site/resources/examples/jmibeans-examples-webcert/src/test/docker/images/ssh-server/setup.sh | /bin/sh
RUN apt -y remove wget
RUN apt -y autoremove

RUN apt -y install apache2 sudo
COPY ./httpd/example.conf /etc/apache2/sites-available/
COPY ./httpd/example-ssl.conf /etc/apache2/sites-available/

RUN useradd apache-adm -m -s /bin/bash
RUN echo 'apache-adm ALL=(ALL) NOPASSWD: ALL' >>/etc/sudoers

RUN mkdir -p /etc/ssl/certs
RUN chown apache-adm /etc/ssl/certs
RUN mkdir -p /etc/ssl/private
RUN chown apache-adm /etc/ssl/private

RUN a2enmod ssl
RUN a2dissite 000-default
RUN a2ensite example
RUN echo 'ServerName localhost' >/etc/apache2/conf-available/default-server-name.conf
RUN a2enconf default-server-name

COPY ./httpd/init.sh /init
RUN chmod +x /init

CMD /init
