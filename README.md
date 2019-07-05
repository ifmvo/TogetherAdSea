![TogetherAdSea](app/src/main/res/mipmap-xxhdpi/ic_logo.png)

# TogetherAdSea 让广告收益最大化

TogetherAdSea 是将``多个海外广告提供商``的 SDK 聚合在一起进行``封装``、``扩展``的 Android 库。  

1. 可配置比例
2. 支持多个档位
3. 多个档位可以选择 横向切换 和 竖向切换
4. 提供海外广告踩坑指南

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
```

### 第 5 步
请求广告参照 Demo
|:-----------:| :-------------:|
| BannerHelper | 横幅广告  |
| FlowHelper | 原生广告 |
| FlowBannerHelper | 原生横幅广告（ 只有Facebook ）  |
| InterstitialHelper | 插页广告  |
| RewardHelper | 激励广告  |

## 查看日志
只需要在 Logcat 中过滤 ``TogetherAdSeaInfo``