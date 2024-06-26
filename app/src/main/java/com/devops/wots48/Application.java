package com.devops.wots48

import android.content.ComponentCallbacks2;
import android.os.Message;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import java.util.List;
import java.util.Map;

import com.devops.devops_core.AppConfig;
import com.devops.devops_core.Bridge;
import com.devops.devops_core.BridgeModule;
import com.devops.devops_core.GNLog;

/**
 * Created by weiyin on 9/2/15.
 * Copyright 2014 GoNative.io LLC
 */
public class Application extends MultiDexApplication {

    private LoginManager loginManager;
    private RegistrationManager registrationManager;
    private WebViewPool webViewPool;
    private Message webviewMessage;
    private WindowManager WindowManager;
    private List<BridgeModule> plugins;
    private final static String TAG = GoNativeApplication.class.getSimpleName();
    public final Bridge mBridge = new Bridge(this) {
        @Override
        protected List<BridgeModule> getPlugins() {
            if (GoNativeApplication.this.plugins == null) {
                Application.this.plugins = new PackageList(Application.this).getPackages();
            }

            return  Application.this.plugins;
        }
    };

    private boolean appBackgrounded = false;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        mBridge.onApplicationCreate(this);

        AppConfig appConfig = AppConfig.getInstance(this);
        if (appConfig.configError != null) {
            Toast.makeText(this, "Invalid appConfig json", Toast.LENGTH_LONG).show();
            GNLog.getInstance().logError(TAG, "AppConfig error", appConfig.configError);
        }

        this.loginManager = new LoginManager(this);

        if (appConfig.registrationEndpoints != null) {
            this.registrationManager = new RegistrationManager(this);
            registrationManager.processConfig(appConfig.registrationEndpoints);
        }

        // some global webview setup
        WebViewSetup.setupWebviewGlobals(this);

        webViewPool = new WebViewPool();

        goNativeWindowManager = new WindowManager();
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public RegistrationManager getRegistrationManager() {
        return registrationManager;
    }

    public WebViewPool getWebViewPool() {
        return webViewPool;
    }

    public Message getWebviewMessage() {
        return webviewMessage;
    }

    public void setWebviewMessage(Message webviewMessage) {
        this.webviewMessage = webviewMessage;
    }

    public Map<String, Object> getAnalyticsProviderInfo() {
        return mBridge.getAnalyticsProviderInfo();
    }

    public WindowManager getWindowManager() {
        return goNativeWindowManager;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // App has gone into the background
            setAppBackgrounded(true);
        }
    }

    public boolean isAppBackgrounded() {
        return appBackgrounded;
    }

    public void setAppBackgrounded(boolean appBackgrounded) {
        this.appBackgrounded = appBackgrounded;
    }
}
