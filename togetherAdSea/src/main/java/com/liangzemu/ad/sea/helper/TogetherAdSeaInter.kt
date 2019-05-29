package com.liangzemu.ad.sea.helper

import android.content.Context
import androidx.annotation.NonNull
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAdListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.liangzemu.ad.sea.AdBase
import com.liangzemu.ad.sea.R
import com.liangzemu.ad.sea.TogetherAdSea
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.AdRandomUtil
import com.liangzemu.ad.sea.other.logd
import com.liangzemu.ad.sea.other.loge

/* 
 * (●ﾟωﾟ●) 插屏广告
 * 
 * Created by Matthew_Chen on 2019-05-29.
 */
object TogetherAdSeaInter : AdBase {

    private var interGoogle: InterstitialAd? = null
    private var interFacebook: com.facebook.ads.InterstitialAd? = null

    fun requestAdInter(
        @NonNull context: Context,
        interConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adListener: AdListenerInter
    ) {

        val randomAdName = AdRandomUtil.getRandomAdName(interConfigStr)
        when (randomAdName) {
            AdNameType.GOOGLE_ADMOB -> TogetherAdSeaInter.requestAdInterGoogle(
                context.applicationContext,
                interConfigStr,
                adConstStr,
                0,
                adListener
            )
            AdNameType.FACEBOOK -> TogetherAdSeaInter.requestAdInterFacebook(
                context.applicationContext,
                interConfigStr,
                adConstStr,
                0,
                adListener
            )
            else -> {
                adListener.onAdFailed(context.getString(R.string.all_ad_error))
                loge(context.getString(R.string.all_ad_error))
            }
        }
    }

    private fun requestAdInterGoogle(
        @NonNull context: Context,
        bannerConfigStr: String?,
        @NonNull adConstStr: String,
        indexGoogle: Int,
        @NonNull adListener: AdListenerInter
    ) {
        val idList = TogetherAdSea.idListGoogleMap[adConstStr]

        if (indexGoogle >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newBannerConfig = bannerConfigStr?.replace(AdNameType.GOOGLE_ADMOB.type, AdNameType.NO.type)
            requestAdInter(context, newBannerConfig, adConstStr, adListener)
            return
        }

        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(context.getString(R.string.ad_id_no))
            return
        }

        adListener.onStartRequest(AdNameType.GOOGLE_ADMOB.type)

        interGoogle = InterstitialAd(context)
        interGoogle?.adUnitId = idList[indexGoogle]
        interGoogle?.loadAd(AdRequest.Builder().build())
        interGoogle?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type)
            }

            override fun onAdClosed() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.dismiss)}")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                loge("${AdNameType.GOOGLE_ADMOB.type}: indexGoogle:$indexGoogle, errorCode:$errorCode")

                if (indexGoogle < TogetherAdSea.idListGoogleMap[adConstStr]?.size ?: 0) {
                    val newIndexGoogle = indexGoogle + 1
                    requestAdInterGoogle(context, bannerConfigStr, adConstStr, newIndexGoogle, adListener)
                } else {
                    val newBannerConfig = bannerConfigStr?.replace(AdNameType.GOOGLE_ADMOB.type, AdNameType.NO.type)
                    requestAdInter(context, newBannerConfig, adConstStr, adListener)
                }
            }

            override fun onAdClicked() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type)
            }

            override fun onAdImpression() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.exposure)}")
            }

            override fun onAdOpened() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.opened)}")
            }
        }
    }

    private fun requestAdInterFacebook(
        @NonNull context: Context,
        bannerConfigStr: String?,
        @NonNull adConstStr: String,
        indexFacebook: Int,
        @NonNull adListener: AdListenerInter
    ) {

        val idList = TogetherAdSea.idListFacebookMap[adConstStr]

        if (indexFacebook >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newBannerConfig = bannerConfigStr?.replace(AdNameType.FACEBOOK.type, AdNameType.NO.type)
            requestAdInter(context, newBannerConfig, adConstStr, adListener)
            return
        }

        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(context.getString(R.string.ad_id_no))
            return
        }

        adListener.onStartRequest(AdNameType.FACEBOOK.type)

        interFacebook = com.facebook.ads.InterstitialAd(context, idList[indexFacebook])
        interFacebook?.setAdListener(object : InterstitialAdListener {
            override fun onInterstitialDisplayed(p0: Ad?) {
            }

            override fun onAdClicked(p0: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.FACEBOOK.type)
            }

            override fun onInterstitialDismissed(p0: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.dismiss)}")
            }

            override fun onError(p0: Ad?, adError: AdError?) {
                loge("${AdNameType.FACEBOOK.type}: indexFacebook:$indexFacebook, errorCode:${adError?.errorCode} ${adError?.errorMessage}")

                val newIndexFacebook = indexFacebook + 1
                requestAdInterFacebook(context, bannerConfigStr, adConstStr, newIndexFacebook, adListener)
            }

            override fun onAdLoaded(p0: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.FACEBOOK.type)
            }

            override fun onLoggingImpression(p0: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.exposure)}")
            }
        })
        interFacebook?.loadAd()
    }

    fun showAdInter() {
        if (interGoogle?.isLoaded == true) {
            interGoogle?.show()
        } else {
            if (interFacebook?.isAdLoaded == true) {
                interFacebook?.show()
            }
        }
    }

    /**
     * 监听器
     */
    interface AdListenerInter {

        fun onStartRequest(channel: String)

        fun onAdClick(channel: String)

        fun onAdFailed(failedMsg: String?)

        fun onAdPrepared(channel: String)
    }
}