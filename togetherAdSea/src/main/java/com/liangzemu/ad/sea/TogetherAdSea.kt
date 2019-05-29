package com.liangzemu.ad.sea

import android.content.Context
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
    fun initAdGoogle(@NonNull context: Context, @NonNull googleAdAppId: String, googleIdMap: MutableMap<String, MutableList<String>>) {
        idListGoogleMap = googleIdMap
        MobileAds.initialize(context, googleAdAppId)
    }

    fun initAdFacebook(@NonNull context: Context, @NonNull facebookIdMap: MutableMap<String, MutableList<String>>) {
        idListFacebookMap = facebookIdMap
        // Example for setting the SDK to crash when in debug mode
        AudienceNetworkAds.isInAdsProcess(context)
        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE)
        AudienceNetworkAds.initialize(context)
    }

    /**
     * 初始化广告
     */
    fun initGoogleAd(@NonNull context: Context, @NonNull googleAdAppId: String, googleIdMap: MutableMap<String, String?>) {
        idMapGoogle = googleIdMap
        MobileAds.initialize(context, googleAdAppId)
    }

    fun initFacebookAd(@NonNull context: Context, @NonNull facebookIdMap: MutableMap<String, String?>) {
        idMapFacebook = facebookIdMap
        // Example for setting the SDK to crash when in debug mode
        AudienceNetworkAds.isInAdsProcess(context)
        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE)
        AudienceNetworkAds.initialize(context)
    }

//    fun setAdTimeOutMillis(millis: Long) {
//        timeOutMillis = millis
//    }

}