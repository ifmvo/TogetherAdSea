![TogetherAdSea](app/src/main/res/mipmap-xxhdpi/ic_logo.png)

# 本项目停止维护，建议使用[Google中介](https://developers.google.com/admob/android/mediate?hl=zh_cn)代替，各方面比较后者优势明显

# 让广告收益最大化

TogetherAdSea 是将``多个海外广告提供商``的 SDK 聚合在一起进行``封装``、``扩展``的 Android 库。 

- 目前支持 Google AdMob、 Facebook 广告
- 可配置比例
- 支持多个档位
- 多个档位可以选择 横向切换 和 竖向切换
- 自动缓存功能
- 广告超时处理
- 提供海外广告踩坑指南

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
        //横幅
        const val banner = "banner"

        //原生
        const val native = "native"

        //插屏
        const val interstitial = "interstitial"

        //激励
        const val reward = "reward"

        //原生横幅  仅facebook有
        const val flow_banner = "flow_banner"
    }
}
```

### 第 3 步
新建一个文件，这个文件中的数据实际项目中是由接口下发。
区分大小写
```
/*
 * (●ﾟωﾟ●) 区分大小写
 *
 */
object Config {

    fun bannerAdConfig() = "google_admob:1,facebook:0"

    fun nativeAdConfig() = "google_admob:1,facebook:0"

    fun interstitialAdConfig() = "google_admob:1,facebook:0"

    fun rewardAdConfig() = "google_admob:1,facebook:0"

    fun flowBannerConfig() = "google_admob:1,facebook:0"
}
```
### 第 4 步
Application 中初始化广告，初始化所有广告位对应广告的 ``位ID``。多个档位从前到后 第一档、第二档、第三档......
```
/**
 * Facebook
 * 获取某个广告位 所有档位的广告ID,
 * adConst：广告位
 */
fun getFbAdIdList(): Map<String, List<String>> {
    return mapOf(
        TogetherAdConst.banner to mutableListOf(
            "308281803417604_308289240083527", "308281803417604_308289343416850"
        ),
        TogetherAdConst.native to mutableListOf(
            "308281803417604_308288026750315", "308281803417604_308288183416966"
        ),
        ......
    )
}

/**
 * Google
 * 获取某个广告位 所有档位的 广告ID
 * adConst：广告位
 */
fun getGAdIdList(): Map<String, List<String>> {
    return mapOf(
        TogetherAdConst.banner to mutableListOf(
            "ca-app-pub-3940256099942544/6300978111", "ca-app-pub-3940256099942544/6300978111"
        ),
        TogetherAdConst.native to mutableListOf(
            "ca-app-pub-3940256099942544/2247696110", "ca-app-pub-3940256099942544/2247696110"
        ),
        ......
    )
}

/**
 * 谷歌广告应用ID
 */
fun getGoogleAdId(): String {
    return "ca-app-pub-3940256099942544~3347511713"
}

/**
 * 初始化 Google AdMob 广告
 * testDeviceID： 测试设备ID 
 * Logcat 中查看：Ads: Use AdRequest.Builder.addTestDevice("69758C95501DD877201C4F23EEC6E3FD") to get test ads on this device.
 */
TogetherAdSea.initAdGoogle(this, AdIdFactory.getGoogleAdId(), AdIdFactory.getGAdIdList())
TogetherAdSea.initAdGoogle(this, AdIdFactory.getGoogleAdId(), AdIdFactory.getGAdIdList(), "AD84300E9B7D7E2DC6479CFB2F31E5C7")

/**
 * 初始化 Facebook 广告
 * testMode: 是否开启测试模式
 */
TogetherAdSea.initAdFacebook(this, AdIdFactory.getFbAdIdList())
TogetherAdSea.initAdFacebook(this, AdIdFactory.getFbAdIdList(), BuildConfig.DEBUG)

```

### 第 5 步
请求广告参照 Demo  

|类名|用途|
|:-----------:| :-------------:|
| BannerHelper | 横幅广告  |
| FlowHelper | 原生广告 |
| FlowBannerHelper | 原生横幅广告（ 只有Facebook ）  |
| InterstitialHelper | 插页广告  |
| RewardHelper | 激励广告  |

## 查看日志
只需要在 Logcat 中过滤 ``TogetherAdSeaInfo``


## 有疑问？VX 联系我!
![WeChat](img/Wechat.jpeg)


## 版本日志
v1.3.5
添加了 RewardTempHelper

v1.3.4
添加了中介模式的开关 TogetherAdSea.isMediationMode = true/false

v1.3.1
实现目前需求的一个稳定版


 
