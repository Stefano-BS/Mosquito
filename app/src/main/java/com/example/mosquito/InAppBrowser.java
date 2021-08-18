package com.example.mosquito;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class InAppBrowser extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_browser);
        getSupportActionBar().hide();

        String link = getIntent().getStringExtra("link");
        //getSupportActionBar().setTitle(link);
        WebView browser = findViewById(R.id.inAppBrowserView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setLoadWithOverviewMode(true);
        browser.getSettings().setUseWideViewPort(true);
        WebViewClient client = new WebViewClient();
        browser.setWebViewClient(client);
        browser.loadUrl(link);
    }
}