# Unity Ads Plugin

[![Pub](https://img.shields.io/pub/v/unity_ads_plugin.svg)](https://pub.dev/packages/unity_ads_plugin)
[![License](https://img.shields.io/github/license/pavzay/flutter_unity_ads)](https://github.com/pavzay/flutter_unity_ads/blob/master/LICENSE)
[![Pub likes](https://badgen.net/pub/likes/unity_ads_plugin)](https://pub.dev/packages/unity_ads_plugin/score)
[![Pub popularity](https://badgen.net/pub/popularity/unity_ads_plugin)](https://pub.dev/packages/unity_ads_plugin/score)
[![Pub points](https://badgen.net/pub/points/unity_ads_plugin)](https://pub.dev/packages/unity_ads_plugin/score)
[![Flutter platform](https://badgen.net/pub/flutter-platform/unity_ads_plugin)](https://pub.dev/packages/unity_ads_plugin)
[![GitHub popularity](https://img.shields.io/github/stars/pavzay/flutter_unity_ads?logo=github&logoColor=white)](https://github.com/pavzay/flutter_unity_ads)


[![Buy Me A Coffee](https://img.shields.io/badge/Donate-Buy%20me%20a%20coffee-FFDD00?logo=buymeacoffee)](https://www.buymeacoffee.com/rebeloid)
[![PayPal](https://img.shields.io/badge/Donate-PayPal-066BB7?logo=paypal)](https://paypal.me/pavelzaichyk)

[Unity Ads](https://unity.com/solutions/unity-ads) plugin for Flutter Applications. This plugin is able to display Unity Banner Ads and Unity Video Ads.

- [Getting Started](#getting-started)
    - [1. Initialization](#1-initialization)
    - [2. Show Rewarded/Interstitial Video Ad](#2-show-rewardedinterstitial-video-ad)
    - [3. Show Banner Ad](#3-show-banner-ad)
- [Donate](#donate)

## Getting Started

### 1. Initialization:

```dart
    UnityAds.init(
          gameId: AdManager.gameId,
          rewardId: AdManager.rewardedVideoAdPlacementId,
          fullId: AdManager.interstitialVideoAdPlacementId,
          testMode: true,
          listener: (state, args) {
            print('Init Listener: $state => $args');
          },
        );
```

Set your Game id.
For testing purposes set `testMode` to `true`.

---

_Android only:_ To change ads behavior in Firebase Test Lab use `firebaseTestLabMode` parameter. Possible values:

Mode | Description 
--- | --- 
disableAds | Ads are not displayed in the Firebase Test Lab (by default)
showAdsInTestMode | Ads are displayed in test mode.
showAds | Real ads are displayed, if testMode is false.

### 2. Show Rewarded/Interstitial Video Ad:

![Rewarded Video Ad](https://github.com/pavzay/flutter_unity_ads/raw/master/example/images/rewarded.gif "Rewarded Video Ad")
![Interstitial Video Ad](https://github.com/pavzay/flutter_unity_ads/raw/master/example/images/interstitial.gif "Interstitial Video Ad")

```dart
UnityAds.showVideoAd(
  placementId: 'video_placement_id',
  listener: (state, args) {
    if (state == UnityAdState.complete) {
      print('User watched a video. User should get a reward!');
    } else if (state == UnityAdState.skipped) {
      print('User cancel video.');
    }
  },
);
```

Check if the video ad is ready:

```dart
UnityAds.isReady(placementId: 'video_placement_id');
```

Possible unity ad state:

State | Description 
--- | --- 
ready | Ad loaded successfully. 
error | Some error occurred. 
started | Video ad started. 
complete | Video played till the end. Use it to reward the user. 
skipped | Video ad closed. 

#### Server-to-server redeem callbacks

`UnityAds.showVideoAd` has `serverId` parameter.

To use server-to-server callbacks, you need to set this parameter.

Read more on [unity3d.com](https://unityads.unity3d.com/help/resources/s2s-redeem-callbacks).

### 3. Show Banner Ad:

![Banner Ad](https://github.com/pavzay/flutter_unity_ads/raw/master/example/images/banner.gif "Banner Ad")

Place `UnityBannerAd` widget in your app.

```dart
UnityBannerAd(
  placementId: "banner_placement_id",
  listener: (state, args) {
    print('Banner Listener: $state => $args');
  },
)
```

Possible unity banner ad state:

State | Description 
--- | --- 
loaded | Banner is loaded.
clicked | Banner is clicked.
error | Error during loading banner.

## Donate

If you found this plugin helpful and would like to thank me:

[![Buy Me A Coffee](https://img.shields.io/badge/Donate-Buy%20me%20a%20coffee-FFDD00?logo=buymeacoffee)](https://www.buymeacoffee.com/rebeloid)
[![PayPal](https://img.shields.io/badge/Donate-PayPal-066BB7?logo=paypal)](https://paypal.me/pavelzaichyk)
