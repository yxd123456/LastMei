package com.hz.activity.base;

import android.graphics.Color;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hz.R;
import com.hz.util.LocationUtils;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewClient;
import org.apache.cordova.engine.SystemWebViewEngine;

public class WebMapActivity extends CordovaActivity {

    private SystemWebView webView;

    private double[] location;

    private LocationUtils locationUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_map);
        super.init();
        locationUtils = new LocationUtils(this);


        webView.addJavascriptInterface(new JsInteration(), "control");
        loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected CordovaWebView makeWebView() {
        webView = (SystemWebView)findViewById(R.id.web_map);
        return new CordovaWebViewImpl(new SystemWebViewEngine(webView));
    }

    @Override
    protected void createViews() {
        if (preferences.contains("BackgroundColor")) {
            int backgroundColor = preferences.getInteger("BackgroundColor", Color.BLACK);
            // Background of activity:
            appView.getView().setBackgroundColor(backgroundColor);
        }

        appView.getView().requestFocusFromTouch();
    }

    private class JsInteration {

        @JavascriptInterface
        public double getX(){
            getLocation();
            return location[0];
        }

        @JavascriptInterface
        public double getY(){
            getLocation();
            return location[1];
        }

        private void getLocation() {
            locationUtils.getCurrentLocationMessage();
            locationUtils.updateData();
            location = locationUtils.getLocation();
        }

        @JavascriptInterface
        public void toast(){
            Toast.makeText(WebMapActivity.this, "调用了", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetLocation(View v){
        webView.loadUrl("javascript:reset()");
    }

    public void A(View v){
        webView.loadUrl("");
    }

}
