#!/bin/sh

scriptname=$(basename $0)
basedir=$(dirname $0)
builddir=$(mktemp -d)
buildrootdir=$builddir/root
buildsignerdir=$builddir/signer
targetdir=$1

if [ -z "$targetdir" ]
then
  cat << EOF
Target directory is not specified.

Usage: $scriptname <targetdir>
EOF
  exit 1
fi

if [ ! -d $targetdir ]
then
  cat << EOF
Target directory $targetdir is not a directory.
EOF
  exit 1
fi

mkdir $buildrootdir
mkdir $buildrootdir/certs
mkdir $buildrootdir/csr
mkdir $buildrootdir/newcerts
mkdir $buildrootdir/private

touch $buildrootdir/index.txt
touch $buildrootdir/index.txt.attr
echo 1000 > $buildrootdir/private/serial

mkdir $buildsignerdir
mkdir $buildsignerdir/private

cp $basedir/root.cnf $builddir/root.cnf

export BUILDDIR=$builddir

openssl genrsa \
  -aes256 \
  -out $buildrootdir/private/key.pem \
  -passout pass:12345678 \
  4096

openssl req \
  -config $builddir/root.cnf \
  -key $buildrootdir/private/key.pem \
  -new \
  -x509 \
  -days 7300 \
  -sha256 \
  -extensions v3_ca \
  -out $buildrootdir/certs/cert.pem \
  -passin pass:12345678 \
  -subj '/CN=JMI Beans Examples Root/O=JMI Beans Examples/OU=Development'

openssl genrsa \
  -aes256 \
  -out $buildsignerdir/private/key.pem \
  -passout pass:12345678 \
  4096

openssl req \
  -key $buildsignerdir/private/key.pem \
  -new \
  -sha256 \
  -out $buildrootdir/csr/signer.pem \
  -passin pass:12345678 \
  -subj '/CN=JMI Beans Examples CA/O=JMI Beans Examples/OU=Development'

openssl ca \
  -config $builddir/root.cnf \
  -extensions v3_intermediate_ca \
  -days 3650 \
  -notext \
  -md sha256 \
  -in $buildrootdir/csr/signer.pem \
  -out $buildrootdir/newcerts/signer.pem \
  -passin pass:12345678 \
  -batch

cp $buildrootdir/certs/cert.pem $targetdir/root-cert.pem
cp $buildsignerdir/private/key.pem $targetdir/signer-key.pem
cp $buildrootdir/newcerts/signer.pem $targetdir/signer-cert.pem

rm -r $builddir
