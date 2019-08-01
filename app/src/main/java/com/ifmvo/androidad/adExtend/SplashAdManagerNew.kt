package com.ifmvo.androidad.adExtend

import android.os.CountDownTimer
import com.google.android.gms.ads.InterstitialAd
import com.ifmvo.androidad.UmengEvent
import com.ifmvo.androidad.ad.Config
import com.ifmvo.androidad.ad.TogetherAdConst
import com.ifmvo.androidad.ad.logd
import com.ifmvo.androidad.ad.loge
import com.liangzemu.ad.sea.AdWrapper
import com.liangzemu.ad.sea.IAdListener
import com.liangzemu.ad.sea.helper.InterstitialHelper

/*
 * (●ﾟωﾟ●) 插页广告
 *
 * Created by Matthew_Chen on 2019-05-29.
 */
object SplashAdManagerNew {

    private const val tag = "SplashAdManagerNew"

    private const val adConstStr = TogetherAdConst.interstitial
    private val configStr by lazy { Config.interstitialAdConfig() }

    private val adHelperA by lazy { InterstitialHelper(TogetherAdConst.splash_interstitial_a) }
    private val adHelperD by lazy { InterstitialHelper(TogetherAdConst.splash_interstitial_d) }

    private var isFailedA = false
    private var isFailedD = false

    private var timer: CountDownTimer? = null

    fun requestAd(
        overTimeSecond: Long = 30,
        onSuccess: () -> Unit = { logd(tag, "onSuccess: () -> Unit") },
        onFailed: () -> Unit = { logd(tag, "onFailed: () -> Unit") },
        onClosed: () -> Unit = {}
    ) {

        isFailedA = false
        isFailedD = false

        timer?.cancel()
        timer = createTimer(overTimeSecond) {

            logd(tag, "${overTimeSecond}秒，时间到")

            val adCacheA = adHelperA.getAdFromCache { true }
            if (adCacheA != null) {
                adHelperD.onDestory()
                onSuccess()
                return@createTimer
            }
            val adCacheD = adHelperD.getAdFromCache { true }

            if (adCacheD != null) {
                adHelperA.onDestory()
                onSuccess()
                return@createTimer
            }

            onFailed()

        }.start()

        adHelperA.requestAd(configStr, object : IAdListener {
            override fun onAdClick(channel: String, key: String) {
                logd(tag, "onAdClick A")
                UmengEvent.eventAdClick(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onAdClose(channel: String, key: String, other: Any) {
                logd(tag, "onAdClose A")
                adHelperA.removeAd(key)?.destory()
                adHelperA.onDestory()
                onClosed()
            }

            override fun onAdFailed(failedMsg: String?, key: String) {
                loge(tag, "onAdFailed A")
                isFailedA = true
                val adCacheD = adHelperD.getAdFromCache { true }
                if (adCacheD != null) {
                    timer?.cancel()
                    onSuccess()
                } else {
                    if (isFailedD) {
                        timer?.cancel()
                        onFailed()
                    }
                }
            }

            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
                logd(tag, "onAdPrepared A")
                UmengEvent.eventAdFills(channel, UmengEvent.AD_SPLASH_LOCATION)
                timer?.cancel()
                adHelperD.onDestory()
                onSuccess()
            }

            override fun onAdShow(channel: String, key: String) {
                logd(tag, "onAdShow A")
                UmengEvent.eventAdShow(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onStartRequest(channel: String, key: String) {
                logd(tag, "onStartRequest A")
            }
        }, onlyOnce = true)

        adHelperD.requestAd(configStr, object : IAdListener {
            override fun onAdClick(channel: String, key: String) {
                logd(tag, "onAdClick D")
                UmengEvent.eventAdClick(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onAdClose(channel: String, key: String, other: Any) {
                logd(tag, "onAdClose D")
                adHelperD.removeAd(key)?.destory()
                adHelperD.onDestory()
                onClosed()
            }

            override fun onAdFailed(failedMsg: String?, key: String) {
                loge(tag, "onAdFailed D")
                isFailedD = true
                val adCacheA = adHelperA.getAdFromCache { true }
                if (adCacheA != null) {
                    timer?.cancel()
                    onSuccess()
                } else {
                    if (isFailedA) {
                        timer?.cancel()
                        onFailed()
                    }
                }
            }

            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
                logd(tag, "onAdPrepared D")
                UmengEvent.eventAdFills(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onAdShow(channel: String, key: String) {
                logd(tag, "onAdShow D")
                UmengEvent.eventAdShow(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onStartRequest(channel: String, key: String) {
                logd(tag, "onStartRequest D")
            }
        }, onlyOnce = true)
    }

    /**
     * 优先 A 档
     * A 档为空的话，就展示 D
     * 如果都是空就不展示
     */
    fun showAd() {
        var adCache = adHelperA.getAdFromCache { true }

        if (adCache == null) {
            adCache = adHelperD.getAdFromCache { true }
        }

        when (val ad = adCache?.realAd) {
            is InterstitialAd -> {
                logd(tag, "showAd Gooogle")
                ad.show()
            }
            is com.facebook.ads.InterstitialAd -> {
                logd(tag, "showAd Facebook")
                ad.show()
            }
        }
    }

    fun onDestroy() {
        logd(tag, "onDestroy")
        timer?.cancel()
        adHelperA.onDestory()
        adHelperD.onDestory()
    }

    /**
     * 创建超时倒计时
     * @param callback ()->Unit
     * @return CountDownTimer
     */
    private fun createTimer(second: Long, callback: () -> Unit): CountDownTimer {
        return object : CountDownTimer(second * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                logd(tag, "onTick: $millisUntilFinished")
            }

            override fun onFinish() {
                logd(tag, "onFinish")
                callback()
            }
        }
    }
}