package com.liangzemu.ad.sea.helper

import android.os.CountDownTimer
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.liangzemu.ad.sea.*
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.loge
import com.liangzemu.ad.sea.other.logi

/* 
 * (●ﾟωﾟ●) 横幅广告
 * 
 * Created by Matthew_Chen on 2019-06-24.
 */
class BannerHelper(
    adConstStr: String,
    timeOutMillsecond: Long = TogetherAdSea.timeoutMillsecond,
    owner: String = adConstStr
) : BaseAdHelp(adConstStr, timeOutMillsecond, owner) {

    @Throws(IllegalArgumentException::class)
    override fun initAD(id: String, adNameType: AdNameType): Pair<Any, String> {
        return when (adNameType) {
            AdNameType.GOOGLE_ADMOB -> {
                val adView = AdView(context)
                adView.adSize = AdSize.SMART_BANNER
                adView.adUnitId = id
                Pair(adView, adView.hashCode().toString())
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
        adOrBuilder as AdView

        val adRequest = AdRequest.Builder()
            .apply { if (TogetherAdSea.testDeviceID != null) addTestDevice(TogetherAdSea.testDeviceID) }.build()
        adOrBuilder.loadAd(adRequest)

        adOrBuilder.adListener = object : AdListener() {
            override fun onAdLoaded() {
                timer.cancel()

                logi("${AdNameType.GOOGLE_ADMOB.type}:$adConstStr ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, AdWrapper(adOrBuilder, adOrBuilder.hashCode().toString()))

                logi("${AdNameType.GOOGLE_ADMOB.type}: $adConstStr ${context.getString(R.string.exposure)}")
                adListener.onAdShow(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.hashCode().toString())
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                loge("${AdNameType.GOOGLE_ADMOB.type}: $adConstStr ${context.getString(R.string.failed)}")
                errorCallback(errorCode.toString())
            }

            override fun onAdOpened() {
                logi("${AdNameType.GOOGLE_ADMOB.type}: $adConstStr ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.hashCode().toString())
            }

            override fun onAdLeftApplication() {
            }

            override fun onAdClicked() {
            }

            override fun onAdImpression() {
            }

            override fun onAdClosed() {
            }
        }
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