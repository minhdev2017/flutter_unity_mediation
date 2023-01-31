import Flutter
import UnityMediationSdk

public class SwiftUnityAdsPlugin: NSObject, FlutterPlugin, UMSInitializationDelegate, UMSRewardedAdLoadDelegate, UMSInterstitialAdLoadDelegate, UMSInterstitialAdShowDelegate, UMSRewardedAdShowDelegate {
    
    
    static var viewController : UIViewController =  UIViewController();
    var rewardID:String?;
    var fullID:String?;
    var rewardedAd: UMSRewardedAd?;
    var interstitialAd: UMSInterstitialAd?;
    var mainChannel: FlutterMethodChannel? = nil
    var hasReward:Bool = false;
    var hasFull:Bool = false;
    public static func register(with registrar: FlutterPluginRegistrar) {
        
        viewController =
            (UIApplication.shared.delegate?.window??.rootViewController)!;
        let messenger = registrar.messenger()
        
        let channel = FlutterMethodChannel(name: UnityAdsConstants.MAIN_CHANNEL, binaryMessenger: messenger)
        let instance = SwiftUnityAdsPlugin()
        instance.mainChannel = channel
        channel.setMethodCallHandler({
            (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
            let args = call.arguments as? NSDictionary
            switch call.method {
                case UnityAdsConstants.INIT_METHOD:
                result(instance.initialize(args!))
                case UnityAdsConstants.SHOW_FULL_METHOD:
                result(instance.showFullAds())
                case UnityAdsConstants.SHOW_REWARD_METHOD:
                result(instance.showReward())
                default:
                    result(FlutterMethodNotImplemented)
            }
        })
        
        
    }
    
    func initialize(_ args: NSDictionary) -> Bool {
        let gameId = args[UnityAdsConstants.GAME_ID_PARAMETER] as! String
        rewardID = args[UnityAdsConstants.REWARD_ID_PARAMETER] as? String
        fullID = args[UnityAdsConstants.FULL_ID_PARAMETER] as? String
        //let testMode = args[UnityAdsConstants.TEST_MODE_PARAMETER] as! Bool
        let initializationConfiguration = UMSInitializationConfigurationBuilder().setGameId(gameId).setInitializationDelegate(self).build()
        UMSUnityMediation.initialize(with: initializationConfiguration)
        return true
    }
    
    public func onInitializationComplete() {
        loadReward()
        loadFullAds()
    }
    
    public func onInitializationFailed(_ errorCode: UMSSdkInitializationError, message: String!) {
 
    }
    
    
    func loadReward(){
        rewardedAd = UMSRewardedAd(adUnitId: rewardID!);
        rewardedAd?.load(with: self)
        
    }
    
    func loadFullAds(){
        interstitialAd = UMSInterstitialAd(adUnitId: fullID!);
        interstitialAd?.load(with: self)
    }
    
    func showReward() -> Bool{
        if(hasReward && rewardedAd != nil){
            rewardedAd?.show(with: SwiftUnityAdsPlugin.viewController, delegate: self)
            return true
        }
        loadReward()
        return false
    }
    
    func showFullAds() -> Bool{
        if(hasFull && interstitialAd != nil){
            interstitialAd?.show(with: SwiftUnityAdsPlugin.viewController, delegate: self)
            return true
        }
        loadFullAds()
        return false
    }
    
    public func onInterstitialLoaded(_ interstitialAd: UMSInterstitialAd) {
        print("UnityAds: onInterstitialLoaded")
        hasFull = true;
    }
    
    public func onInterstitialFailedLoad(_ interstitialAd: UMSInterstitialAd, error: UMSLoadError, message: String) {
        print("UnityAds: onInterstitialFailedLoad", message)
        hasFull = false;
    }
    
    public func onInterstitialShowed(_ interstitialAd: UMSInterstitialAd) {
       
    }
    
    public func onInterstitialClicked(_ interstitialAd: UMSInterstitialAd) {
      
    }
    
    public func onInterstitialClosed(_ interstitialAd: UMSInterstitialAd) {
        hasFull = false;
        mainChannel!.invokeMethod("onAdsClosed", arguments: nil)
        loadFullAds()
    }
    
    public func onInterstitialFailedShow(_ interstitialAd: UMSInterstitialAd, error: UMSShowError, message: String) {
       
    }
    //Reward listener
    
    public func onRewardedLoaded(_ rewardedAd: UMSRewardedAd) {
        print("UnityAds: onRewardedLoaded")
        hasReward = true
    }
    
    public func onRewardedFailedLoad(_ rewardedAd: UMSRewardedAd, error: UMSLoadError, message: String) {
        print("UnityAds: " + message)
        hasReward = false;
    }
    
    
    public func onRewardedShowed(_ rewardedAd: UMSRewardedAd) {
        
    }
    
    public func onRewardedClicked(_ rewardedAd: UMSRewardedAd) {
        
    }
    
    public func onRewardedClosed(_ rewardedAd: UMSRewardedAd) {
        hasReward = false
        mainChannel!.invokeMethod("onAdsClosed", arguments: nil)
        loadReward();
    }
    
    public func onRewardedFailedShow(_ rewardedAd: UMSRewardedAd, error: UMSShowError, message: String) {
      
    }
    
    public func onUserRewarded(_ rewardedAd: UMSRewardedAd, reward: UMSReward) {
        mainChannel!.invokeMethod("onAdsRewarded", arguments: reward.getAmount())
    }
}
