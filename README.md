# PWA Builder HMS Template
This Android project is the base template for the Huawei AppGallery build option on https://pwabuilder-ag.com/.

While PWA Builder can automatically generate an APK without the Android Studio IDE, it is very simple for a developer with Android experience to modify the template project directly. This option might be better if there are issues using PWA Builder to generate an APK or if a developer wishes to customize the PWA solution to meet their needs.



## 

## Enabling HMS Kits

### Splash Ads
1. MainActivity.java (line 82) set splashEnable = true
2. strings.xml (line 51) comment line out
3. strings.xml (line 52) uncomment line out

### Banner Ads at top of screen
1. MainActivity.java (line 83) set bannerEnable = true
2. MainActivity.java (line 84) set enableTopBanner = true
3. strings.xml (line 12) comment line out
4. strings.xml (line 13) uncomment line out

### Banner Ads at bottom of screen
1. MainActivity.java (line 83) set bannerEnable = true
2. strings.xml (line 12) comment line out
3. strings.xml (line 13) uncomment line out

### Analytics/Push
Make sure that [Analytics](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-config-agc-0000001050163815)/[Push](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-config-agc-0000001050170137) are properly configured on AGC first.
1. AndroidManifest.xml (line 26) replace 'replaceAppId' with App ID from AGC.
2. AndroidManifest.xml (line 30) replace 'replaceCpId' with CP ID from AGC.


