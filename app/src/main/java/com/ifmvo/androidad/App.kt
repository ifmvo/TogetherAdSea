package com.ifmvo.androidad

import android.app.Application
import com.ifmvo.androidad.ad.AdIdFactory
import com.liangzemu.ad.sea.TogetherAdSea

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-07-04.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * 初始化 Google AdMob 广告
         * testDeviceID： 测试设备ID
         * Logcat 中查看：Ads: Use AdRequest.Builder.addTestDevice("69758C95501DD877201C4F23EEC6E3FD") to get test ads on this device.
         */
        val testDeviceId = if (BuildConfig.DEBUG) "69758C95501DD877201C4F23EEC6E3FD" else null
        TogetherAdSea.initAdGoogle(this, AdIdFactory.getGoogleAdId(), AdIdFactory.getGAdIdList(), testDeviceId)

        /**
         * 初始化 Facebook 广告
         * testMode: 是否开启测试模式
         */
//        TogetherAdSea.initAdFacebook(this, AdIdFactory.getFbAdIdList(), BuildConfig.DEBUG)
//        TogetherAdSea.isMediationMode = true
    }

}