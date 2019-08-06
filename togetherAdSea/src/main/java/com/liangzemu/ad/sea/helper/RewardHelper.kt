package com.liangzemu.ad.sea.helper

import android.os.CountDownTimer
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.liangzemu.ad.sea.*
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.logi


/**
 * (●ﾟωﾟ●) 激励视频广告：已实现档位
 *
 * Created by Matthew_Chen on 2019-06-05.
 */
class RewardHelper(
    adConstStr: String,
    timeOutMillsecond: Long = TogetherAdSea.timeoutMillsecond,
    owner: String = adConstStr
) : BaseAdHelp(adConstStr, timeOutMillsecond, owner) {
    override fun initAD(id: String, adNameType: AdNameType): Pair<Any, String> {
        return when (adNameType) {
            AdNameType.GOOGLE_ADMOB -> {
                val ad = MobileAds.getRewardedVideoAdInstance(context)
                Pair(ad, ad.hashCode().toString())
            }
            AdNameType.FACEBOOK -> {
                val ad = com.facebook.ads.RewardedVideoAd(context, id)
                Pair(ad, ad.hashCode().toString())
            }
            else -> {
                throw IllegalArgumentException("没有此广告类型:${adNameType.type}")
            }
        }
    }

    override fun setGoogleAdListenerAndStart(
        id: String,
        adOrBuilder: Any,
        adListener: IAdListener,
        timer: CountDownTimer,
        errorCallback: (String?) -> Unit
    ) {
        adOrBuilder as RewardedVideoAd
        adOrBuilder.rewardedVideoAdListener = object : RewardedVideoAdListener {
            var rewarded = false
            override fun onRewardedVideoAdClosed() {
                logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${context.getString(R.string.dismiss)}")
                adListener.onAdClose(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.hashCode().toString(), rewarded)
            }

            override fun onRewardedVideoAdLeftApplication() {
                logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.hashCode().toString())

            }

            override fun onRewardedVideoAdLoaded() {
                if (adOrBuilder.isLoaded) {
                    logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${context.getString(R.string.prepared)}")
                    timer.cancel()
                    adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, AdWrapper(adOrBuilder))
                } else {
                    errorCallback("Google ad is not loaded")
                }
            }

            override fun onRewardedVideoAdOpened() {
                logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${context.getString(R.string.show)}")
                adListener.onAdShow(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.hashCode().toString())

            }

            override fun onRewardedVideoCompleted() {
                logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${context.getString(R.string.complete)}")
                rewarded = true
            }

            override fun onRewarded(p0: RewardItem?) {
            }

            override fun onRewardedVideoStarted() {
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                errorCallback(p0.hashCode().toString())
            }
        }
        adOrBuilder.loadAd(id, getGoogleAdRequest())
    }

    override fun setFaceBookAdListenerAndStart(
        adOrBuilder: Any,
        adListener: IAdListener,
        timer: CountDownTimer,
        errorCallback: (String?) -> Unit
    ) {
        adOrBuilder as com.facebook.ads.RewardedVideoAd
        adOrBuilder.setAdListener(object : com.facebook.ads.RewardedVideoAdListener {
            var isRewarded = false
            override fun onRewardedVideoClosed() {
                logi("${AdNameType.FACEBOOK.type}:$adConstStr ${context.getString(R.string.dismiss)}")
                adListener.onAdClose(AdNameType.FACEBOOK.type, adOrBuilder.hashCode().toString(), isRewarded)
            }

            override fun onAdClicked(p0: Ad) {
                logi("${AdNameType.FACEBOOK.type}:$adConstStr ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.FACEBOOK.type, p0.hashCode().toString())
            }

            override fun onRewardedVideoCompleted() {
                logi("${AdNameType.FACEBOOK.type}:$adConstStr ${context.getString(R.string.complete)}")
                isRewarded = true
            }

            override fun onError(p0: Ad, adError: AdError?) {
                errorCallback(adError?.errorMessage)
            }

            override fun onAdLoaded(p0: Ad) {
                //取消超时
                timer.cancel()

                logi("${AdNameType.FACEBOOK.type}:$adConstStr ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.FACEBOOK.type, AdWrapper(p0))
            }

            override fun onLoggingImpression(p0: Ad) {
                logi("${AdNameType.FACEBOOK.type}:$adConstStr ${context.getString(R.string.show)}")
                adListener.onAdShow(AdNameType.FACEBOOK.type, p0.hashCode().toString())
            }
        })
        adOrBuilder.loadAd()
    }
}