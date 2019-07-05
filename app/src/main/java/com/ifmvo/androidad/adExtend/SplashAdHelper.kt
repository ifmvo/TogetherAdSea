package com.ifmvo.androidad.adExtend

import android.os.CountDownTimer
import com.google.android.gms.ads.InterstitialAd
import com.ifmvo.androidad.ad.Config
import com.ifmvo.androidad.ad.TogetherAdConst
import com.ifmvo.androidad.ad.logd
import com.ifmvo.androidad.ad.loge
import com.liangzemu.ad.sea.AdWrapper
import com.liangzemu.ad.sea.IAdListener
import com.liangzemu.ad.sea.helper.InterstitialHelper

/* 
 * (●ﾟωﾟ●) 开屏广告的管理
 *
 * 逻辑：
 * A、D 两个档位的广告同时请求；
 * 在超时时间内，
 * 如果 A 档返回成功就直接使用；
 * 如果 D 档返回成功，则等待 A，如果再超时时间内 A 没有返回，就使用 D。
 *
 * 总之，超时时间内，有 A 就展示 A，没有 A 就展示 D
 * 
 * Created by Matthew_Chen on 2019-06-25.
 */
object SplashAdHelper {

    private const val tag = "SplashAdHelper"

    private val interHelperA by lazy {
        InterstitialHelper(
            TogetherAdConst.splash_interstitial_a,
            destroyAfterShow = false
        )
    }
    private val interHelperD by lazy {
        InterstitialHelper(
            TogetherAdConst.splash_interstitial_a,
            destroyAfterShow = false
        )
    }

    private var adWrapperA: AdWrapper? = null
    private var adWrapperD: AdWrapper? = null

    private var isResultA = false
    private var isResultD = false

    /**
     * 请求插页广告
     * overTimeSecond：超时时间，单位：秒
     */
    fun requestAd(overTimeSecond: Int = 30, onResult: () -> Unit = {}) {
        logd("SplashAdHelper", "requestAd")
        //如果 A 档有缓存就直接返回 A 档展示
        if (adWrapperA != null) {
            onResult()
            return
        }

        val timer = createTimer(overTimeSecond) {
            interHelperA.onDestory()
            interHelperD.onDestory()
            loge(tag, "超时了")
            onResult()
        }.start()

        isResultA = false
        interHelperA.requestAd(Config.adConfig(), object : IAdListener {
            override fun onAdClick(channel: String, key: String) {
//                UmengEvent.eventAdClick(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onAdClose(channel: String, key: String, other: Any) {
            }

            override fun onAdFailed(failedMsg: String?, key: String) {
                isResultA = true
                loge(tag, "onAdFailed: A")
                handleError(onResult, timer)
            }

            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
                adWrapperA = adWrapper
                timer.cancel()
                logd(tag, "onAdPrepared: A")
                onResult()
            }

            override fun onAdShow(channel: String, key: String) {
//                UmengEvent.eventAdClick(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onStartRequest(channel: String, key: String) {
//                UmengEvent.eventAdRequest(channel, UmengEvent.AD_SPLASH_LOCATION)
            }
        }, onlyOnce = true)

        //如果 D 档有缓存，就不用重新请求 D 档了，等待 A 请求回来，或超时再返回 D 档
        if (adWrapperD != null) {
            isResultD = true
            return
        }

        isResultD = false
        interHelperD.requestAd(Config.adConfig(), object : IAdListener {
            override fun onAdClick(channel: String, key: String) {
//                UmengEvent.eventAdClick(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onAdClose(channel: String, key: String, other: Any) {
            }

            override fun onAdFailed(failedMsg: String?, key: String) {
                isResultD = true
                loge(tag, "onAdFailed: D")
                handleError(onResult, timer)
            }

            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
                adWrapperD = adWrapper
                logd(tag, "onAdPrepared: D")
            }

            override fun onAdShow(channel: String, key: String) {
//                UmengEvent.eventAdClick(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onStartRequest(channel: String, key: String) {
//                UmengEvent.eventAdRequest(channel, UmengEvent.AD_SPLASH_LOCATION)
            }
        }, onlyOnce = true)
    }

    fun showAd() {
        logd("SplashAdHelper", "showAd")
        if (adWrapperA != null) {
            logd("SplashAdHelper", "showAd A")
            when (val ad = adWrapperA!!.realAd) {
                is InterstitialAd -> {
                    ad.show()
                }
                is com.facebook.ads.InterstitialAd -> {
                    ad.show()
                }
            }
            interHelperA.removeAdFromCache(adWrapperA!!.key)
            adWrapperA = null

        } else {
            if (adWrapperD != null) {
                logd("SplashAdHelper", "showAd D")
                when (val ad = adWrapperD!!.realAd) {
                    is InterstitialAd -> {
                        ad.show()
                    }
                    is com.facebook.ads.InterstitialAd -> {
                        ad.show()
                    }
                }
                interHelperD.removeAdFromCache(adWrapperD!!.key)
                adWrapperD = null
            }
        }
    }

    private fun handleError(onFailed: () -> Unit, timer: CountDownTimer) {
        if (isResultA && isResultD) {
            timer.cancel()
            onFailed()
        }
    }

    /**
     * 创建超时倒计时
     * @param callback ()->Unit
     * @return CountDownTimer
     */
    private fun createTimer(millSecond: Int, callback: () -> Unit): CountDownTimer {
        return object : CountDownTimer((millSecond * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                logd(tag, "CountDownTimerOnTick: millisUntilFinished: $millisUntilFinished")
            }

            override fun onFinish() {
                logd(tag, "CountDownTimerOnFinish")
                callback()
            }
        }
    }
}