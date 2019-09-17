package com.liangzemu.ad.sea.helper

import android.os.CountDownTimer
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.liangzemu.ad.sea.*
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.logi

/* 
 * (●ﾟωﾟ●) Google 激励 和 Facebook 插页的组合
 *
 * 因为 Facebook 的激励广告只有游戏才能使用，所以使用插页广告代替激励
 * 
 * Created by Matthew_Chen on 2019-07-11.
 */
class RewardTempHelper(
    adConstStr: String,
    timeOutMillsecond: Long = TogetherAdSea.timeoutMillsecond,
    owner: String = adConstStr
) : BaseAdHelp(adConstStr, timeOutMillsecond, owner) {

    override fun initAD(id: String, adNameType: AdNameType): Pair<Any, String> {
        return when (adNameType) {
            AdNameType.GOOGLE_ADMOB -> {
                val ad = MobileAds.getRewardedVideoAdInstance(TogetherAdSea.context)
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
                logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${TogetherAdSea.context.getString(R.string.dismiss)}")
                adListener.onAdClose(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.hashCode().toString(), rewarded)
            }

            override fun onRewardedVideoAdLeftApplication() {
                logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${TogetherAdSea.context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.hashCode().toString())

            }

            override fun onRewardedVideoAdLoaded() {
                if (adOrBuilder.isLoaded) {
                    logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${TogetherAdSea.context.getString(R.string.prepared)}")
                    timer.cancel()
                    adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, AdWrapper(adOrBuilder))
                } else {
                    errorCallback("Google RewardedVideoAd is not Loaded")
                }
            }

            override fun onRewardedVideoAdOpened() {
                logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${TogetherAdSea.context.getString(R.string.show)}")
                adListener.onAdShow(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.hashCode().toString())

            }

            override fun onRewardedVideoCompleted() {
                logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${TogetherAdSea.context.getString(R.string.complete)}")
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
        errorCallback("Delete FB")
    }

}