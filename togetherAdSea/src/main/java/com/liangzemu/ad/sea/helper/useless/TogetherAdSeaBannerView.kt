package com.liangzemu.ad.sea.helper.useless

//package com.liangzemu.ad.sea.helper
//
//import android.content.Context
//import androidx.annotation.NonNull
//import com.facebook.ads.Ad
//import com.facebook.ads.AdError
//import com.google.android.gms.ads.AdListener
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdSize
//import com.google.android.gms.ads.AdView
//import com.liangzemu.ad.sea.AdBase
//import com.liangzemu.ad.sea.R
//import com.liangzemu.ad.sea.TogetherAdSea
//import com.liangzemu.ad.sea.other.AdNameType
//import com.liangzemu.ad.sea.other.AdRandomUtil
//import com.liangzemu.ad.sea.other.logd
//import com.liangzemu.ad.sea.other.loge
//
//
///*
// * (●ﾟωﾟ●) 横幅广告 Banner
// *
// * Created by Matthew_Chen on 2019-04-22.
// */
//@Deprecated("old", replaceWith = ReplaceWith("BannerHelper"), level = DeprecationLevel.ERROR)
//object TogetherAdSeaBannerView : AdBase {
//
//    fun showAdBanner(
//        @NonNull context: Context,
//        bannerConfigStr: String?,
//        @NonNull adConstStr: String,
//        @NonNull adListener: AdListenerBanner
//    ) {
//
//        val randomAdName = AdRandomUtil.getRandomAdName(bannerConfigStr)
//        when (randomAdName) {
//            AdNameType.GOOGLE_ADMOB -> showAdBannerGoogle(
//                context.applicationContext,
//                bannerConfigStr,
//                adConstStr,
//                adListener
//            )
//            AdNameType.FACEBOOK -> showAdBannerFacebook(
//                context.applicationContext,
//                bannerConfigStr,
//                adConstStr,
//                adListener
//            )
//            else -> {
//                adListener.onAdFailed(context.getString(R.string.all_ad_error))
//                loge(context.getString(R.string.all_ad_error))
//            }
//        }
//    }
//
//    /**
//     * Google
//     * splashConfigStr ：例：google:2,facebook:8
//     * adConstStr : 例：TogetherAdConst.AD_SPLASH
//     */
//    private fun showAdBannerGoogle(
//        @NonNull context: Context,
//        bannerConfigStr: String?,
//        @NonNull adConstStr: String,
//        @NonNull adListener: AdListenerBanner
//    ) {
//        adListener.onStartRequest(AdNameType.GOOGLE_ADMOB.type)
//
//        val mAdView = AdView(context)
//        mAdView.adSize = AdSize.SMART_BANNER
//        mAdView.adUnitId = TogetherAdSea.idMapGoogle[adConstStr]
//        val adRequest = AdRequest.Builder()
//            .apply { if (TogetherAdSea.testDeviceID != null) addTestDevice(TogetherAdSea.testDeviceID) }.build()
//        mAdView.loadAd(adRequest)
//        mAdView.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
//                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, mAdView)
//            }
//
//            override fun onAdFailedToLoad(errorCode: Int) {
//                loge("${AdNameType.GOOGLE_ADMOB.type}: errorCode:$errorCode")
//                val newBannerConfig = bannerConfigStr?.replace(AdNameType.GOOGLE_ADMOB.type, AdNameType.NO.type)
//                showAdBanner(context, newBannerConfig, adConstStr, adListener)
//            }
//
//            override fun onAdClicked() {
//                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.clicked)}")
//                adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type)
//            }
//
//            override fun onAdImpression() {
//                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.exposure)}")
//            }
//        }
//    }
//
//    /**
//     * Facebook
//     */
//    private fun showAdBannerFacebook(
//        @NonNull context: Context,
//        bannerConfigStr: String?,
//        @NonNull adConstStr: String,
//        @NonNull adListener: AdListenerBanner
//    ) {
//
//        adListener.onStartRequest(AdNameType.FACEBOOK.type)
//
//        val adView = com.facebook.ads.AdView(
//            context,
//            TogetherAdSea.idMapFacebook[adConstStr],
//            com.facebook.ads.AdSize.BANNER_HEIGHT_50
//        )
//
//        adView.setAdListener(object : com.facebook.ads.AdListener {
//            override fun onAdClicked(ad: Ad?) {
//                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
//                adListener.onAdClick(AdNameType.FACEBOOK.type)
//            }
//
//            override fun onError(ad: Ad?, adError: AdError?) {
//                loge("${AdNameType.FACEBOOK.type}: adError:${adError?.errorCode},${adError?.errorMessage}")
//                val newBannerConfig = bannerConfigStr?.replace(AdNameType.FACEBOOK.type, AdNameType.NO.type)
//                showAdBanner(context, newBannerConfig, adConstStr, adListener)
//            }
//
//            override fun onAdLoaded(ad: Ad?) {
//                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")
//                adListener.onAdPrepared(AdNameType.FACEBOOK.type, adView)
//            }
//
//            override fun onLoggingImpression(ad: Ad?) {
//                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.exposure)}")
//            }
//        })
//
//        adView.loadAd()
//    }
//
//    /**
//     * 监听器
//     */
//    interface AdListenerBanner {
//
//        fun onStartRequest(channel: String)
//
//        fun onAdClick(channel: String)
//
//        fun onAdFailed(failedMsg: String?)
//
//        fun onAdPrepared(channel: String, bannerView: Any)
//    }
//
//}