# PWA Builder HMS Template
This Android project is the base template for the Huawei AppGallery build option on [PWA Builder AG](https://pwabuilder-ag.com/).

While PWA Builder can automatically generate an APK without the Android Studio IDE, it is very simple for a developer with Android experience to modify the template project directly. 

This option might be better if: 
- There are issues using PWA Builder to generate an APK.
- A developer wishes to customize the PWA solution to meet their needs.

## FAQ

1. What is a PWA?  <br />
PWA  = Progress Web Application. Web apps that are built with modern APIs to deliver enhanced capabilities, reliability, and a better end user experience that is closer to native mobile app. https://web.dev/what-are-pwas/
2. What is PWA Builder?  <br />
[PWABuilder.com](https://www.pwabuilder.com/) is a Microsoft open-source tool to package PWAs for mobile app stores. 
3. What is PWA Builder AG?  <br />
[PWABuilder-AG.com](https://pwabuilder-ag.com/) is an enhancement to Microsoft's tool that adds support for HMS Kits.
4. What is a PWA APK (Web APK)?  <br />

7. What is the ideal use case for PWA Builder?  <br />
8. When should PWA Builder not be used?  <br />

## Enabling HMS Kits

### Splash Ads
[Documentation](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/publisher-service-splash-0000001050066919)
1. MainActivity.java (line 82) set splashEnable = true
2. strings.xml (line 51) comment line out
3. strings.xml (line 52) uncomment line out

### Banner Ads at top of screen
[Documentation](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/publisher-service-banner-0000001050066915)
1. MainActivity.java (line 83) set bannerEnable = true
2. MainActivity.java (line 84) set enableTopBanner = true
3. strings.xml (line 12) comment line out
4. strings.xml (line 13) uncomment line out

### Banner Ads at bottom of screen
[Documentation](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/publisher-service-banner-0000001050066915)
1. MainActivity.java (line 83) set bannerEnable = true
2. strings.xml (line 12) comment line out
3. strings.xml (line 13) uncomment line out

### Analytics/Push
Make sure that  are properly configured on AGC first.
- [Analytics Config](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-config-agc-0000001050163815)
- [Push Config](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-config-agc-0000001050170137)
1. AndroidManifest.xml (line 26) replace 'replaceAppId' with App ID from AGC.
2. AndroidManifest.xml (line 30) replace 'replaceCpId' with CP ID from AGC.


