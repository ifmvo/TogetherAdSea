# TogetherAdSea 

海外版的 [TogetherAd](http://a.i33.tv:3000/rocky/TogetherAd-Pro) 目前包含 Facebook、Google 的广告

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
    implementation 'com.github.ifmvo:TogetherAdSea:1.0.3'
}
```


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
Application 中初始化广告
```
Map<String, String> googleIdMap = new HashMap<>();
googleIdMap.put(TogetherAdConst.AD_SPLASH, "ca-app-pub-3940256099942544/2247696110");
googleIdMap.put(TogetherAdConst.AD_BANNER, "ca-app-pub-3940256099942544/6300978111");
googleIdMap.put(TogetherAdConst.AD_FLOW, "ca-app-pub-3940256099942544/2247696110");
TogetherAdSea.INSTANCE.initGoogleAd(this, "ca-app-pub-3940256099942544~3347511713", googleIdMap);

Map<String, String> facebookIdMap = new HashMap<>();
facebookIdMap.put(TogetherAdConst.AD_SPLASH, "290080388575176_290083468574868");
facebookIdMap.put(TogetherAdConst.AD_BANNER, "290080388575176_298008991115649");
facebookIdMap.put(TogetherAdConst.AD_FLOW, "290080388575176_298449734404908");
TogetherAdSea.INSTANCE.initFacebookAd(this, facebookIdMap);
```

### 第 4 步
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


