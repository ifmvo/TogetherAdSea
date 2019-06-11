package com.liangzemu.ad.sea.helper

import android.content.Context
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.NonNull
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
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
object TogetherAdSeaSplash : AdBase {

    fun showAdFull(
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adsParentLayout: ViewGroup,
        @NonNull adListener: AdListenerSplash
    ) {

        //按照比例随机出一种广告
        val randomAdName = AdRandomUtil.getRandomAdName(splashConfigStr)
        when (randomAdName) {
            AdNameType.GOOGLE_ADMOB -> showAdFullGoogle(
                context.applicationContext,
                splashConfigStr,
                adConstStr,
                adsParentLayout,
                adListener
            )
            AdNameType.FACEBOOK -> showAdFullFacebook(
                context.applicationContext,
                splashConfigStr,
                adConstStr,
                adsParentLayout,
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
    private fun showAdFullGoogle(
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adsParentLayout: ViewGroup,
        @NonNull adListener: AdListenerSplash
    ) {
        adListener.onStartRequest(AdNameType.GOOGLE_ADMOB.type)
        val adLoader = AdLoader.Builder(context, TogetherAdSea.idMapGoogle[adConstStr])
            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type)

                val adView = View.inflate(
                    context,
                    R.layout.layout_splash_google,
                    null
                ) as UnifiedNativeAdView
                val mediaView = adView.findViewById<MediaView>(R.id.ad_mediaview)
                adView.mediaView = mediaView

                val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                headlineView.text = ad.headline
                adView.headlineView = headlineView

                val adTimeAction = adView.findViewById<TextView>(R.id.ad_action)

                val countDownTimer = object : CountDownTimer(5000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        adTimeAction.text =
                            context.getString(
                                R.string.time_action,
                                ((millisUntilFinished / 1000) + 1).toString()
                            )
                        adTimeAction.visibility = View.VISIBLE
                        logd("${AdNameType.GOOGLE_ADMOB.type}: 倒计时: $millisUntilFinished")
                    }

                    override fun onFinish() {
                        logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.dismiss)}")
                        adListener.onAdDismissed()
                        ad.destroy()
                    }
                }.start()

                adTimeAction.setOnClickListener {
                    logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.clicked_action)}")
                    countDownTimer.cancel()
                    adListener.onAdDismissed()
                    ad.destroy()
                }

                adView.setNativeAd(ad)
                adsParentLayout.removeAllViews()
                adsParentLayout.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    loge("${AdNameType.GOOGLE_ADMOB.type}: errorCode:$errorCode")
                    val newSplashConfig = splashConfigStr?.replace(AdNameType.GOOGLE_ADMOB.type, AdNameType.NO.type)
                    showAdFull(
                        context,
                        newSplashConfig,
                        adConstStr,
                        adsParentLayout,
                        adListener
                    )
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
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_BOTTOM_RIGHT)
                    .setImageOrientation(NativeAdOptions.ORIENTATION_PORTRAIT)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    /**
     * Facebook
     */
    private fun showAdFullFacebook(
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adsParentLayout: ViewGroup,
        @NonNull adListener: AdListenerSplash
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
                showAdFull(
                    context,
                    newSplashConfig,
                    adConstStr,
                    adsParentLayout,
                    adListener
                )
            }

            override fun onAdLoaded(ad: Ad?) {
                if (nativeAd != ad) {
                    return
                }

                nativeAd.unregisterView()

                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")

                val nativeAdLayout = View.inflate(context, R.layout.layout_splash_facebook, null)
                val adView = nativeAdLayout.findViewById<FrameLayout>(R.id.ad_view)
                adsParentLayout.addView(nativeAdLayout)

                // Add the AdOptionsView
//                val adChoicesContainer = findViewById(R.id.ad_choices_container)
//                val adOptionsView = AdOptionsView(this@NativeAdcontext, nativeAd, nativeAdLayout)
//                adChoicesContainer.removeAllViews()
//                adChoicesContainer.addView(adOptionsView, 0)

                // Create native UI using the ad metadata.
//                val nativeAdIcon = adView.findViewById<AdIconView>(R.id.native_ad_icon)
//                val nativeAdTitle = adView.findViewById(R.id.native_ad_title)
                val nativeAdMedia = adView.findViewById<com.facebook.ads.MediaView>(R.id.native_ad_media)
//                val nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context)
//                val nativeAdBody = adView.findViewById(R.id.native_ad_body)
//                val sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label)
//                val nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action)

                // Set the Text.
//                nativeAdTitle.setText(nativeAd.advertiserName)
//                nativeAdBody.setText(nativeAd.adBodyText)
//                nativeAdSocialContext.setText(nativeAd.adSocialContext)
//                nativeAdCallToAction.setVisibility(if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE)
//                nativeAdCallToAction.setText(nativeAd.adCallToAction)
//                sponsoredLabel.setText(nativeAd.sponsoredTranslation)

                // Create a list of clickable views
                val clickableViews = mutableListOf<View>()
                clickableViews.add(nativeAdMedia)
//                clickableViews.add(nativeAdCallToAction)

                // Register the Title and CTA button to listen for clicks.
                nativeAd.registerViewForInteraction(adView, nativeAdMedia, clickableViews)

                val adTimeAction = adView.findViewById<TextView>(R.id.ad_action)

                val countDownTimer = object : CountDownTimer(5000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        adTimeAction.text =
                            context.getString(
                                R.string.time_action,
                                ((millisUntilFinished / 1000) + 1).toString()
                            )
                        adTimeAction.visibility = View.VISIBLE
                        logd("${AdNameType.FACEBOOK.type}: 倒计时: $millisUntilFinished")
                    }

                    override fun onFinish() {
                        logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.dismiss)}")
                        adListener.onAdDismissed()
                        ad.destroy()
                    }
                }.start()

                adTimeAction.setOnClickListener {
                    logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked_action)}")
                    countDownTimer.cancel()
                    adListener.onAdDismissed()
                    ad.destroy()
                }

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
    interface AdListenerSplash {

        fun onStartRequest(channel: String)

        fun onAdClick(channel: String)

        fun onAdFailed(failedMsg: String?)

        fun onAdDismissed()

        fun onAdPrepared(channel: String)
    }

}