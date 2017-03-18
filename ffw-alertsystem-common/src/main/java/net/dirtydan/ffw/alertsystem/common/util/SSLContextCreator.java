/*
  Copyright (c) 2015-2017, Max Stark <max.stark88@web.de>
    All rights reserved.
  
  This file is part of ffw-alertsystem, which is free software: you
  can redistribute it and/or modify it under the terms of the GNU
  General Public License as published by the Free Software Foundation,
  either version 2 of the License, or (at your option) any later
  version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, see <http://www.gnu.org/licenses/>.
*/

package net.dirtydan.ffw.alertsystem.common.util;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.io.FileReader;
import java.io.IOException;

import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;



public final class SSLContextCreator {
  
  private SSLContextCreator() {}
  
  
  
  public static SSLContext create(String certCAFile, String certClientFile,
                                  String keyFile, String password) {
    // add BouncyCastle as a Security Provider
    Security.addProvider(new BouncyCastleProvider());
    
    // load Certificate Authority (CA) certificate
    X509Certificate crtCA = getCertificate(certCAFile);
    // load client certificate
    X509Certificate crtClient = getCertificate(certClientFile);
    // load client private key
    KeyPair key = getKeyPair(keyFile, password);
    
    SSLContext context = null;
    
    try {
      // CA-certificate is used to authenticate the server
      KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      caKeyStore.load(null, null);
      caKeyStore.setCertificateEntry("ca-certificate", crtCA);
      
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(caKeyStore);
      
      // send client-key and client-certificate to server, so it can
      // authenticate the client
      KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      clientKeyStore.load(null, null);
      clientKeyStore.setCertificateEntry("certificate", crtClient);
      clientKeyStore.setKeyEntry(
                       "private-key", key.getPrivate(), password.toCharArray(),
                       new Certificate[] { crtClient }
                     );
      
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
              KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(clientKeyStore, password.toCharArray());
      
      // create SSLContext
      context = SSLContext.getInstance("TLSv1.2");
      context.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                null
              );
      
    } catch (IOException | KeyStoreException | NoSuchAlgorithmException |
             CertificateException | UnrecoverableKeyException |
             KeyManagementException e) {
      e.printStackTrace();
    }
    
    return context;
  }
  
  
  
  private static X509Certificate getCertificate(String certFile) {
    X509Certificate cert = null;
    
    try {
      PEMParser pem = new PEMParser(new FileReader(certFile));
      X509CertificateHolder certHolder = (X509CertificateHolder) pem.readObject();
      pem.close();
      
      cert = new JcaX509CertificateConverter().setProvider("BC")
                   .getCertificate(certHolder);
      
    } catch (IOException | CertificateException e) {
      e.printStackTrace();
    }
    
    return cert;
  }
  
  private static KeyPair getKeyPair(String keyFile, String password) {
    KeyPair key = null;
    
    try {
      PEMParser pem = new PEMParser(new FileReader(keyFile));
      Object keyObject = pem.readObject();
      pem.close();
      
      PEMDecryptorProvider provider = new JcePEMDecryptorProviderBuilder()
                                            .build(password.toCharArray());
      JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter()
                                              .setProvider("BC");
      
      if (keyObject instanceof PEMEncryptedKeyPair) {
        key = keyConverter.getKeyPair(((PEMEncryptedKeyPair) keyObject)
                          .decryptKeyPair(provider));
      } else {
        key = keyConverter.getKeyPair((PEMKeyPair) keyObject);
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return key;
  }
  
}
