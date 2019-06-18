package com.liangzemu.ad.sea.helper

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
import com.liangzemu.ad.sea.*
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.*


/**
 * 原生广告
 *
 * Created by Matthew_Chen on 2019-04-22.
 */

class FlowHelper(adConstStr: String) : AbstractAdHelp(adConstStr) {
    override fun dispatchAdRequest(
        type: AdNameType,
        level: Int,
        configStr: String?,
        requestIndex: Int,
        adListener: IAdListener
    ) {
        when(type){
            AdNameType.FACEBOOK->{
                showAdFlowFacebook(level,configStr,requestIndex,adListener)
            }
            AdNameType.GOOGLE_ADMOB->{
                showAdFlowGoogle(level,configStr,requestIndex,adListener)
            }
        }
    }
    private fun showAdFlowGoogle(
        level: Int,
        splashConfigStr: String?,
        @NonNull requestIndex: Int,
        @NonNull adListener: IAdListener
    ) {
        /**
         * 分档检测
         */
        val idList =
            if (level != -1) {
                TogetherAdSea.idListGoogleMap[adConstStr]?.filterIndexed { index, _ ->
                    level == index
                }
            } else {
                TogetherAdSea.idListGoogleMap[adConstStr]
            }
        //分档位请求完毕  都没广告
        if (requestIndex >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newConfig = splashConfigStr?.replace(AdNameType.GOOGLE_ADMOB.type, AdNameType.NO.type)
            requestAdVertical(newConfig, adListener, level)
            return
        }
        //检测结束 开始请求
        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(context.getString(R.string.ad_id_no),"ALL")
            return
        }
        val builder = AdLoader.Builder(context, idList[requestIndex])
        val adLoader =builder
            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                //成功才加载 莫得办法
                //(TogetherAdSea.adCacheMap[adConstStr] as ArrayList<Any>).add(ad)
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, AdWrapper(ad,builder.toString()))
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    loge("${AdNameType.GOOGLE_ADMOB.type}: errorCode:$errorCode")
                    showAdFlowGoogle(level, splashConfigStr, requestIndex + 1, adListener)
                }

                override fun onAdImpression() {
                    logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.exposure)}")
                    //(TogetherAdSea.adCacheMap[adConstStr] as ArrayList<Any>).remove()
                    adListener.onAdShow(AdNameType.GOOGLE_ADMOB.type,builder.toString())
                }

                override fun onAdClicked() {
                    logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.clicked)}")
                    adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type,builder.toString())
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()

        adListener.onStartRequest(AdNameType.GOOGLE_ADMOB.type,adLoader.toString())

        adLoader.loadAd(AdRequest.Builder().apply {
            if (TogetherAdSea.testDeviceID != null) addTestDevice(
                TogetherAdSea.testDeviceID
            )
        }.build())
//        adLoader.loadAds(AdRequest.Builder().build(), 3)//最大5
    }
    /**
     * Facebook
     */
    private fun showAdFlowFacebook(
        level: Int = -1,
        splashConfigStr: String?,
        @NonNull requestIndex: Int,
        @NonNull adListener: IAdListener
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
            requestAdVertical(newConfig, adListener, level)
            return
        }
        //检测结束 开始请求
        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(context.getString(R.string.ad_id_no),"ALL")
            return
        }


        val nativeAd = NativeAd(context, idList[requestIndex])
        adListener.onStartRequest(AdNameType.FACEBOOK.type,nativeAd.toString())
        nativeAd.setAdListener(object : NativeAdListener {
            override fun onAdClicked(ad: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.FACEBOOK.type,ad.toString())
            }

            override fun onMediaDownloaded(ad: Ad?) {
            }

            override fun onError(ad: Ad, adError: AdError?) {
                loge("${AdNameType.FACEBOOK.type}: adError:${adError?.errorCode},${adError?.errorMessage}")
                showAdFlowFacebook(level, splashConfigStr, requestIndex + 1, adListener)
            }

            override fun onAdLoaded(ad: Ad) {
                if (nativeAd != ad) {
                    loge("${AdNameType.FACEBOOK.type}: 广告返回错误 nativeAd != ad")
                    showAdFlowFacebook(level, splashConfigStr, requestIndex + 1, adListener)
                    return
                }
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.FACEBOOK.type, AdWrapper(ad,ad.toString()))
            }

            override fun onLoggingImpression(ad: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.exposure)}")
                adListener.onAdShow(AdNameType.FACEBOOK.type,ad.toString())
            }
        })
        nativeAd.loadAd()
    }

    override fun onAdShow(channel: String, key: String) {
        super.onAdShow(channel, key)
        removeAd(key,true)
    }
}


