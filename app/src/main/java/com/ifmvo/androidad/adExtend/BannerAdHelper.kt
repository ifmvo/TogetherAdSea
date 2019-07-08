package com.ifmvo.androidad.adExtend

import com.ifmvo.androidad.ad.Config
import com.ifmvo.androidad.ad.TogetherAdConst
import com.liangzemu.ad.sea.AdWrapper
import com.liangzemu.ad.sea.IAdListener
import com.liangzemu.ad.sea.helper.BannerHelper

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-06-24.
 */
object BannerAdHelper {

    private val bannerHelper by lazy { BannerHelper(TogetherAdConst.banner) }
    private var cacheAd: AdWrapper? = null

    fun requestAd() {

        bannerHelper.requestAd(Config.bannerAdConfig(), object : IAdListener {
            override fun onAdClick(channel: String, key: String) {
//                UmengEvent.eventAdClick(channel, UmengEvent.AD_CHECK_LOCATION)
            }

            override fun onAdClose(channel: String, key: String, other: Any) {
                //列表中展示关不掉、不理会
            }

            override fun onAdFailed(failedMsg: String?, key: String) {
                //请求失败了，就不展示，不理会
            }

            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
                cacheAd = adWrapper
            }

            override fun onAdShow(channel: String, key: String) {
//                UmengEvent.eventAdShow(channel, UmengEvent.AD_CHECK_LOCATION)
            }

            override fun onStartRequest(channel: String, key: String) {
//                UmengEvent.eventAdRequest(channel, UmengEvent.AD_CHECK_LOCATION)
            }
        }, onlyOnce = true)
    }

    fun getAd(): AdWrapper? {
        val tempAd = cacheAd
        if (cacheAd != null) {
            bannerHelper.removeAdFromCache(cacheAd!!.key)
        }
        cacheAd = null
        return tempAd
    }

}