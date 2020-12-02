package com.bvutest.core.pwa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.aaid.entity.AAIDResult;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.AudioFocusType;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.ads.splash.SplashAdDisplayListener;
import com.huawei.hms.ads.splash.SplashView;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.common.ApiException;

import org.json.*;

import java.io.*;
import java.util.Arrays;

import static com.huawei.hms.analytics.type.HAEventType.SUBMITSCORE;
import static com.huawei.hms.analytics.type.HAParamType.SCORE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private WebView myWebView;

    //HMS ADS
    private BannerView defaultBannerView;
    private static final int REFRESH_TIME = 30;

    // Ad display timeout interval, in milliseconds.
    private static final int AD_TIMEOUT = 5000;

    // Ad display timeout message flag.
    private static final int MSG_AD_TIMEOUT = 1001;

    private boolean hasPaused = false;

    private SplashView splashView;
    
//    private boolean splashEnable = false;
//    private boolean bannerEnable = false;
//    private boolean enableTopBanner = false;
    private boolean splashEnable = true;
    private boolean bannerEnable = false;
    private boolean enableTopBanner = false;

    //HMS ANALYTICS
    HiAnalyticsInstance instance;

    private Handler timeoutHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (MainActivity.this.hasWindowFocus()) {
                jump();
            }
            return false;
        }
    });

    private SplashView.SplashAdLoadListener splashAdLoadListener = new SplashView.SplashAdLoadListener() {
        @Override
        public void onAdLoaded() {
            // Call this method when an ad is successfully loaded.
            Log.i(TAG, "SplashAdLoadListener onAdLoaded.");
//            Toast.makeText(MainActivity.this, getString(R.string.status_load_ad_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            // Call this method when an ad fails to be loaded.
            Log.i(TAG, "SplashAdLoadListener onAdFailedToLoad, errorCode: " + errorCode);
//            Toast.makeText(MainActivity.this, getString(R.string.status_load_ad_fail) + errorCode, Toast.LENGTH_SHORT).show();
            jump();
        }

        @Override
        public void onAdDismissed() {
            // Call this method when the ad display is complete.
            Log.i(TAG, "SplashAdLoadListener onAdDismissed.");
//            Toast.makeText(MainActivity.this, getString(R.string.status_ad_dismissed), Toast.LENGTH_SHORT).show();
            jump();
        }
    };

    private SplashAdDisplayListener adDisplayListener = new SplashAdDisplayListener() {
        @Override
        public void onAdShowed() {
            // Call this method when an ad is displayed.
            Log.i(TAG, "SplashAdDisplayListener onAdShowed.");
        }

        @Override
        public void onAdClick() {
            // Call this method when an ad is clicked.
            Log.i(TAG, "SplashAdDisplayListener onAdClick.");
        }
    };

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            // Called when an ad is loaded successfully.
//            showToast("Ad loaded.");
        }

        @Override
        public void onAdFailed(int errorCode) {
            // Called when an ad fails to be loaded.
//            showToast(String.format(Locale.ROOT, "Ad failed to load with error code %d.", errorCode));
        }

        @Override
        public void onAdOpened() {
            // Called when an ad is opened.
//            showToast(String.format("Ad opened "));
        }

        @Override
        public void onAdClicked() {
            // Called when a user taps an ad.
//            showToast("Ad clicked");
        }

        @Override
        public void onAdLeave() {
            // Called when a user has left the app.
//            showToast("Ad Leave");
        }

        @Override
        public void onAdClosed() {
            // Called when an ad is closed.
//            showToast("Ad closed");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HwAds.init(this);

        HiAnalyticsTools.enableLog();
        instance = HiAnalytics.getInstance(this);

        loadManifest();
        setDisplay(this);
        setOrientation(this);
        setName(this);
        setContentView(R.layout.activity_main);
        myWebView = (WebView) this.findViewById(R.id.webView);
        setWebView(myWebView);
        getToken();

        //bannerEnable
        //splashEnable
        if(bannerEnable){
            loadDefaultBannerAd();
        }
        if(splashEnable){
            loadSplashAd();
        }

//        testEvent();

    }

    private void setDisplay(Activity activity) {
        if (this.manifestObject.optString("display").equals("fullscreen")) {
            activity.setTheme(R.style.FullscreenTheme);
        } else {
            activity.setTheme(R.style.AppTheme);
        }
    }

    private void setName(Activity activity) {
        String name = this.manifestObject.optString("name");
        if (!name.isEmpty()) {
            activity.setTitle(name);
        }
    }

    private static final String ANY = "any";
    private static final String NATURAL = "natural";
    private static final String PORTRAIT_PRIMARY = "portrait-primary";
    private static final String PORTRAIT_SECONDARY = "portrait-secondary";
    private static final String LANDSCAPE_PRIMARY = "landscape-primary";
    private static final String LANDSCAPE_SECONDARY = "landscape-secondary";
    private static final String PORTRAIT = "portrait";
    private static final String LANDSCAPE = "landscape";

    private void setOrientation(Activity activity) {
        String orientation = this.manifestObject.optString("orientation");
        switch (orientation) {
            case LANDSCAPE_PRIMARY:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case PORTRAIT_PRIMARY:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case LANDSCAPE:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            case PORTRAIT:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                break;
            case LANDSCAPE_SECONDARY:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case PORTRAIT_SECONDARY:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case ANY:
            case NATURAL:
            default:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView(WebView myWebView) {
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String start_url = this.manifestObject.optString("start_url");
        String scope = this.manifestObject.optString("scope");
        myWebView.setWebViewClient(new PwaWebViewClient(start_url, scope));
        myWebView.loadUrl(start_url);
    }

    private static final String DEFAULT_MANIFEST_FILE = "manifest.json";
    private JSONObject manifestObject;

    private void loadManifest() {
        if (this.assetExists((DEFAULT_MANIFEST_FILE))) {
            try {
                this.manifestObject = this.loadLocalManifest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean assetExists(String asset) {
        final AssetManager assetManager = this.getResources().getAssets();
        try {
            return Arrays.asList(assetManager.list("")).contains(asset);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private JSONObject loadLocalManifest() throws JSONException {
        try {
            InputStream inputStream = this.getResources().getAssets().open(DEFAULT_MANIFEST_FILE);
            int size = inputStream.available();
            byte[] bytes = new byte[size];
            int readBytes = inputStream.read(bytes);
            inputStream.close();
            if (readBytes > 0) {
                String jsonString = new String(bytes, "UTF-8");
                return new JSONObject(jsonString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void loadDefaultBannerAd() {
        // Obtain BannerView based on the configuration in layout/activity_main.xml.
        //enableTopBanner
        if(enableTopBanner){
            defaultBannerView = findViewById(R.id.top_banner);
        }else{
            defaultBannerView = findViewById(R.id.bot_banner);
        }

        defaultBannerView.setAdListener(adListener);
        defaultBannerView.setBannerRefresh(REFRESH_TIME);

        AdParam adParam = new AdParam.Builder().build();
        defaultBannerView.loadAd(adParam);
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadSplashAd() {
        Log.i(TAG, "Start to load ad");

        AdParam adParam = new AdParam.Builder().build();
        splashView = findViewById(R.id.splash_ad_view);
        splashView.setAdDisplayListener(adDisplayListener);

        // Set a default app launch image.
        splashView.setSloganResId(R.drawable.default_slogan);

        // Set a logo image.
        splashView.setLogoResId(R.mipmap.ic_launcher);
        // Set logo description.
        splashView.setMediaNameResId(R.string.media_name);
        // Set the audio focus type for a video splash ad.
        splashView.setAudioFocusType(AudioFocusType.NOT_GAIN_AUDIO_FOCUS_WHEN_MUTE);

        splashView.load(getString(R.string.ad_id_splash), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, adParam, splashAdLoadListener);
        Log.i(TAG, "End to load ad");

        // Remove the timeout message from the message queue.
        timeoutHandler.removeMessages(MSG_AD_TIMEOUT);
        // Send a delay message to ensure that the app home screen can be displayed when the ad display times out.
        timeoutHandler.sendEmptyMessageDelayed(MSG_AD_TIMEOUT, AD_TIMEOUT);
    }

    private void jump() {
        Log.i(TAG, "jump hasPaused: " + hasPaused);
        if (!hasPaused) {
            hasPaused = true;
            Log.i(TAG, "jump into application");

            splashView.destroyView();
            splashView.setVisibility(View.GONE);
        }
    }

    /**
     * Set this parameter to true when exiting the app to ensure that the app home screen is not displayed.
     */
    @Override
    protected void onStop() {
        Log.i(TAG, "SplashActivity onStop.");
        // Remove the timeout message from the message queue.
        timeoutHandler.removeMessages(MSG_AD_TIMEOUT);
        hasPaused = true;
        super.onStop();
    }

    /**
     * Call this method when returning to the splash ad screen from another screen to access the app home screen.
     */
    @Override
    protected void onRestart() {
        Log.i(TAG, "SplashActivity onRestart.");
        super.onRestart();
        hasPaused = false;
        jump();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "SplashActivity onDestroy.");
        super.onDestroy();
        if (splashView != null) {
            splashView.destroyView();
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "SplashActivity onPause.");
        super.onPause();
        if (splashView != null) {
            splashView.pauseView();
        }
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "SplashActivity onResume.");
        super.onResume();
        if (splashView != null) {
            splashView.resumeView();
        }
    }

//    private void testEvent() {
//        // Report score by using SUBMITSCORE Event
//        // Initiate Parameters
//        Log.d(TAG, "testEvent");
//        Bundle bundle = new Bundle();
//        bundle.putString("TEST_BUNDLE_KEY", "TEST_BUNDLE_VALUE");
//
//        // Report a preddefined Event
//        instance.onEvent("TEST_ONEVENT", bundle);
//
//        Bundle bundle2 = new Bundle();
//        bundle2.putLong(SCORE, 123456789);
//
//        // Report a preddefined Event
//        instance.onEvent(SUBMITSCORE, bundle2);
//    }

    private void getToken() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
//                    String appId = "103400103";
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                    Log.i(TAG, "get token:" + token);
//                    if(!TextUtils.isEmpty(token)) {
//                        sendRegTokenToServer(token);
//                    }
                } catch (ApiException e) {
                    Log.e(TAG, "get token failed, " + e);
                }
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
