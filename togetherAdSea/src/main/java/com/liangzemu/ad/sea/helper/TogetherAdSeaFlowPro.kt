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
import com.liangzemu.ad.sea.AdBase
import com.liangzemu.ad.sea.R
import com.liangzemu.ad.sea.TogetherAdSea
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.*


/**
 * 原生广告
 *
 * Created by Matthew_Chen on 2019-04-22.
 */
@Deprecated("old", replaceWith = ReplaceWith("FlowHelper"), level = DeprecationLevel.ERROR)
class TogetherAdSeaFlowPro(val adConstStr: String) : AdBase {

    init {
        val any = TogetherAdSea.adCacheMap[adConstStr]
        if (any == null)
            TogetherAdSea.adCacheMap[adConstStr] = ArrayList<Any>()
    }

    companion object {
        /**
         * 存放由activity传递过来的监听器 页面销毁时需要清理 避免内存泄漏
         */
        private val multipleAdListenerFlowMap = HashMap<String, AdListenerFlow>()//HashMap<adConstStr+objectStr,监听器>()
    }

    private var outListener: AdListenerFlow?
        get() = multipleAdListenerFlowMap[adConstStr]
        private set(value) {
            value?.let {
                multipleAdListenerFlowMap[adConstStr] = it
            }
        }

    /**
     * 不区分横向纵向   ！！！！激励展示后如果要删除缓存必须手动调用删除
     * @param splashConfigStr String?
     * @param adListener AdListenerReward 对外的监听器 负责最后回调给用户
     * @return Unit
     */
    fun requestAdFlow(
        splashConfigStr: String?,
        direction: Direction = Direction.HORIZONTAL,
        @NonNull adListener: AdListenerFlow
    ) {

        outListener = adListener
        loge("开始请求")
        if (direction == Direction.HORIZONTAL) {
            showAdFlowHorizontal(splashConfigStr)
        } else {
            showAdFlowVertical(splashConfigStr, object : AdListenerFlow {
                override fun onAdShow(channel: String) {
                    outListener?.onAdShow(channel)
                }

                override fun onAdPrepared(channel: String, ad: Any) {
                    callPrepare(TogetherAdSea.adCacheMap[adConstStr], outListener)
                }

                override fun onStartRequest(channel: String) {
                    outListener?.onStartRequest(channel)
                }

                override fun onAdClick(channel: String) {
                    outListener?.onAdClick(channel)
                }

                override fun onAdFailed(failedMsg: String?) {
                    outListener?.onAdFailed(failedMsg)
                }

            })
        }
    }

