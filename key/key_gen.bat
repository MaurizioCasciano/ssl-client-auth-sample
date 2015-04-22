# generate public keys for client/server respectively

keytool -genkeypair -alias serverkey -keyalg RSA -dname "CN=cxz.asuscomm.com,OU=R&D,O=Hyper.no,L=OSLO,S=OSLO,C=NO" -keypass password -keystore server.jks -storepass password -validity 3650
keytool -genkeypair -alias changkey -keyalg RSA -dname "CN=chang,OU=R&D,O=Hyper.no,L=OSLO,S=OSLO,C=NO" -keypass password -storepass password -keystore chang.jks -validity 3650

# export client certificate 
keytool -exportcert -alias changkey -file chang-public.cer -keystore chang.jks -storepass password

# In context of tomcat using, jks is expected
keytool  -importcert -file chang-public.cer -keystore trusted-keystore.jks -alias "chang"
keytool -list -keystore trusted-keystore.jks -storepass password
 
# export server certificate DER format
keytool -exportcert -alias serverkey -file server-public.cer -keystore server.jks -storepass password

# PKCS12 keystore is commonly used in Windows & Android.
keytool -importkeystore -srckeystore chang.jks -srcstoretype JKS -srcstorepass password -destkeystore chang.pfx -deststoretype PKCS12 -deststorepass password
