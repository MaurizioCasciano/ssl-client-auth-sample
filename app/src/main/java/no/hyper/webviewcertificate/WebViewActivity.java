package no.hyper.webviewcertificate;

import android.annotation.TargetApi;
import android.net.http.SslError;
import android.os.Build;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.security.KeyChainException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ClientCertRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class WebViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWebView();
    }

    public void setupWebView() {
        WebView webView = (WebView)findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(wvc);
        webView.loadUrl("https://cxz.asuscomm.com:8443");
    }

    WebViewClient wvc = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            /*
            * To solve Exception "Trust anchor for certification path not found"
            * All of my certificates are self-signed, which are not trusted.
            *
            * This issue should not happen in production environment.
            */
            handler.proceed();
        }

        @Override
        public void onReceivedClientCertRequest(WebView view, final ClientCertRequest request) {
            Log.v(getClass().getSimpleName(), "===> certificate required!");

            KeyChain.choosePrivateKeyAlias(WebViewActivity.this, new KeyChainAliasCallback(){
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void alias(String alias) {
                    Log.v(getClass().getSimpleName(), "===>Key alias is: " + alias);
                    try {
                        PrivateKey changPrivateKey = KeyChain.getPrivateKey(WebViewActivity.this, alias);
                        X509Certificate[] certificates = KeyChain.getCertificateChain(WebViewActivity.this, alias);
                        Log.v(getClass().getSimpleName(), "===>Getting Private Key Success!" );
                        request.proceed(changPrivateKey, certificates);
                    } catch (KeyChainException e) {
                        Log.e(getClass().getSimpleName(), Util.printException(e));
                    } catch (InterruptedException e) {
                        Log.e(getClass().getSimpleName(), Util.printException(e));
                    }
                }
            },new String[]{"RSA"}, null, null, -1, null);
        }
    };
}
