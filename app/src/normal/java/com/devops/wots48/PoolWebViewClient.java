package com.devops.wots48;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.devops.devops_core.WebviewInterface;

/**
 * Created by weiyin on 9/9/15.
 */
public class PoolWebViewClient extends WebViewClient {
    private WebViewPool.WebViewPoolCallback webViewPoolCallback;

    public PoolWebViewClient(WebViewPool.WebViewPoolCallback webViewPoolCallback, LeanWebView view) {
        this.webViewPoolCallback = webViewPoolCallback;
        view.setWebViewClient(this);
    }

    @Override
    public void onPageFinished(final WebView view, String url) {
        super.onPageFinished(view, url);

        // remove self as webviewclient
        new Handler(view.getContext().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                view.setWebViewClient(null);
            }
        });

        webViewPoolCallback.onPageFinished((WebviewInterface) view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return webViewPoolCallback.interceptHtml((WebviewInterface)view, url);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String method = request.getMethod();
        if (method == null || !method.equalsIgnoreCase("GET")) return null;

        android.net.Uri uri = request.getUrl();
        if (uri == null || !uri.getScheme().startsWith("http")) return null;

        return shouldInterceptRequest(view, uri.toString());
    }
}
