#!/bin/sh

git_branch_name="not-only-ssh-connection"

setup_files_base_url="https://raw.githubusercontent.com/codeteapot/jmibeans/"
setup_files_base_url="$setup_files_base_url/$git_branch_name"
setup_files_base_url="$setup_files_base_url/src/site/resources/examples/jmibeans-examples-httpcert"
setup_files_base_url="$setup_files_base_url/src/test/docker/images/ssh-server"

wget -O /ssh-server-init "$setup_files_base_url/init.sh" # Docker
wget -O /usr/bin/authkeys "$setup_files_base_url/authkeys.sh" 
wget -O /tmp/authkeys-install "$setup_files_base_url/authkeys-install.sh"

chmod +x /ssh-server-init # Docker
chmod +x /usr/bin/authkeys
chmod +x /tmp/authkeys-install

sed -i 's/^#.*StrictModes .*$/StrictModes yes/g' \
/etc/ssh/sshd_config
sed -i 's/^#.*PubkeyAuthentication .*$/PubkeyAuthentication yes/g' \
/etc/ssh/sshd_config
sed -i 's/^#.*AuthorizedKeysCommand .*$/AuthorizedKeysCommand \/usr\/bin\/authkeys %u/g' \
/etc/ssh/sshd_config
sed -i 's/^#.*AuthorizedKeysCommandUser .*$/AuthorizedKeysCommandUser nobody/g' \
/etc/ssh/sshd_config

# FIX: Missing privilege separation directory: /run/sshd
mkdir /var/run/sshd
chmod 0755 /var/run/sshd
