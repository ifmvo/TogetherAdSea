package com.liangzemu.ad.sea.helper

import android.content.Context
import android.support.annotation.NonNull
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


/**
 * 原生广告
 * 
 * Created by Matthew_Chen on 2019-04-22.
 */

object TogetherAdSeaFlow : AdBase {
    /**
     * 横向切换
     */
    fun showAdFlowHorizontal(
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adListener: TogetherAdSeaFlow.AdListenerFlow
    ){
        //取最高等级
        val levelCount=Math.max(TogetherAdSea.idListGoogleMap[adConstStr]?.size?:0,TogetherAdSea.idListFacebookMap[adConstStr]?.size?:0)
        var level=0
        loge("total level:$levelCount level:$level start")
        //循环等级请求
        fun showAdFlowByLevel(){

            TogetherAdSeaFlow.showAdFlowVertical(context, splashConfigStr, adConstStr, object :TogetherAdSeaFlow.AdListenerFlow{
                override fun onStartRequest(channel: String) {adListener.onStartRequest(channel)}
                override fun onAdClick(channel: String) {adListener.onAdClick(channel)}

                override fun onAdFailed(failedMsg: String?) {
                    loge("TogetherAdSeaFlowHorizontal: level:$level failed:$failedMsg")
                    if(level>=levelCount){
                        adListener.onAdFailed(failedMsg)
                    }else{
                        level++
                        showAdFlowByLevel()
                    }

                }
                override fun onAdPrepared(channel: String, ad: Any) {
                    loge("TogetherAdSeaFlowHorizontal: level:$level success:$channel")
                    adListener.onAdPrepared(channel, ad)
                }

            },level)

        }
        //开始请求
        showAdFlowByLevel()
    }
    /**
     * 竖向切换
     */
    fun showAdFlowVertical(
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull adListener: AdListenerFlow,
        level:Int=-1
    ) {

        val randomAdName = AdRandomUtil.getRandomAdName(splashConfigStr)
        loge("splashConfigStr:$splashConfigStr randomAdName:$randomAdName")
        when (randomAdName) {
            AdNameType.GOOGLE_ADMOB -> showAdFlowGoogle(
                level,
                context.applicationContext,
                splashConfigStr,
                adConstStr,
                0,
                adListener
            )
            AdNameType.FACEBOOK -> showAdFlowFacebook(
                level,
                context.applicationContext,
                splashConfigStr,
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

    /**
     * Google
     * splashConfigStr ：例：google:2,facebook:8
     * adConstStr : 例：TogetherAdConst.AD_SPLASH
     */
    private fun showAdFlowGoogle(
        level:Int,
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull requestIndex:Int,
        @NonNull adListener: AdListenerFlow
    ) {
        /**
         * 分档检测
         */
        val idList =
        if(level!=-1){
            TogetherAdSea.idListGoogleMap[adConstStr]?.filterIndexed { index, _ ->
                level==index
            }
        }else{
            TogetherAdSea.idListGoogleMap[adConstStr]
        }
        //分档位请求完毕  都没广告
        if (requestIndex >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newConfig = splashConfigStr?.replace(AdNameType.GOOGLE_ADMOB.type, AdNameType.NO.type)
            showAdFlowVertical(context, newConfig, adConstStr, adListener,level)
            return
        }
        //检测结束 开始请求
        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(context.getString(R.string.ad_id_no))
            return
        }
        adListener.onStartRequest(AdNameType.GOOGLE_ADMOB.type)
        val adLoader = AdLoader.Builder(context, idList[requestIndex])
            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    loge("${AdNameType.GOOGLE_ADMOB.type}: errorCode:$errorCode")
                    showAdFlowGoogle(level,context, splashConfigStr, adConstStr, requestIndex+1, adListener)
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
        adLoader
//        adLoader.loadAds(AdRequest.Builder().build(), 3)//最大5
    }

    /**
     * Facebook
     */
    private fun showAdFlowFacebook(
        level:Int=-1,
        @NonNull context: Context,
        splashConfigStr: String?,
        @NonNull adConstStr: String,
        @NonNull requestIndex:Int,
        @NonNull adListener: AdListenerFlow
    ) {
        /**
         * 分档检测
         */
        val idList =
            if (level != -1) {
                TogetherAdSea.idListFacebookMap[adConstStr]?.filterIndexed { index, _ ->
                    level == index
                }
            } else {
                TogetherAdSea.idListFacebookMap[adConstStr]
            }
        //分档位请求完毕  都没广告
        if (requestIndex >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newConfig = splashConfigStr?.replace(AdNameType.FACEBOOK.type, AdNameType.NO.type)
            showAdFlowVertical(context, newConfig, adConstStr, adListener,level)
            return
        }
        //检测结束 开始请求
        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(context.getString(R.string.ad_id_no))
            return
        }
        adListener.onStartRequest(AdNameType.FACEBOOK.type)

        val nativeAd = NativeAd(context, idList[requestIndex])
        nativeAd.setAdListener(object : NativeAdListener {
            override fun onAdClicked(ad: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
            }

            override fun onMediaDownloaded(ad: Ad?) {
            }

            override fun onError(ad: Ad?, adError: AdError?) {
                loge("${AdNameType.FACEBOOK.type}: adError:${adError?.errorCode},${adError?.errorMessage}")
                showAdFlowFacebook(level,context, splashConfigStr, adConstStr, requestIndex+1, adListener)
            }

            override fun onAdLoaded(ad: Ad?) {
                if (nativeAd != ad) {
                    loge("${AdNameType.FACEBOOK.type}: 广告返回错误 nativeAd != ad")
                    showAdFlowFacebook(level,context, splashConfigStr, adConstStr, requestIndex+1, adListener)
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


