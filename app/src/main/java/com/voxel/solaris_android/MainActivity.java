package com.voxel.solaris_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title

        final var window = getWindow();

        WindowCompat.setDecorFitsSystemWindows(window, false);

        setContentView(R.layout.activity_main);

        final var webView = (WebView) findViewById(R.id.webview);
        final var root = findViewById(R.id.root);

        final var insetsController = WindowCompat.getInsetsController(window, root);
        insetsController.setAppearanceLightNavigationBars(false);
        insetsController.setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            final var systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            final var imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());

            final var bottomInset = Math.max(systemBarInsets.bottom, imeInsets.bottom);

            view.setPadding(
                    systemBarInsets.left,
                    systemBarInsets.top,
                    systemBarInsets.right,
                    bottomInset
            );

            return insets;
        });

        webView.clearCache(true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebViewClient(new MyBrowser());

        Intent intent = getIntent();
        if (intent != null) {
            var data = intent.getData();

            if (data != null && data.getHost() != null) {
                if (data.getHost().equals(getResources().getString(R.string.domain))) {
                    webView.loadUrl(data.toString());
                    return;
                }
            }
        }

        webView.loadUrl("https://solaris.games");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView webView = (WebView) findViewById(R.id.webview);

        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
