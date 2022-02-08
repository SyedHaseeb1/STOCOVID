package com.hsb.covid_19_predictor_fyp_v10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class Stock_Market_WebPage extends AppCompatActivity {

    WebView stock_webview;
    ProgressBar stock_webview_pbr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_market_web_page);
        stock_webview=findViewById(R.id.stock_webview);
        stock_webview_pbr=findViewById(R.id.stock_webview_pbr);
        stock_webview_pbr.setVisibility(View.VISIBLE);

        stock_webview.getSettings().setJavaScriptEnabled(true);
        stock_webview.getSettings().setLoadWithOverviewMode(true);
        stock_webview.getSettings().setUseWideViewPort(true);
        stock_webview.getSettings().setBuiltInZoomControls(true);
        stock_webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        stock_webview.loadUrl("https://tradingeconomics.com/pakistan/indicators");
        stock_webview.setWebViewClient(new myWebClient());
    }

    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            stock_webview_pbr.setVisibility(View.GONE);
            super.onPageFinished(view, url);


        }
    }

    //flip screen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }




}