    private fun callPrepare(
        ad: Any?,
        adListener: AdListenerFlow?
    ) {
        loge("加载完成或者已有缓存，通知加载情况")
        adListener?.let {
            if (ad != null) {
                if (ad is UnifiedNativeAd) {
                    loge("google:通知完成")
                    it.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, ad)
                } else if (ad is com.facebook.ads.RewardedVideoAd) {
                    loge("facebook:通知完成")
                    it.onAdPrepared(AdNameType.FACEBOOK.type, ad)
                }
            }
        }
    }

    /**
     * 横向切换
     */
    fun showAdFlowHorizontal(
        splashConfigStr: String?
    ) {
        //取最高等级
        val levelCount = Math.max(
            TogetherAdSea.idListGoogleMap[adConstStr]?.size ?: 0,
            TogetherAdSea.idListFacebookMap[adConstStr]?.size ?: 0
        ) - 1
        var level = 0
        TogetherAdSea.loadingAdTask[adConstStr] = levelCount
        loge("total level:$levelCount level:$level start")
        //循环等级请求
        fun showAdFlowByLevel() {

            showAdFlowVertical(splashConfigStr, object : AdListenerFlow {
                override fun onAdShow(channel: String) {
                    outListener?.onAdShow(channel)
                }

                override fun onStartRequest(channel: String) {
                    outListener?.onStartRequest(channel)
                }

                override fun onAdClick(channel: String) {
                    outListener?.onAdClick(channel)
                }

                override fun onAdFailed(failedMsg: String?) {
                    loge("TogetherAdSeaFlowHorizontal: level:$level failed:$failedMsg")
                    if (level >= levelCount) {
                        //加载完成  移除
                        TogetherAdSea.loadingAdTask.remove(adConstStr)
                        outListener?.onAdFailed(failedMsg)
                    } else {
                        level++
                        showAdFlowByLevel()
                    }

                }

                override fun onAdPrepared(channel: String, ad: Any) {
                    loge("TogetherAdSeaFlowHorizontal: level:$level success:$channel")
                    //加载完成  移除
                    TogetherAdSea.loadingAdTask.remove(adConstStr)
                    outListener?.onAdPrepared(channel, ad)
                }

            }, level)

        }
        //开始请求
        showAdFlowByLevel()
    }

    /**
     * 竖向切换
     */
    fun showAdFlowVertical(
        splashConfigStr: String?,
        @NonNull adListener: AdListenerFlow,
        level: Int = -1
    ) {
        if (level == -1) {
            TogetherAdSea.loadingAdTask[adConstStr] = -1
        }
        val randomAdName = AdRandomUtil.getRandomAdName(splashConfigStr)
        loge("splashConfigStr:$splashConfigStr randomAdName:$randomAdName")
        when (randomAdName) {
            AdNameType.GOOGLE_ADMOB -> showAdFlowGoogle(
                level,
                splashConfigStr,
                0,
                adListener
            )
            AdNameType.FACEBOOK -> showAdFlowFacebook(
                level,
                splashConfigStr,
                0,
                adListener
            )
            else -> {
                if (TogetherAdSea.loadingAdTask[adConstStr] == level) {//广告加载完毕并且已经是目标等级了
                    //加载完成  移除
                    TogetherAdSea.loadingAdTask.remove(adConstStr)
                }
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
        level: Int,
        splashConfigStr: String?,
        @NonNull requestIndex: Int,
        @NonNull adListener: AdListenerFlow
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
            showAdFlowVertical(newConfig, adListener, level)
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
                //成功才加载 莫得办法
                //(TogetherAdSea.adCacheMap[adConstStr] as ArrayList<Any>).add(ad)
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    loge("${AdNameType.GOOGLE_ADMOB.type}: errorCode:$errorCode")
                    showAdFlowGoogle(level, splashConfigStr, requestIndex + 1, adListener)
                }

                override fun onAdImpression() {
                    logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.exposure)}")
                    //(TogetherAdSea.adCacheMap[adConstStr] as ArrayList<Any>).remove()
                    adListener.onAdShow(AdNameType.GOOGLE_ADMOB.type)
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
            showAdFlowVertical(newConfig, adListener, level)
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
                showAdFlowFacebook(level, splashConfigStr, requestIndex + 1, adListener)
            }

            override fun onAdLoaded(ad: Ad) {
                if (nativeAd != ad) {
                    loge("${AdNameType.FACEBOOK.type}: 广告返回错误 nativeAd != ad")
                    showAdFlowFacebook(level, splashConfigStr, requestIndex + 1, adListener)
                    return
                }
                (TogetherAdSea.adCacheMap[adConstStr] as ArrayList<Any>).add(ad)
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.FACEBOOK.type, ad)
            }

            override fun onLoggingImpression(ad: Ad) {
                (TogetherAdSea.adCacheMap[adConstStr] as ArrayList<Any>).remove(ad)
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.exposure)}")
                adListener.onAdShow(AdNameType.FACEBOOK.type)
            }
        })
        nativeAd.loadAd()
    }

    /**
     * 用来清除监听器  在activity退出的时候调用
     * @return Unit
     */
    fun clearRewarListener() {
        multipleAdListenerFlowMap.remove(adConstStr)
    }

    fun getloadedList(): List<Any> {
        return (TogetherAdSea.adCacheMap[adConstStr] as ArrayList<Any>)
    }

    /**
     * 监听器
     */
    interface AdListenerFlow {

        fun onStartRequest(channel: String)

        fun onAdClick(channel: String)

        fun onAdFailed(failedMsg: String?)

        fun onAdPrepared(channel: String, ad: Any)

        fun onAdShow(channel: String)
    }

}


