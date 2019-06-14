package com.liangzemu.ad.sea.helper

import android.support.annotation.NonNull
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.NativeAdListener
import com.facebook.ads.NativeBannerAd
import com.liangzemu.ad.sea.AdBase
import com.liangzemu.ad.sea.R
import com.liangzemu.ad.sea.TogetherAdSea
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.AdRandomUtil
import com.liangzemu.ad.sea.other.logd
import com.liangzemu.ad.sea.other.loge


/**
 * 原生横幅广告  因为仅在facebook有  所以不采用比例，也不需要分方向  仅仅使用分级
 *
 * Created by Matthew_Chen on 2019-04-22.
 */
class TogetherAdSeaFlowBanner(private val adConstStr: String) : AdBase {
    init {
        val any = TogetherAdSea.adCacheMap[adConstStr]
        if (any == null)
            TogetherAdSea.adCacheMap[adConstStr] = ArrayList<NativeBannerAd>()
    }

    companion object {
        /**
         * 存放由activity传递过来的监听器 页面销毁时需要清理 避免内存泄漏
         */
        private val multipleAdListenerFlowBannerMap = HashMap<String, NativeAdListener>()//HashMap<adConstStr,监听器>()
    }

    private var outListener: NativeAdListener?
        get() = multipleAdListenerFlowBannerMap[adConstStr]
        private set(value) {
            value?.let {
                multipleAdListenerFlowBannerMap[adConstStr] = it
            }
        }

    fun showAdFlow(
        splashConfigStr: String?,
        adListener: NativeAdListener?
    ) {
        loge("原生横幅已加载数量：${(TogetherAdSea.adCacheMap[adConstStr] as ArrayList<NativeBannerAd>).size}")
        outListener = adListener
        val randomAdName = AdRandomUtil.getRandomAdName(splashConfigStr)
        when (randomAdName) {
            AdNameType.FACEBOOK -> showAdFlowFacebook(

                splashConfigStr,
                0
            )
            else -> {
                outListener?.onError(null, AdError(4396, context.getString(R.string.ad_id_no)))
                loge(context.getString(R.string.all_ad_error))
            }
        }
    }

    /**
     * Facebook
     */
    private fun showAdFlowFacebook(
        splashConfigStr: String?,
        @NonNull requestIndex: Int
    ) {
        /**
         * 分档检测
         */
        val idList = TogetherAdSea.idListFacebookMap[adConstStr]
        //分档位请求完毕  都没广告
        if (requestIndex >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newConfig = splashConfigStr?.replace(AdNameType.FACEBOOK.type, AdNameType.NO.type)
            showAdFlow(newConfig, outListener)
            return
        }
        //检测结束 开始请求
        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.ad_id_no)}")
            outListener?.onError(null, AdError(4396, context.getString(R.string.ad_id_no)))
            return
        }

        val nativeAd = NativeBannerAd(context, idList[requestIndex])
        nativeAd.setAdListener(object : NativeAdListener {
            override fun onAdClicked(ad: Ad?) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
                outListener?.onAdClicked(ad)
            }

            override fun onMediaDownloaded(ad: Ad?) {
            }

            override fun onError(ad: Ad?, adError: AdError?) {
                loge("${AdNameType.FACEBOOK.type}: adError:${adError?.errorCode},${adError?.errorMessage}")
                showAdFlowFacebook(splashConfigStr, requestIndex + 1)
            }

            override fun onAdLoaded(ad: Ad) {
                if (nativeAd != ad) {
                    loge("${AdNameType.FACEBOOK.type}: 广告返回错误 nativeAd != ad")
                    showAdFlowFacebook(splashConfigStr, requestIndex + 1)
                    return
                }
                (TogetherAdSea.adCacheMap[adConstStr] as ArrayList<NativeBannerAd>).add(ad as NativeBannerAd)
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")
                outListener?.onAdLoaded(ad)
            }

            override fun onLoggingImpression(ad: Ad) {
                (TogetherAdSea.adCacheMap[adConstStr] as ArrayList<NativeBannerAd>).remove(ad)
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.exposure)}")
                outListener?.onLoggingImpression(ad)
            }
        })
        nativeAd.loadAd()
    }

    /**
     * 用来清除监听器  在activity退出的时候调用
     * @return Unit
     */
    fun clearRewarListener() {
        multipleAdListenerFlowBannerMap.remove(adConstStr)
    }

    fun getloadedList(): List<NativeBannerAd> {
        return (TogetherAdSea.adCacheMap[adConstStr] as ArrayList<NativeBannerAd>)
    }
}