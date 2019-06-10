package com.liangzemu.ad.sea.helper

import android.content.Context
import android.support.annotation.NonNull
import com.facebook.ads.*
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


/**
 * 原生横幅广告  因为仅在facebook有  所以不采用比例，也不需要分方向  仅仅使用分级
 * 
 * Created by Matthew_Chen on 2019-04-22.
 */
object TogetherAdSeaFlowBanner : AdBase {

    fun showAdFlow(
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        onStartRequest:(String,Int)->Unit={ _, _ ->},
        onAdPrepared:(String,Any)->Unit={ _, _ ->},
        onAdFailed:(String)->Unit={}
    ) {

        val randomAdName = AdRandomUtil.getRandomAdName(splashConfigStr)
        when (randomAdName) {
            AdNameType.FACEBOOK -> showAdFlowFacebook(

                context.applicationContext,
                splashConfigStr,
                adConstStr,
                0,
                onStartRequest,
                onAdPrepared,
                onAdFailed
            )
            else -> {
                onAdFailed(context.getString(R.string.all_ad_error))
                loge(context.getString(R.string.all_ad_error))
            }
        }
    }

    /**
     * Facebook
     */
    private fun showAdFlowFacebook(
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull requestIndex:Int,
        onStartRequest: (String, Int) -> Unit,
        onAdPrepared: (String, Any) -> Unit,
        onAdFailed: (String) -> Unit
    ) {
        /**
         * 分档检测
         */
        val idList = TogetherAdSea.idListFacebookMap[adConstStr]
        //分档位请求完毕  都没广告
        if (requestIndex >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newConfig = splashConfigStr?.replace(AdNameType.FACEBOOK.type, AdNameType.NO.type)
            showAdFlow(context, newConfig, adConstStr, onStartRequest, onAdPrepared, onAdFailed)
            return
        }
        //检测结束 开始请求
        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.ad_id_no)}")
            onAdFailed(context.getString(R.string.ad_id_no))
            return
        }
        onStartRequest(AdNameType.FACEBOOK.type,requestIndex)

        val nativeAd = NativeBannerAd(context, idList[requestIndex])
        nativeAd.setAdListener(object : NativeAdListener {
            override fun onAdClicked(ad: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
            }

            override fun onMediaDownloaded(ad: Ad?) {
            }

            override fun onError(ad: Ad?, adError: AdError?) {
                loge("${AdNameType.FACEBOOK.type}: adError:${adError?.errorCode},${adError?.errorMessage}")
                showAdFlowFacebook(context, splashConfigStr, adConstStr,requestIndex+1, onStartRequest, onAdPrepared, onAdFailed)
            }

            override fun onAdLoaded(ad: Ad?) {
                if (nativeAd != ad) {
                    loge("${AdNameType.FACEBOOK.type}: 广告返回错误 nativeAd != ad")
                    showAdFlowFacebook(context, splashConfigStr, adConstStr, requestIndex+1, onStartRequest, onAdPrepared, onAdFailed)
                    return
                }
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")
                onAdPrepared(AdNameType.FACEBOOK.type, ad)
            }

            override fun onLoggingImpression(ad: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.exposure)}")
            }
        })
        nativeAd.loadAd()
    }
}