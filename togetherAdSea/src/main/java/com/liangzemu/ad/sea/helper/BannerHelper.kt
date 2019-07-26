package com.liangzemu.ad.sea.helper

import android.os.CountDownTimer
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.liangzemu.ad.sea.*
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.logd

/* 
 * (●ﾟωﾟ●) 横幅广告
 * 
 * Created by Matthew_Chen on 2019-06-24.
 */
class BannerHelper(adConstStr: String) : BaseAdHelp(adConstStr) {

    @Throws(IllegalArgumentException::class)
    override fun initAD(id: String, adNameType: AdNameType): Pair<Any, String> {
        return when (adNameType) {
            AdNameType.GOOGLE_ADMOB -> {
                val adView = AdView(context)
                Pair(adView, adView.toString())
            }
            AdNameType.FACEBOOK -> {
                val adView = com.facebook.ads.AdView(
                    context, id,
                    com.facebook.ads.AdSize.BANNER_HEIGHT_50
                )
                Pair(adView, adView.toString())
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
        adOrBuilder.adSize = AdSize.SMART_BANNER
        adOrBuilder.adUnitId = id
        val adRequest = AdRequest.Builder()
            .apply { if (TogetherAdSea.testDeviceID != null) addTestDevice(TogetherAdSea.testDeviceID) }.build()
        adOrBuilder.loadAd(adRequest)
        adOrBuilder.adListener = object : AdListener() {
            override fun onAdLoaded() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
                timer.cancel()
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, AdWrapper(adOrBuilder))
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                errorCallback(errorCode.toString())
            }

            override fun onAdClicked() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.toString())
            }

            override fun onAdImpression() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.exposure)}")
                adListener.onAdShow(AdNameType.GOOGLE_ADMOB.type, adOrBuilder.toString())
            }
        }

    }

    override fun setFaceBookAdListenerAndStart(
        adOrBuilder: Any,
        adListener: IAdListener,
        timer: CountDownTimer,
        errorCallback: (String?) -> Unit
    ) {
        adOrBuilder as com.facebook.ads.AdView
        adOrBuilder.setAdListener(object : com.facebook.ads.AdListener {
            override fun onAdClicked(ad: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.FACEBOOK.type, ad.toString())
            }

            override fun onError(ad: Ad?, adError: AdError?) {
                errorCallback(adError?.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")
                timer.cancel()
                adListener.onAdPrepared(AdNameType.FACEBOOK.type, AdWrapper(adOrBuilder))
            }

            override fun onLoggingImpression(ad: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.exposure)}")
                adListener.onAdShow(AdNameType.FACEBOOK.type, ad.toString())
            }
        })

        adOrBuilder.loadAd()
    }


}