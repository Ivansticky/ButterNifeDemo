package ivansticky.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {

    private WebView mWebView;
    private TextView logTextView;
    private Boolean mFromsplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebChromeClient(new WebChromeClient());
        WebSettings mSettings = mWebView.getSettings();
        mSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsInteraction(), "control");
        mWebView.loadUrl("file:///android_asset/text.html");
        mWebView.addJavascriptInterface(this, "wx");
        logTextView = (TextView) findViewById(R.id.text);
        mFromsplash = getIntent().getBooleanExtra("fromsplash", false);


    }

    @Override
    public void onBackPressed() {

        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else if (mFromsplash) {
            gotoMainActivity();
        } else {
            super.onBackPressed();
        }


    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class JsInteraction {
        @JavascriptInterface
        public void toastMessage(String message) {   //提供给js调用的方法
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 拨打电话
     *
     * @param phone
     */
    @JavascriptInterface
    public void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);

    }
}
