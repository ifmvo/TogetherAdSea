![TogetherAdSea](app/src/main/res/mipmap-xxhdpi/ic_logo.png)

# TogetherAdSea 

海外版的 [TogetherAd](http://a.i33.tv:3000/rocky/TogetherAd-Pro) 目前包含 Facebook、Google 的广告
项目全都使用 Kotlin 编写

### 第 0 步
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
```
dependencies {
    implementation 'com.github.ifmvo:TogetherAdSea:最新版本'
}
```
可在 [这里](https://jitpack.io/#ifmvo/TogetherAdSea) 查看最新版本，也可在标签列表中查看

### 第 1 步
注册 Google 广告的 APP_ID：将 ADMOB_APP_ID 替换为自己的 ID
缺少这一步骤会导致崩溃，报错信息：The Google Mobile Ads SDK was initialized incorrectly."
```
<manifest>
    <application>
        <!-- Sample AdMob App ID: ca-app-pub-3940256099942544~3347511713 -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ADMOB_APP_ID"/>
    </application>
</manifest>
```

### 第 2 步
新建一个文件，配置所有的广告位
```
class TogetherAdConst {
    companion object {
        //开屏
        const val AD_SPLASH = "ad_splash"

        //横幅
        const val AD_BANNER = "ad_banner"

        //原生
        const val AD_FLOW = "ad_flow"

    }
}
```

### 第 3 步
新建一个文件，这个文件中的数据实际项目中是由接口下发。
区分大小写
```
object Config {

    fun splashAdConfig() = "google_admob:1,facebook:0"

    fun bannerAdConfig() = "google_admob:1,facebook:0"

    fun flowAdConfig() = "GOOGLE_ADMOB:1,facebook:0"

}
```
### 第 4 步
Application 中初始化广告，初始化所有广告位对应广告的 ``位ID``
```
Map<String, List<String>> googleIdListMap = new HashMap<>();

//插页广告
List<String> googleInterList = new ArrayList<>();
googleInterList.add("ca-app-pub-6810306854458967/3276250160");
googleInterList.add("ca-app-pub-6810306854458967/5710841812");
googleIdListMap.put(TogetherAdConst.AD_INTER, googleInterList);

//播放暂停
List<String> googlePauseList = new ArrayList<>();
googlePauseList.add("ca-app-pub-3940256099942544/6300978111");
googlePauseList.add("ca-app-pub-3940256099942544/6300978111");
googleIdListMap.put(TogetherAdConst.AD_PAUSE, googlePauseList);

//激励
List<String> googleRewardList = new ArrayList<>();
googleRewardList.add("ca-app-pub-3940256099942544/5224354917");
googleRewardList.add("ca-app-pub-3940256099942544/5224354917");
googleIdListMap.put(TogetherAdConst.AD_REWARD, googleRewardList);

//
List<String> googleFlowList = new ArrayList<>();
googleFlowList.add("ca-app-pub-3940256099942544/2247696110");
googleFlowList.add("ca-app-pub-3940256099942544/5224354917");
googleFlowList.add("ca-app-pub-3940256099942544/2247696110");
googleIdListMap.put(TogetherAdConst.AD_FLOW, googleFlowList);

//横幅Banner
List<String> googleBannerList = new ArrayList<>();
googleBannerList.add("ca-app-pub-3940256099942544/6300978111");
googleIdListMap.put(TogetherAdConst.AD_BANNER, googleBannerList);
/*
 * "AD84300E9B7D7E2DC6479CFB2F31E5C7"
 */
TogetherAdSea.INSTANCE.initAdGoogle(this, "ca-app-pub-6810306854458967~4172564328", googleIdListMap, "AD84300E9B7D7E2DC6479CFB2F31E5C7");



Map<String, String> googleIdMap = new HashMap<>();
googleIdMap.put(TogetherAdConst.AD_SPLASH, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
googleIdMap.put(TogetherAdConst.AD_BANNER, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
googleIdMap.put(TogetherAdConst.AD_FLOW, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
TogetherAdSea.INSTANCE.initGoogleAd(this, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", googleIdMap);

Map<String, String> facebookIdMap = new HashMap<>();
facebookIdMap.put(TogetherAdConst.AD_SPLASH, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
facebookIdMap.put(TogetherAdConst.AD_BANNER, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
facebookIdMap.put(TogetherAdConst.AD_FLOW, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
TogetherAdSea.INSTANCE.initFacebookAd(this, facebookIdMap);
```

### 第 5 步
请求开屏广告
```
TogetherAdSeaSplash.showAdFull(this, Config.splashAdConfig(), TogetherAdConst.AD_SPLASH, mFlAdContainer,
    object : TogetherAdSeaSplash.AdListenerSplash {
        override fun onStartRequest(channel: String) {
        }
    
        override fun onAdClick(channel: String) {
        }
    
        override fun onAdFailed(failedMsg: String?) {
            actionDetail(1000)
        }
    
        override fun onAdDismissed() {
            actionDetail(0)
        }
    
        override fun onAdPrepared(channel: String) {
        }
})
```

原生广告（ 是用于列表中展示的广告 ）
```
TogetherAdSeaFlow.showAdFlow(this, Config.flowAdConfig(), TogetherAdConst.AD_FLOW,
    object : TogetherAdSeaFlow.AdListenerFlow {
        override fun onStartRequest(channel: String) {
        }

        override fun onAdClick(channel: String) {
        }

        override fun onAdFailed(failedMsg: String?) {
        }

        override fun onAdPrepared(channel: String, ad: Any) {
            when (ad) {
                is Ad -> {
                    Log.e("ifmvo", "TogetherAdSeaFlow:facebook")
                }
                is UnifiedNativeAd -> {
                    Log.e("ifmvo", "TogetherAdSeaFlow:google")
                }
            }
        }
    })
```

横幅广告 Banner
```
TogetherAdSeaBanner.showAdBanner(this, Config.bannerAdConfig(), TogetherAdConst.AD_BANNER, mFlAdBannerContainer, 
    object : TogetherAdSeaBanner.AdListenerBanner {
        override fun onStartRequest(channel: String) {
        }

        override fun onAdClick(channel: String) {
        }

        override fun onAdFailed(failedMsg: String?) {
        }

        override fun onAdPrepared(channel: String) {
        }
    })
```

## 查看日志
只需要在 Logcat 中过滤 ``TogetherAdSeaInfo``

## Google Admob 和 Facebook 总结
### Google
```
public static final int ERROR_CODE_INTERNAL_ERROR
Something happened internally; for instance, an invalid response was received from the ad server.
Constant Value: 0

public static final int ERROR_CODE_INVALID_REQUEST
The ad request was invalid; for instance, the ad unit ID was incorrect.
Constant Value: 1

public static final int ERROR_CODE_NETWORK_ERROR
The ad request was unsuccessful due to network connectivity.
Constant Value: 2

public static final int ERROR_CODE_NO_FILL
The ad request was successful, but no ad was returned due to lack of ad inventory.
Constant Value: 3
```

### Facebook

1. 可以缓存原生广告供稍后展示吗？
可以，广告元数据可以缓存，并且可以重复利用最多 1 小时。如果您计划在 3 小时之后使用元数据，请通过调用来加载新广告。1 小时之后，如果用户仍未点击广告，则必须请求新广告。

2. 原生广告对图片尺寸和文本长度有何限制？
标题： 最多 30 个字符
封面图片： 1200x627 像素
图标： 128 像素，正方形
社交元素： 30 个字符
按钮（行动号召）标题：少于 20 个字符
正文文本： 90 个字符
评分量表： 5
最低/最高评分： 1 分/5 分