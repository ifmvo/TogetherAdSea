package com.ifmvo.androidad.adExtend

import android.os.CountDownTimer
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.ifmvo.androidad.ad.Config
import com.ifmvo.androidad.ad.TogetherAdConst
import com.ifmvo.androidad.ad.logd
import com.ifmvo.androidad.ad.loge
import com.liangzemu.ad.sea.AdWrapper
import com.liangzemu.ad.sea.IAdListener
import com.liangzemu.ad.sea.helper.RewardTempHelper

/* 
 * (●ﾟωﾟ●) 激励广告的管理
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
object RewardAdManager {

    private const val tag = "RewardAdManager"

    private val rewardHelperA by lazy { RewardTempHelper(TogetherAdConst.reward) }
    private val rewardHelperD by lazy { RewardTempHelper(TogetherAdConst.reward) }

    private var adWrapperA: AdWrapper? = null
    private var adWrapperD: AdWrapper? = null

    private var isFailedA = false
    private var isFailedD = false

    private var timer: CountDownTimer? = null

    /**
     * 请求插页广告
     * overTimeSecond：超时时间，单位：秒
     */
    fun requestAd(
        overTimeSecond: Int = 30,
        onSuccess: () -> Unit = {}, onFailed: () -> Unit = {}, onClosed: (isReward: Boolean) -> Unit = {}
    ) {

        rewardHelperA.onDestory()
        rewardHelperD.onDestory()

        logd(tag, "requestAd")
        //如果 A 档有缓存就直接返回 A 档展示
        if (adWrapperA != null) {
            onSuccess()
            return
        }

        timer?.cancel()
        timer = createTimer(overTimeSecond) {
            rewardHelperA.onDestory()
            rewardHelperD.onDestory()
            loge(tag, "超时了")
            onFailed()
        }.start()

        isFailedA = false
        rewardHelperA.requestAd(Config.rewardAdConfig(), object : IAdListener {
            override fun onAdClick(channel: String, key: String) {
//                UmengEvent.eventAdClick(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onAdClose(channel: String, key: String, other: Any) {
                onClosed(other as Boolean)
            }

            override fun onAdFailed(failedMsg: String?, key: String) {
                isFailedA = true
                loge(tag, "onAdFailed: A: $failedMsg")
                handleError(onFailed)
            }

            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
                adWrapperA = adWrapper
                timer?.cancel()
                logd(tag, "onAdPrepared: A")
                onSuccess()
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
            isFailedD = true
            return
        }

        isFailedD = false
        rewardHelperD.requestAd(Config.rewardAdConfig(), object : IAdListener {
            override fun onAdClick(channel: String, key: String) {
//                UmengEvent.eventAdClick(channel, UmengEvent.AD_SPLASH_LOCATION)
            }

            override fun onAdClose(channel: String, key: String, other: Any) {
                onClosed(other as Boolean)
            }

            override fun onAdFailed(failedMsg: String?, key: String) {
                isFailedD = true
                loge(tag, "onAdFailed: D: $failedMsg")
                handleError(onFailed)
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
        logd(tag, "showAd")
        if (adWrapperA != null) {
            logd(tag, "showAd A")
            when (val ad = adWrapperA!!.realAd) {
                is RewardedVideoAd -> {
                    ad.show()
                }
                is com.facebook.ads.InterstitialAd -> {
                    ad.show()
                }
            }
            rewardHelperA.removeAd(adWrapperA!!.key)
            adWrapperA = null

        } else {
            if (adWrapperD != null) {
                logd(tag, "showAd D")
                when (val ad = adWrapperD!!.realAd) {
                    is RewardedVideoAd -> {
                        ad.show()
                    }
                    is com.facebook.ads.InterstitialAd -> {
                        ad.show()
                    }
                }
                rewardHelperD.removeAd(adWrapperD!!.key)
                adWrapperD = null
            }
        }
    }

    private fun handleError(onFailed: () -> Unit) {
        if (isFailedA && isFailedD) {
            timer?.cancel()
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