package com.bvutest.core.pwa;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

class PwaWebViewClient extends WebViewClient {
    private Pattern scope_pattern;
    private ArrayList<Pattern> scopePatterns = new ArrayList<>();
    private ArrayList<String> filters = new ArrayList<>(Arrays.asList("filterReplace"));
//    private ArrayList<String> filters = new ArrayList<>(Arrays.asList(".ebay.com", ".ebayinc.com"));
    private static final String TAG = "PwaWebViewClient";

    public PwaWebViewClient(String start_url, String scope) {
        try {
            URL baseUrl = new URL(start_url);
            URL scopeUrl = new URL(baseUrl, scope);
            if (!scopeUrl.toString().endsWith("*")) {
                scopeUrl = new URL(scopeUrl, "*");
            }

            this.scope_pattern = this.regexFromPattern(scopeUrl.toString());
            scopePatterns.add(this.regexFromPattern(scopeUrl.toString()));
//            urlList.add(start_url);
        } catch (MalformedURLException e) {
            this.scope_pattern = null;
        }

//        try{
//            for(String item : urlList){
//                URL baseUrl = new URL(item);
//                URL scopeUrl = new URL(baseUrl, scope);
//                scopeUrl = new URL(scopeUrl, "*");
//                scopePatterns.add(this.regexFromPattern(scopeUrl.toString()));
//            }
//        }catch(Exception e){
//            Log.d(TAG, "error adding URL to pattern array >> " + e);
//        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        if (this.scoped(url)) {
//            Log.d(TAG, "shouldOverrideUrlLoading >> return false");
//            return false;
//        } else {
//            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            view.getContext().startActivity(i);
//            Log.d(TAG, "shouldOverrideUrlLoading >> return true");
//            return true;
//        }
        if (checkUrl(url)) {
            Log.d(TAG, "shouldOverrideUrlLoading >> return false");
            return false;
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(i);
            Log.d(TAG, "shouldOverrideUrlLoading >> return true");
            return true;
        }
//        return false;
    }

    private boolean checkUrl(String url){
        for(String item : filters){
            Log.d(TAG, "checkUrl >> " + url + " | " + item);
            if(url.contains(item)){
                Log.d(TAG, "checkUrl == match >> return true");
                return true;
            }
        }
        return false;
    }

    private boolean scoped(String url) {
        if(this.scope_pattern == null ){
            Log.d(TAG, "scoped == null >> return true");
            return true;
        }
        for(Pattern item : scopePatterns){
            Log.d(TAG, "scoped == matcher >> " + url + " | " + item.toString());
            if(item.matcher(url).matches()){
                Log.d(TAG, "scoped == match >> return true");
                return true;
            }
        }
        Log.d(TAG, "scoped != match >> return false");
        return false;
//        return this.scope_pattern.matcher(url).matches();
    }

    private Pattern regexFromPattern(String pattern) {
        final String toReplace = "\\.[]{}()^$?+|";
        StringBuilder regex = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '*') {
                regex.append(".");
            } else if (toReplace.indexOf(c) > -1) {
                regex.append('\\');
            }
            regex.append(c);
        }
        return Pattern.compile(regex.toString());
    }
}

