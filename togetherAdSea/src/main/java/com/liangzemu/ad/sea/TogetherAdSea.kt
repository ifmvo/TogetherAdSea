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

    lateinit var mContext: Application
        private set

    /**
     * 位ID的Map
     */
    var idMapGoogle = mutableMapOf<String, String?>()
        private set
    var idMapFacebook = mutableMapOf<String, String?>()
        private set

    var idListGoogleMap = mutableMapOf<String, MutableList<String>>()

    var idListFacebookMap = mutableMapOf<String, MutableList<String>>()

    /**
     * 超时时间
     */
//    var timeOutMillis: Long = 5000
//        private set

    /**
     * 初始化广告
     */
    fun initAdGoogle(@NonNull context: Application, @NonNull googleAdAppId: String, googleIdMap: MutableMap<String, MutableList<String>>) {
        mContext = context
        idListGoogleMap = googleIdMap
        MobileAds.initialize(context, googleAdAppId)
    }

    fun initAdFacebook(@NonNull context: Application, @NonNull facebookIdMap: MutableMap<String, MutableList<String>>) {
        mContext = context
        idListFacebookMap = facebookIdMap
        // Example for setting the SDK to crash when in debug mode
        AudienceNetworkAds.isInAdsProcess(context)
        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE)
        AudienceNetworkAds.initialize(context)
    }

    /**
     * 初始化广告
     */
    fun initGoogleAd(@NonNull context: Application, @NonNull googleAdAppId: String, googleIdMap: MutableMap<String, String?>) {
        idMapGoogle = googleIdMap
//        MobileAds.initialize(context, googleAdAppId)
    }

    fun initFacebookAd(@NonNull context: Application, @NonNull facebookIdMap: MutableMap<String, String?>) {
        idMapFacebook = facebookIdMap
        // Example for setting the SDK to crash when in debug mode
//        AudienceNetworkAds.isInAdsProcess(context)
//        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE)
//        AudienceNetworkAds.initialize(context)
    }

//    fun setAdTimeOutMillis(millis: Long) {
//        timeOutMillis = millis
//    }

}