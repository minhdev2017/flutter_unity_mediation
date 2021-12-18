library unity_ads;

import 'package:flutter/services.dart';

import 'src/constants.dart';

export 'ad/unity_banner_ad.dart';

/// Unity Ads plugin for Flutter applications.
class UnityAds {
  static const MethodChannel _channel = MethodChannel(mainChannel);

  /// Initialize Unity Ads.
  ///
  /// * [gameId] - identifier from Project Settings in Unity Dashboard.
  /// * [testMode] - if true, then test ads are shown.
  /// * [firebaseTestLabMode] - mode of showing ads in Firebase Test Lab.
  static Future<bool?> init({
    required String gameId,
    required String rewardId,
    required String fullId,
    bool testMode = false,
    FirebaseTestLabMode firebaseTestLabMode = FirebaseTestLabMode.disableAds,
    Function(UnityAdState, dynamic)? listener,
  }) async {
    Map<String, dynamic> arguments = {
      gameIdParameter: gameId,
      testModeParameter: testMode,
      rewardIdParameter: rewardId,
      fullIdParameter: fullId,
    };
    try {
      if (listener != null) {
        _channel.setMethodCallHandler((call) => _methodCall(call, listener));
      }
      final result = await _channel.invokeMethod(initMethod, arguments);
      return result;
    } on PlatformException {
      return false;
    }
  }


  static final Map<String, MethodChannel> _channels = {};

  /// Show video ad, if ready.
  ///
  /// [placementId] placement identifier, as defined in Unity Ads admin tools
  /// If true, placement are shown
  static Future<bool?> showRewardAd() async {
    try {
      final result = await _channel.invokeMethod(showRewardMethod);
      return result;
    } on PlatformException {
      return false;
    }
  }

  static Future<bool?> showFullAd() async {
    try {
      final result = await _channel.invokeMethod(showFullMethod);
      return result;
    } on PlatformException {
      return false;
    }
  }

  static Future<dynamic> _methodCall(
      MethodCall call, Function(UnityAdState, dynamic) listener) {
    switch (call.method) {
      case readyMethod:
        listener(UnityAdState.ready, call.arguments);
        break;
      case startMethod:
        listener(UnityAdState.started, call.arguments);
        break;
      case rewardMethod:
        listener(UnityAdState.reward, call.arguments);
        break;
      case closeMethod:
        listener(UnityAdState.close, call.arguments);
        break;
      case errorMethod:
        listener(UnityAdState.error, call.arguments);
        break;
    }
    return Future.value(true);
  }
}

enum UnityAdState {
  /// Ad loaded successfully.
  ready,

  /// Some error occurred.
  error,

  /// Video ad started.
  started,

  /// Video played till the end. Use it to reward the user.
  reward,

  /// Video ad closed.
  close,
}

enum FirebaseTestLabMode {
  /// Ads are not displayed in the Firebase Test Lab
  disableAds,

  /// Ads are displayed in test mode.
  showAdsInTestMode,

  /// Real ads are displayed, if [testMode] is false.
  showAds,
}
