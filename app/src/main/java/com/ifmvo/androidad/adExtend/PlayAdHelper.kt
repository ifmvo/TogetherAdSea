//package com.video.downloader.ad
//
//import com.ifmvo.androidad.ad.TogetherAdConst
//import com.liangzemu.ad.sea.AdWrapper
//import com.liangzemu.ad.sea.IAdListener
//import com.liangzemu.ad.sea.helper.FlowHelper
//
///*
// * (●ﾟωﾟ●) 播放前贴
// *
// * Created by Matthew_Chen on 2019-06-21.
// */
//object PlayAdHelper {
//
//    private val flowHelper by lazy { FlowHelper(TogetherAdConst.) }
//    private var cacheAd: AdWrapper? = null
//
//    fun requestAd() {
//        flowHelper.requestAd(Config.videoAdConfig(), object : IAdListener {
//            override fun onAdClick(channel: String, key: String) {
//                UmengEvent.eventAdClick(channel, UmengEvent.AD_VIDEO_LOCATION)
//            }
//
//            override fun onAdClose(channel: String, key: String, other: Any) {
//                //列表中展示关不掉、不理会
//            }
//
//            override fun onAdFailed(failedMsg: String?, key: String) {
//
//            }
//
//            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
//                cacheAd = adWrapper
//            }
//
//            override fun onAdShow(channel: String, key: String) {
//                UmengEvent.eventAdShow(channel, UmengEvent.AD_VIDEO_LOCATION)
//            }
//
//            override fun onStartRequest(channel: String, key: String) {
//                UmengEvent.eventAdRequest(channel, UmengEvent.AD_VIDEO_LOCATION)
//            }
//        }, onlyOnce = true)
//    }
//
//    fun requestAd(onSuccess: (adWrapper: AdWrapper) -> Unit, onFailed: () -> Unit) {
//
//        if (cacheAd != null) {
//            flowHelper.removeAdFromCache(cacheAd!!.key)
//            onSuccess(cacheAd!!)
//            cacheAd = null
//            return
//        }
//
//        flowHelper.requestAd(Config.videoAdConfig(), object : IAdListener {
//            override fun onAdClick(channel: String, key: String) {
//                UmengEvent.eventAdClick(channel, UmengEvent.AD_VIDEO_LOCATION)
//            }
//
//            override fun onAdClose(channel: String, key: String, other: Any) {
//                //列表中展示关不掉、不理会
//            }
//
//            override fun onAdFailed(failedMsg: String?, key: String) {
//                onFailed()
//            }
//
//            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
//                onSuccess(adWrapper)
//            }
//
//            override fun onAdShow(channel: String, key: String) {
//                UmengEvent.eventAdShow(channel, UmengEvent.AD_VIDEO_LOCATION)
//            }
//
//            override fun onStartRequest(channel: String, key: String) {
//                UmengEvent.eventAdRequest(channel, UmengEvent.AD_VIDEO_LOCATION)
//            }
//        }, onlyOnce = true)
//    }
//
//    fun removeAdFromCache(adWrapper: AdWrapper) {
//        flowHelper.removeAdFromCache(adWrapper.key)
//    }
//
//    fun onDestory() {
//        flowHelper.onDestory()
//    }
//}