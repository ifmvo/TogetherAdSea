package com.liangzemu.ad.sea.helper

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.NonNull
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.liangzemu.ad.sea.AdBase
import com.liangzemu.ad.sea.R
import com.liangzemu.ad.sea.TogetherAdSea
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.AdRandomUtil
import com.liangzemu.ad.sea.other.logd
import com.liangzemu.ad.sea.other.loge

/**
 * (●ﾟωﾟ●)暂停插屏：使用大的 Banner 模拟实现播放暂停的插屏
 *
 * Created by Matthew_Chen on 2019-06-04.
 */
object TogetherAdSeaPause : AdBase {

    fun showAdPause(
        @NonNull context: Activity,
        bannerConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adsParentLayout: ViewGroup,
        @NonNull adListener: AdListenerPause
    ) {

        val randomAdName = AdRandomUtil.getRandomAdName(bannerConfigStr)
        when (randomAdName) {
            AdNameType.GOOGLE_ADMOB -> showAdPauseGoogle(
                context,
                bannerConfigStr,
                adConstStr,
                adsParentLayout,
                0,
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
    private fun showAdPauseGoogle(
        @NonNull context: Activity,
        bannerConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adsParentLayout: ViewGroup,
        indexGoogle: Int,
        @NonNull adListener: AdListenerPause
    ) {
        val idList = TogetherAdSea.idListGoogleMap[adConstStr]

        if (indexGoogle >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newBannerConfig = bannerConfigStr?.replace(AdNameType.GOOGLE_ADMOB.type, AdNameType.NO.type)
            showAdPause(context, newBannerConfig, adConstStr, adsParentLayout, adListener)
            return
        }

        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(context.getString(R.string.ad_id_no))
            return
        }

        logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.start_request)}")
        adListener.onStartRequest(AdNameType.GOOGLE_ADMOB.type)

        val mAdView = com.google.android.gms.ads.AdView(context)
        mAdView.adSize = com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE
        mAdView.adUnitId = idList[indexGoogle]
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type)

                val frameLayout = FrameLayout(context)
                val frameParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                frameParams.gravity = Gravity.CENTER
                frameLayout.layoutParams = frameParams

                val closeImageView = ImageView(context)
                val closeParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                closeParams.gravity = Gravity.END
                closeImageView.layoutParams = closeParams
                closeImageView.setImageResource(R.mipmap.ad_close)
                closeImageView.setOnClickListener {
                    adsParentLayout.setBackgroundColor(Color.parseColor("#00000000"))
                    if (adsParentLayout.childCount > 0) {
                        adsParentLayout.removeAllViews()
                    }
                    adsParentLayout.visibility = View.GONE
                }

                frameLayout.addView(mAdView)
                frameLayout.addView(closeImageView)
                adsParentLayout.setBackgroundColor(Color.parseColor("#80000000"))
                adsParentLayout.setOnClickListener {
                    adsParentLayout.setBackgroundColor(Color.parseColor("#00000000"))
                    if (adsParentLayout.childCount > 0) {
                        adsParentLayout.removeAllViews()
                    }
                    adsParentLayout.visibility = View.GONE
                }

                adsParentLayout.visibility = View.VISIBLE
                if (adsParentLayout.childCount > 0) {
                    adsParentLayout.removeAllViews()
                }
                adsParentLayout.addView(frameLayout)
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                loge("${AdNameType.GOOGLE_ADMOB.type}: indexGoogle:$indexGoogle, errorCode:$errorCode")
                val newIndexGoogle = indexGoogle + 1
                showAdPauseGoogle(context, bannerConfigStr, adConstStr, adsParentLayout, newIndexGoogle, adListener)
            }

            override fun onAdClicked() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type)
            }

            override fun onAdImpression() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.exposure)}")
            }
        }
    }

    /**
     * 监听器
     */
    interface AdListenerPause {

        fun onStartRequest(channel: String)

        fun onAdClick(channel: String)

        fun onAdFailed(failedMsg: String?)

        fun onAdPrepared(channel: String)
    }

}