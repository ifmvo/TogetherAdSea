package com.liangzemu.ad.sea.helper

import android.content.Context
import androidx.annotation.NonNull
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.liangzemu.ad.sea.AdBase
import com.liangzemu.ad.sea.R
import com.liangzemu.ad.sea.TogetherAdSea
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.AdRandomUtil
import com.liangzemu.ad.sea.other.logd
import com.liangzemu.ad.sea.other.loge


/*
 * (●ﾟωﾟ●) 开屏广告
 * 
 * Created by Matthew_Chen on 2019-04-22.
 */
object TogetherAdSeaFlow : AdBase {

    fun showAdFlow(
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adListener: AdListenerFlow
    ) {

        val randomAdName = AdRandomUtil.getRandomAdName(splashConfigStr)
        when (randomAdName) {
            AdNameType.GOOGLE_ADMOB -> showAdFlowGoogle(
                context,
                splashConfigStr,
                adConstStr,
                adListener
            )
            AdNameType.FACEBOOK -> showAdFlowFacebook(
                context,
                splashConfigStr,
                adConstStr,
                adListener
            )
            else -> {
                adListener.onAdFailed(context.getString(R.string.all_ad_error))
                loge(context.getString(R.string.all_ad_error))
            }
        }
    }

    /**
     * Google
     * splashConfigStr ：例：google:2,facebook:8
     * adConstStr : 例：TogetherAdConst.AD_SPLASH
     */
    private fun showAdFlowGoogle(
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adListener: AdListenerFlow
    ) {
        adListener.onStartRequest(AdNameType.GOOGLE_ADMOB.type)
        val adLoader = AdLoader.Builder(context, TogetherAdSea.idMapGoogle[adConstStr])
            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    loge("${AdNameType.GOOGLE_ADMOB.type}: errorCode:$errorCode")
                    val newSplashConfig = splashConfigStr?.replace(AdNameType.GOOGLE_ADMOB.type, AdNameType.NO.type)
                    showAdFlow(context, newSplashConfig, adConstStr, adListener)
                }

                override fun onAdImpression() {
                    logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.exposure)}")
                }

                override fun onAdClicked() {
                    logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.clicked)}")
                    adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type)
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
        adLoader.loadAds(AdRequest.Builder().build(), 3)//最大5
    }

    /**
     * Facebook
     */
    private fun showAdFlowFacebook(
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adListener: AdListenerFlow
    ) {

        adListener.onStartRequest(AdNameType.FACEBOOK.type)

        val nativeAd = NativeAd(context, TogetherAdSea.idMapFacebook[adConstStr])
        nativeAd.setAdListener(object : NativeAdListener {
            override fun onAdClicked(ad: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
            }

            override fun onMediaDownloaded(ad: Ad?) {
            }

            override fun onError(ad: Ad?, adError: AdError?) {
                loge("${AdNameType.FACEBOOK.type}: adError:${adError?.errorCode},${adError?.errorMessage}")
                val newSplashConfig = splashConfigStr?.replace(AdNameType.FACEBOOK.type, AdNameType.NO.type)
                showAdFlow(context, newSplashConfig, adConstStr, adListener)
            }

            override fun onAdLoaded(ad: Ad?) {
                if (nativeAd != ad) {
                    loge("${AdNameType.FACEBOOK.type}: 广告返回错误 nativeAd != ad")
                    val newSplashConfig = splashConfigStr?.replace(AdNameType.FACEBOOK.type, AdNameType.NO.type)
                    showAdFlow(context, newSplashConfig, adConstStr, adListener)
                    return
                }
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.FACEBOOK.type, ad)
            }

            override fun onLoggingImpression(ad: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.exposure)}")
            }
        })
        nativeAd.loadAd()
    }

    /**
     * 监听器
     */
    interface AdListenerFlow {

        fun onStartRequest(channel: String)

        fun onAdClick(channel: String)

        fun onAdFailed(failedMsg: String?)

        fun onAdPrepared(channel: String, ad: Any)
    }

}