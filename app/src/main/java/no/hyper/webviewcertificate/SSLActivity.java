package no.hyper.webviewcertificate;

import android.os.Bundle;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.security.KeyChainException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * This is an example on how to create an SSL with client-authentication connection to a server.
 *
 * We should observer the HTML script on the console
 */
public class SSLActivity extends ActionBarActivity {

    private static String selectedAlias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KeyChain.choosePrivateKeyAlias(this, new KeyChainAliasCallback(){
            @Override
            public void alias(String alias) {
                selectedAlias = alias;
                new SslThread().start();
            }
        },new String[]{"RSA"}, null, null, -1, null);

    }


    public class SslThread extends Thread {

        private KeyManager[] buildPrivateKeyManager() throws KeyChainException, InterruptedException, IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
            String UNSAFE_PASSWORD = "whatever";

            KeyStore privateKeyStore = KeyStore.getInstance("PKCS12");
            privateKeyStore.load(null, null);
            PrivateKey changPrivateKey = KeyChain.getPrivateKey(SSLActivity.this, selectedAlias);
            X509Certificate[] certificates = KeyChain.getCertificateChain(SSLActivity.this, selectedAlias);
            privateKeyStore.setKeyEntry(selectedAlias, changPrivateKey, UNSAFE_PASSWORD.toCharArray(), certificates );

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(privateKeyStore, UNSAFE_PASSWORD.toCharArray());
            return kmf.getKeyManagers();
        }

        private TrustManager[] buildPublicTrustManager() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream caInput = new BufferedInputStream(getAssets().open("server-public.cer"));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore trustKeyStore = KeyStore.getInstance(keyStoreType);
            trustKeyStore.load(null, null);
            trustKeyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trustKeyStore);

            return tmf.getTrustManagers();
        }

        public void openSSL() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, InterruptedException, KeyChainException, UnrecoverableKeyException {
            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");

            KeyManager[] privateKeyManagers = buildPrivateKeyManager();
            TrustManager[] trustManagers = buildPublicTrustManager();

            context.init(privateKeyManagers, trustManagers, null);

            URL url = new URL("https://cxz.asuscomm.com:8443");
            HttpsURLConnection urlConnection =
                    (HttpsURLConnection)url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            InputStream in = urlConnection.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            byte[] buf = new byte[4096];
            Log.v(getClass().getSimpleName(), "===>Start Reading HTML");
            dis.readFully(buf);
            String str = new String(buf, "UTF-8");
            Log.v(getClass().getSimpleName(), "===>" + str);
            in.close();
        }

        @Override
        public void run() {
            try {
                openSSL();
            } catch (IOException e) {
                Log.w(getClass().getSimpleName(), Util.printException(e));
            } catch (CertificateException e) {
                Log.w(getClass().getSimpleName(), Util.printException(e));

            } catch (NoSuchAlgorithmException e) {
                Log.w(getClass().getSimpleName(), Util.printException(e));

            } catch (KeyStoreException e) {
                Log.w(getClass().getSimpleName(), Util.printException(e));

            } catch (KeyManagementException e) {
                Log.w(getClass().getSimpleName(), Util.printException(e));

            } catch (InterruptedException e) {
                Log.w(getClass().getSimpleName(), Util.printException(e));

            } catch (KeyChainException e) {
                Log.w(getClass().getSimpleName(), Util.printException(e));

            } catch (UnrecoverableKeyException e) {
                Log.w(getClass().getSimpleName(), Util.printException(e));

            }
        }
    }
}
