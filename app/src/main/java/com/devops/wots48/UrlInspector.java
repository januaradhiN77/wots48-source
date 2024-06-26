package com.devops.wots48;


import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class UrlInspector {
    private static final String TAG = UrlInspector.class.getName();
    // singleton
    private static UrlInspector instance = null;

    private Pattern userIdRegex = null;

    private String userId = null;


    public static UrlInspector getInstance(){
        if (instance == null) {
            instance = new UrlInspector();
        }
        return instance;
    }

    public void init(Context context) {
        String regexString = AppConfig.getInstance(context).userIdRegex;
        if (regexString != null && !regexString.isEmpty()) {
            try {
                userIdRegex = Pattern.compile(regexString);
            } catch (PatternSyntaxException e) {
                GNLog.getInstance().logError(TAG, e.getMessage(), e);
            }
        }
    }

    private UrlInspector() {
        // prevent direct instantiation
    }

    public void inspectUrl(String url) {
        if (userIdRegex != null) {
            Matcher matcher = userIdRegex.matcher(url);
            if (matcher.groupCount() > 0 && matcher.find()) {
                setUserId(matcher.group(1));
            }
        }
    }

    public String getUserId() {
        return userId;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }
}
