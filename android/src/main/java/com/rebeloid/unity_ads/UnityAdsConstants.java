package com.rebeloid.unity_ads;

public interface UnityAdsConstants {

    String MAIN_CHANNEL = "unity.ads";

    String BANNER_AD_CHANNEL = MAIN_CHANNEL + "/bannerAd";
    String VIDEO_AD_CHANNEL = MAIN_CHANNEL + "/videoAd";

    String GAME_ID_PARAMETER = "gameId";
    String REWARD_ID_PARAMETER = "rewardId";
    String FULL_ID_PARAMETER = "fullId";
    String TEST_MODE_PARAMETER = "testMode";
    String FIREBASE_TEST_LAB_MODE_PARAMETER = "firebaseTestLabMode";

    String PLACEMENT_ID_PARAMETER = "placementId";
    String SERVER_ID_PARAMETER = "serverId";
    String HEIGHT_PARAMETER = "height";
    String WIDTH_PARAMETER = "width";

    String INIT_METHOD = "init";
    String SHOW_FULL_METHOD = "showFull";
    String SHOW_REWARD_METHOD = "showReward";

    String READY_METHOD = "ready";
    String START_METHOD = "start";
    String COMPLETE_METHOD = "complete";
    String SKIPPED_METHOD = "skipped";
    String ERROR_METHOD = "error";

    String BANNER_ERROR_METHOD = "banner_error";
    String BANNER_LOADED_METHOD = "banner_loaded";
    String BANNER_CLICKED_METHOD = "banner_clicked";
}
