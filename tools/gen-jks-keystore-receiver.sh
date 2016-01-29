#!/bin/bash

# Use this to generate a self-signed certificate, which is used to secure the
# communication between ffw-alertreceiver and connected listeners (for this
# purpose a self-signed certificate is sufficient).
# See also:
# https://www.sslshopper.com/article-how-to-create-a-self-signed-certificate-using-java-keytool.html
# 
# For description of java-keytool and its options, see:
# http://docs.oracle.com/javase/7/docs/technotes/tools/windows/keytool.html
# - validity is the number of days before the certificate will expire
#   (36500 days = 100 years)

if [ -z "$1" ]; then
  echo "Please provide keystore password: sh $0 PASSWORD"
else
  keytool -genkey -keyalg RSA -alias selfsigned -keystore ffw-receiver-keystore.jks -storepass $1 -validity 36500 -keysize 2048
fi
