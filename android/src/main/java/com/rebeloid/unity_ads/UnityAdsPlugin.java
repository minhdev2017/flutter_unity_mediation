package com.rebeloid.unity_ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.unity3d.ads.UnityAds;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import static com.rebeloid.unity_ads.UnityAdsConstants.PLACEMENT_ID_PARAMETER;
import static com.rebeloid.unity_ads.UnityAdsConstants.SERVER_ID_PARAMETER;

import com.rebeloid.unity_ads.banner.BannerAdFactory;
import com.unity3d.ads.metadata.PlayerMetaData;
import com.unity3d.mediation.IInitializationListener;
import com.unity3d.mediation.IInterstitialAdLoadListener;
import com.unity3d.mediation.IInterstitialAdShowListener;
import com.unity3d.mediation.IReward;
import com.unity3d.mediation.IRewardedAdLoadListener;
import com.unity3d.mediation.IRewardedAdShowListener;
import com.unity3d.mediation.InitializationConfiguration;
import com.unity3d.mediation.InterstitialAd;
import com.unity3d.mediation.RewardedAd;
import com.unity3d.mediation.UnityMediation;
import com.unity3d.mediation.errors.LoadError;
import com.unity3d.mediation.errors.SdkInitializationError;
import com.unity3d.mediation.errors.ShowError;

/**
 * Unity Ads Plugin
 */
public class UnityAdsPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodChannel channel;
    private Context context;
    private Activity activity;

    private BannerAdFactory bannerAdFactory;
    String rewardId;
    String fullId;
    boolean hasReward = false;
    boolean hasFull = false;
    RewardedAd rewardedAd;
    InterstitialAd interstitialAd;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), UnityAdsConstants.MAIN_CHANNEL);
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();

        flutterPluginBinding.getPlatformViewRegistry()
                .registerViewFactory(UnityAdsConstants.BANNER_AD_CHANNEL, bannerAdFactory);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

        if (call.method.equals(UnityAdsConstants.INIT_METHOD)) {
            result.success(initialize((Map<?, ?>) call.arguments));
            return;
        }

        if (call.method.equals(UnityAdsConstants.SHOW_FULL_METHOD)) {
            result.success(showFullAds());
            return;
        }

        if (call.method.equals(UnityAdsConstants.SHOW_REWARD_METHOD)) {
            result.success(showReward());
            return;
        }

        result.notImplemented();
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        bannerAdFactory.setActivity(activity);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }

    private boolean initialize(Map<?, ?> args) {
        String gameId = (String) args.get(UnityAdsConstants.GAME_ID_PARAMETER);
        rewardId = (String) args.get(UnityAdsConstants.REWARD_ID_PARAMETER);
        fullId = (String) args.get(UnityAdsConstants.FULL_ID_PARAMETER);

        Boolean testMode = (Boolean) args.get(UnityAdsConstants.TEST_MODE_PARAMETER);
        if (testMode == null) {
            testMode = false;
        }

        //UnityAds.initialize(context, gameId, testMode || firebaseTestMode);
        InitializationConfiguration configuration = InitializationConfiguration.builder()
                .setGameId(gameId)
                .setInitializationListener(new IInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                        // Unity Mediation is initialized. Try loading an ad.
                        System.out.println("Unity Mediation is successfully initialized. " + rewardId + " : " + fullId);
                        loadReward();
                        loadFullAds();
                    }

                    @Override
                    public void onInitializationFailed(SdkInitializationError errorCode, String msg) {
                        // Unity Mediation failed to initialize. Printing failure reason...
                        System.out.println("Unity Mediation Failed to Initialize : " + msg);
                    }
                }).build();

        UnityMediation.initialize(configuration);

        return true;
    }

    private boolean isInFirebaseTestLab() {
        String testLabSetting = Settings.System.getString(context.getContentResolver(), "firebase.test.lab");
        return "true".equalsIgnoreCase(testLabSetting);
    }


    private void loadReward(){
        rewardedAd = new RewardedAd(activity, rewardId);
        final IRewardedAdLoadListener loadListener = new IRewardedAdLoadListener() {
            @Override
            public void onRewardedLoaded(RewardedAd ad) {
                hasReward = true;
            }

            @Override
            public void onRewardedFailedLoad(RewardedAd ad, LoadError error, String msg) {
                hasReward = false;
            }
        };
        rewardedAd.load(loadListener);
    }

    private void loadFullAds(){
        interstitialAd = new InterstitialAd(activity, fullId);
        final IInterstitialAdLoadListener loadListener = new IInterstitialAdLoadListener() {
            @Override
            public void onInterstitialLoaded(InterstitialAd ad) {
                hasFull = true;
            }

            @Override
            public void onInterstitialFailedLoad(InterstitialAd ad, LoadError error, String msg) {
                hasFull = false;
            }
        };

// Load an ad:
        interstitialAd.load(loadListener);
    }

    private boolean showReward(){
        if(hasReward && rewardedAd != null){
            final IRewardedAdShowListener showListener = new IRewardedAdShowListener() {
                @Override
                public void onRewardedShowed(RewardedAd ad) {
                    // The ad has started to show.
                }
                @Override
                public void onRewardedClicked(RewardedAd ad) {
                    // The user has clicked on the ad.
                }
                @Override
                public void onRewardedClosed(RewardedAd ad) {
                    channel.invokeMethod("onAdsClosed", null);
                    hasReward = false;
                    loadReward();
                }
                @Override
                public void onRewardedFailedShow(RewardedAd ad, ShowError error, String msg) {
                    // An error occurred during the ad playback.
                }
                @Override
                public void onUserRewarded(RewardedAd ad, IReward reward) {
                    channel.invokeMethod("onAdsRewarded", reward.getAmount());
                }
            };
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    rewardedAd.show(showListener);
                }
            });

            return true;
        }else{
            loadReward();
            return false;
        }
    }

    private boolean showFullAds(){
        if(hasFull && interstitialAd != null){
            final IInterstitialAdShowListener showListener = new IInterstitialAdShowListener() {
                @Override
                public void onInterstitialShowed(InterstitialAd interstitialAd) {
                    // The ad has started to show.
                }
                @Override
                public void onInterstitialClicked(InterstitialAd interstitialAd) {
                    // The user has clicked on the ad.
                }
                @Override
                public void onInterstitialClosed(InterstitialAd interstitialAd) {
                    channel.invokeMethod("onAdsClosed", null);
                    hasFull = false;
                    loadFullAds();
                }
                @Override
                public void onInterstitialFailedShow(InterstitialAd interstitialAd, ShowError error, String msg) {
                    // An error occurred during the ad playback.
                }
            };
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    interstitialAd.show(showListener);
                }
            });

            return true;
        }else{
            loadFullAds();
            return false;
        }
    }

}
