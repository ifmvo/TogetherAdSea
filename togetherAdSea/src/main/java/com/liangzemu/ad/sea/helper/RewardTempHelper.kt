package com.liangzemu.ad.sea.helper

import android.os.CountDownTimer
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.liangzemu.ad.sea.*
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.logd

/* 
 * (●ﾟωﾟ●) Google 激励 和 Facebook 插页的组合
 *
 * 因为 Facebook 的激励广告只有游戏才能使用，所以使用插页广告代替激励
 * 
 * Created by Matthew_Chen on 2019-07-11.
 */
class RewardTempHelper(adConstStr: String) : BaseAdHelp(adConstStr) {

    override fun initAD(id: String, adNameType: AdNameType): Pair<Any, String> {
        return when (adNameType) {
            AdNameType.GOOGLE_ADMOB -> {
                val ad = MobileAds.getRewardedVideoAdInstance(TogetherAdSea.context)
                Pair(ad, ad.toString())
            }
            AdNameType.FACEBOOK -> {
                val ad = InterstitialAd(TogetherAdSea.context, id)
                Pair(ad, ad.toString())
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
                adListener.onAdClose(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.toString(), rewarded)
            }

            override fun onRewardedVideoAdLeftApplication() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${TogetherAdSea.context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.toString())

            }

            override fun onRewardedVideoAdLoaded() {
                logd("${AdNameType.GOOGLE_ADMOB.type} $adConstStr: ${TogetherAdSea.context.getString(R.string.prepared)}")
                timer.cancel()
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, AdWrapper(adOrBuilder))
            }

            override fun onRewardedVideoAdOpened() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${TogetherAdSea.context.getString(R.string.show)}")
                adListener.onAdShow(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.toString())

            }

            override fun onRewardedVideoCompleted() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${TogetherAdSea.context.getString(R.string.complete)}")
                rewarded = true
            }

            override fun onRewarded(p0: RewardItem?) {
            }

            override fun onRewardedVideoStarted() {
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                errorCallback(p0.toString())
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
        adOrBuilder as InterstitialAd
        adOrBuilder.setAdListener(object : InterstitialAdListener {
            override fun onInterstitialDisplayed(p0: Ad?) {
            }

            override fun onAdClicked(p0: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${TogetherAdSea.context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.FACEBOOK.type, p0.toString())
            }

            override fun onInterstitialDismissed(p0: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${TogetherAdSea.context.getString(R.string.dismiss)}")
                adListener.onAdClose(AdNameType.FACEBOOK.type, adOrBuilder.toString(), true)
            }

            override fun onError(p0: Ad, adError: AdError?) {
                errorCallback(adError?.errorMessage)
            }

            override fun onAdLoaded(p0: Ad) {
                //取消超时
                timer.cancel()

                logd("${AdNameType.FACEBOOK.type} $adConstStr: ${TogetherAdSea.context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.FACEBOOK.type, AdWrapper(p0))
            }

            override fun onLoggingImpression(p0: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${TogetherAdSea.context.getString(R.string.show)}")
                adListener.onAdShow(AdNameType.FACEBOOK.type, p0.toString())
            }
        })
        adOrBuilder.loadAd()
    }

    override fun onAdClose(channel: String, key: String, other: Any) {
        val adFromCache = removeAdFromCache(key)
        super.onAdClose(channel, key, other)
        removeListener(key)
        if (adFromCache != null && destroyAfterShow) {
            adFromCache.destory()
        }
    }
}