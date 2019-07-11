package com.liangzemu.ad.sea

import android.app.Application
import androidx.annotation.NonNull
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-04-22.
 */
object TogetherAdSea {

    var idListGoogleMap = mapOf<String, List<String>>()

    var idListFacebookMap = mapOf<String, List<String>>()
    /**
     * google测试id
     */
    var testDeviceID: String? = null
        private set
    /**
     * 当前正在加载中的广告
     */
    val loadingAdTask = HashMap<String, Int>() //HashMap<adConstStr,lastLevel>()
    /**
     * 保存application
     */
    lateinit var context: Application
    /**
     * 超时时间
     */
    var timeoutMillsecond: Long = 10000
    /**
     * 缓存广告
     */
    internal val adCacheMap = HashMap<String, Any>()//HashMap<adConstStr,AD>()
    /**
     * 是否是中介模式
     * 中介模式，就会忽略 configStr，就是不理会广告的比例，写死 google_admob:1,other:0
     * 在项目里面任意时刻调用，实时生效
     */
    var isMediationMode = false

    /**
     * 初始化 Google 广告
     */
    fun initAdGoogle(
        @NonNull context: Application, @NonNull googleAdAppId: String, googleIdMap: Map<String, List<String>>,
        testDeviceID: String? = null
    ) {
        this.context = context
        idListGoogleMap = googleIdMap
        MobileAds.initialize(context, googleAdAppId)
        this.testDeviceID = testDeviceID
    }

    /**
     * 初始化 Facebook 广告
     */
    fun initAdFacebook(
        @NonNull context: Application, @NonNull facebookIdMap: Map<String, List<String>>, testMode: Boolean = false
    ) {
        this.context = context
        idListFacebookMap = facebookIdMap
        // Example for setting the SDK to crash when in debug mode
        AudienceNetworkAds.isInAdsProcess(context)
        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE)
        AudienceNetworkAds.initialize(context)
        AdSettings.setTestMode(testMode)
    }
}