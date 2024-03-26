package com.devops.wots48;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.webkit.ClientCertRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.devops.devops_core.WebviewInterface;

/**
 * Created by weiyin on 9/9/15.
 */
public class WebviewClient extends WebViewClient{
    private static final String TAG = WebviewClient.class.getName();
    private UrlNavigation urlNavigation;
    private Context context;

    public WebviewClient(MainActivity mainActivity, UrlNavigation urlNavigation) {
        this.urlNavigation = urlNavigation;
        this.context = mainActivity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return urlNavigation.shouldOverrideUrlLoading((WebviewInterface)view, url);
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url, boolean isReload) {
        return urlNavigation.shouldOverrideUrlLoading((WebviewInterface)view, url, isReload, false);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = request.getUrl();
            return urlNavigation.shouldOverrideUrlLoading((WebviewInterface)view, uri.toString(), false, request.isRedirect());
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        urlNavigation.onPageStarted(url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        urlNavigation.onPageFinished((WebviewInterface)view, url);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        urlNavigation.onFormResubmission((WebviewInterface)view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        urlNavigation.doUpdateVisitedHistory((WebviewInterface)view, url, isReload);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        urlNavigation.onReceivedError((WebviewInterface) view, errorCode, description, failingUrl);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        urlNavigation.onReceivedError((WebviewInterface) view, error.getErrorCode(),
                error.getDescription().toString(), request.getUrl().toString());
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.cancel();
        urlNavigation.onReceivedSslError(error, view.getUrl());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        urlNavigation.onReceivedClientCertRequest(view.getUrl(), request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return urlNavigation.interceptHtml((LeanWebView)view, url);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        WebResourceResponse wr = ((Application) context.getApplicationContext()).mBridge.interceptHtml((MainActivity) context, request);
        if (wr != null) {
            return wr;
        }

        String method = request.getMethod();
        if (method == null || !method.equalsIgnoreCase("GET")) return null;

        android.net.Uri uri = request.getUrl();
        if (uri == null || !uri.getScheme().startsWith("http")) return null;

        return shouldInterceptRequest(view, uri.toString());
    }
}
