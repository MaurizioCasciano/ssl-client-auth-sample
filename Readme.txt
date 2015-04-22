Prior to Android L, there's no official way to enforce a client authentication in SSL handshaking process. The new API
in Android L has changed this, but prior-to-5.0 Android phones, client-auth might be required. 

This project demonstrates how to establish SSL **mutual_authentication** between a tomcat server and client through
 1) new feature in Lollipop client-authentication in WebViewClient.
	WebViewClient::onReceivedClientCertRequest()
 2) SSL socket

======================
Software version
======================
Tomcat: 7.0.61
JDK: javac 1.8.0_40

======================
Generate Keys
======================
1. Generate public/private keys and certificates for server side
2. Generate Public/Private keys and certificates for client side

Please refer to ${PROJ_ROOT}\key\key_gen.bat

After running the key_gen.bat a set of files would be generated.
 - chang.jks - Keystore file for client, which comprises a private key and a self-signed certificates
 - chang.pfx - Same as above, but in a PKCS12 representation. Chrome/Firefox/Android only recognize this format, not jks
 - chang-public.cer - Public key and a self-signed certificate
 
 - server.jks - Keystore file for tomcat server, comprises a private key and a certificate
 - server.pfx - Same as above, in PKCS12 representation.
 - server-public.cer - Public key and a self-signed certificate
 
 - trusted-keystore.jks - Keystore file for tomcat server, which contains all the trusted client's certificate & public 
 key.
======================
Server setup
======================
1. Install tomcat server
2. Put the server required keystore files to ${TOMCAT_HOME}\conf. The required files are as following
 - server.jks
 - trusted-keystore.jsk
 
3. Change file ${TOMCAT_HOME}\conf\server.xml, add a connector listed as following to enable SSL and client certificate.


    <Connector
        port="8443"
        protocol="org.apache.coyote.http11.Http11Protocol"
        maxThreads="20"
        SSLEnabled="true"
        scheme="https"
        secure="true"
        sslProtocol="TLS"

        clientAuth="true"

        keystoreFile="/var/apache-tomcat-7.0.61/conf/server.jks"
        keystoreType="JKS"
        keystorePass="password"

        truststoreFile="/var/apache-tomcat-7.0.61/conf/trusted-keystore.jks"
        truststoreType="JKS"
        truststorePass="password"

        SSLVerifyClient="require"
        SSLEngine="on"
        SSLVerifyDepth="2"
        />
		
		
======================
Test server setup
======================
Use your browser to access the HTTPS service. Since our keys are not signed by a Valid CA, security warning are 
expected, just Ignore and proceed. Chrome/Firefox/IE will asks certificate for client authentication.