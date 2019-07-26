package com.ifmvo.androidad.adExtend

import com.ifmvo.androidad.UmengEvent
import com.ifmvo.androidad.ad.Config
import com.ifmvo.androidad.ad.TogetherAdConst
import com.ifmvo.androidad.ad.loge
import com.liangzemu.ad.sea.AdWrapper
import com.liangzemu.ad.sea.IAdListener
import com.liangzemu.ad.sea.helper.FlowBannerFlowHelper

/* 
 * (●ﾟωﾟ●) Google 原生 && Facebook 原生横幅 列表中展示
 * 
 * Created by Matthew_Chen on 2019-06-21.
 */
object FlowBannerFlowAdManager {

    private val flowHelper by lazy { FlowBannerFlowHelper(TogetherAdConst.flow_banner) }
    private val cacheList by lazy { mutableListOf<AdWrapper>() }

    fun requestAd(number: Int) {
        val startTime = System.currentTimeMillis()
        repeat(number) {
            flowHelper.requestAd(Config.flowBannerConfig(), object : IAdListener {
                override fun onAdClick(channel: String, key: String) {
                    UmengEvent.eventAdClick(channel, UmengEvent.AD_DOWNLOADED_LOCATION)
                }

                override fun onAdClose(channel: String, key: String, other: Any) {
                    //列表中展示关不掉、不理会
                }

                override fun onAdFailed(failedMsg: String?, key: String) {
                    //请求失败了，就不展示，不理会
                }

                override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
                    cacheList.add(adWrapper)
                }

                override fun onAdShow(channel: String, key: String) {
                    UmengEvent.eventAdShow(channel, UmengEvent.AD_DOWNLOADED_LOCATION)
                }

                override fun onStartRequest(channel: String, key: String) {
                    UmengEvent.eventAdRequest(channel, UmengEvent.AD_DOWNLOADED_LOCATION)
                }
            }, onlyOnce = false)
        }
        loge("ifmvo", "总：${System.currentTimeMillis() - startTime}")
    }

    fun getAdList(): MutableList<AdWrapper> {
        val tempList = mutableListOf<AdWrapper>()
        tempList.addAll(cacheList)
        for (adWrapper in cacheList) {
            flowHelper.removeAd(adWrapper.key)
        }
        cacheList.clear()
        return tempList
    }

}