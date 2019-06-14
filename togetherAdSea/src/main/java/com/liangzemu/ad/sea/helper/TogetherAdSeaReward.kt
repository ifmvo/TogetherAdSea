package com.liangzemu.ad.sea.helper

import androidx.annotation.NonNull
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.liangzemu.ad.sea.AdBase
import com.liangzemu.ad.sea.R
import com.liangzemu.ad.sea.TogetherAdSea
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.*


/**
 * (●ﾟωﾟ●) 激励视频广告：已实现档位
 *
 * Created by Matthew_Chen on 2019-06-05.
 */
class TogetherAdSeaReward(val adConstStr: String) : AdBase {
    companion object {
        /**
         * 存放由activity传递过来的监听器 页面销毁时需要清理 避免内存泄漏
         */
        private val multipleRewarListenerMap = HashMap<String, MultipleRewarListener>()//HashMap<adConstStr,监听器>()
    }

    private var outListener: MultipleRewarListener?
        get() = multipleRewarListenerMap[adConstStr]
        private set(value) {
            value?.let {
                multipleRewarListenerMap[adConstStr] = it
            }
        }
    /**
     * google:RewardedVideoAd
     * facebook:RewardedVideoAd
     */
    /**
     * 不区分横向纵向   ！！！！激励展示后如果要删除缓存必须手动调用删除
     * @param splashConfigStr String?
     * @param adListener AdListenerReward 对外的监听器 负责最后回调给用户
     * @return Unit
     */
    fun requestAdReward(
        splashConfigStr: String?,
        direction: Direction = Direction.HORIZONTAL,
        @NonNull adListener: MultipleRewarListener
    ) {
        outListener = adListener
        //这里需要更换监听器
        if (TogetherAdSea.loadingAdTask[adConstStr] != null) {//已经在加载中  就不再请求了
            loge("已经在加载中 请等待")
            return
        } else {

            val ad = TogetherAdSea.adCacheMap[adConstStr]
            //如果已经存在缓存  则更新监听器  否则重新请求
            if (ad != null) {
                loge("已经有缓存了")
                setFinalListenerAndPrepare(ad, adListener)
                return
            }
        }
        loge("开始请求")
        if (direction == Direction.HORIZONTAL) {
            requestAdRewardHorizontal(splashConfigStr)
        } else {
            requestAdRewardVertical(splashConfigStr, object : MultipleRewarListener() {
                override fun onStartRequest(channel: String) {
                    outListener?.onStartRequest(channel)
                }

                override fun onAdClick(channel: String) {
                    outListener?.onAdClick(channel)
                }

                override fun onAdFailed(failedMsg: String?) {
                    outListener?.onAdFailed(failedMsg)
                }

                override fun onAdShow(channel: String) {
                    outListener?.onAdShow(channel)
                }

                override fun onAdClose(channel: String, isReward: Boolean) {
                    outListener?.onAdClose(channel, isReward)
                }

                override fun onAdPrepared(channel: String) {
                    setFinalListenerAndPrepare(TogetherAdSea.adCacheMap[adConstStr], outListener)
                }
            })
        }
    }

    private fun setFinalListenerAndPrepare(
        ad: Any?,
        adListener: MultipleRewarListener?
    ) {
        loge("加载完成或者已有缓存，更新监听器并通知完成")
        adListener?.let {
            if (ad != null) {
                if (ad is RewardedVideoAd) {
                    loge("google:更新监听器并通知完成")
                    ad.rewardedVideoAdListener = adListener
                    it.onAdPrepared(AdNameType.GOOGLE_ADMOB.type)
                } else if (ad is com.facebook.ads.RewardedVideoAd) {
                    loge("facebook:更新监听器并通知完成")
                    ad.setAdListener(adListener)
                    it.onAdPrepared(AdNameType.FACEBOOK.type)
                }
            }
        }
    }

    /**
     * 横向切换
     */
    private fun requestAdRewardHorizontal(
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
        fun requestAdRewardByLevel() {

            requestAdRewardVertical(splashConfigStr, object : MultipleRewarListener() {
                override fun onAdShow(channel: String) {
                    outListener?.onAdShow(channel)
                }

                override fun onAdClose(channel: String, isReward: Boolean) {
                    outListener?.onAdClose(channel, isReward)
                }

                override fun onAdPrepared(channel: String) {
                    loge("TogetherAdSeaRewardVertical: level:$level success:$channel")
                    //加载完成  移除
                    setFinalListenerAndPrepare(TogetherAdSea.adCacheMap[adConstStr], outListener)

                }

                override fun onStartRequest(channel: String) {
                    outListener?.onStartRequest(channel)
                }

                override fun onAdClick(channel: String) {
                    outListener?.onAdClick(channel)
                }

                override fun onAdFailed(failedMsg: String?) {
                    loge("TogetherAdSeaRewardVertical: level:$level failed:$failedMsg")
                    if (level >= levelCount) {
                        //加载完成  移除
                        TogetherAdSea.loadingAdTask.remove(adConstStr)

                        outListener?.onAdFailed(failedMsg)
                    } else {
                        level++
                        requestAdRewardByLevel()
                    }

                }

            }, level)

        }
        //开始请求
        requestAdRewardByLevel()
    }

    /**
     * 竖向切换 请求激励广告 根据rewardConfigStr随机比例
     * @param rewardConfigStr String?  表示google和facebook广告的比例  当某一部分广告请求失败后  将该部分的key置为no
     * @param adListener AdListenerReward 监听器
     * @param level Int 目前轮次等级  只有横向切换的时候需要  竖向切换默认为-1
     * @return Unit
     */
    private fun requestAdRewardVertical(
        rewardConfigStr: String?,
        @NonNull adListener: AdListenerReward,
        level: Int = -1
    ) {
        if (level == -1) {
            TogetherAdSea.loadingAdTask[adConstStr] = -1
        }
        val randomAdName = AdRandomUtil.getRandomAdName(rewardConfigStr)
        when (randomAdName) {
            AdNameType.GOOGLE_ADMOB -> requestAdRewardGoogle(
                level,
                rewardConfigStr,
                0,
                adListener
            )
            AdNameType.FACEBOOK -> requestAdRewardFacebook(
                level,
                rewardConfigStr,
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

    private fun requestAdRewardGoogle(
        level: Int,
        rewardConfigStr: String?,
        indexGoogle: Int,
        @NonNull adListener: AdListenerReward
    ) {
        val idList = if (level != -1) {
            TogetherAdSea.idListGoogleMap[adConstStr]?.filterIndexed { index, _ ->
                level == index
            }
        } else {
            TogetherAdSea.idListGoogleMap[adConstStr]
        }
        //分档位请求完毕  都没广告
        if (indexGoogle >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newRewardConfig = rewardConfigStr?.replace(AdNameType.GOOGLE_ADMOB.type, AdNameType.NO.type)
            requestAdRewardVertical(newRewardConfig, adListener, level)
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

        val mRewardedVideoAdGoogle = MobileAds.getRewardedVideoAdInstance(context)
        mRewardedVideoAdGoogle.rewardedVideoAdListener = object : MultipleRewarListener() {
            override fun onStartRequest(channel: String) {}

            override fun onAdClick(channel: String) {
                adListener.onAdClick(channel)
            }

            override fun onAdFailed(failedMsg: String?) {
                destoryAd()
                loge("${AdNameType.FACEBOOK.type}: indexGoogle:$indexGoogle, errorCode:$failedMsg")
                val newIndexGoogle = indexGoogle + 1
                requestAdRewardGoogle(level, rewardConfigStr, newIndexGoogle, adListener)
            }

            override fun onAdShow(channel: String) {
                adListener.onAdShow(channel)
            }

            override fun onAdClose(channel: String, isReward: Boolean) {
                adListener.onAdClose(channel, isRewarded)
            }

            override fun onAdPrepared(channel: String) {
                TogetherAdSea.loadingAdTask.remove(adConstStr)
                adListener.onAdPrepared(channel)
            }
        }
        //先存起来，加载失败就移除
        TogetherAdSea.adCacheMap[adConstStr] = mRewardedVideoAdGoogle
        mRewardedVideoAdGoogle.loadAd(idList[indexGoogle], AdRequest.Builder().apply {
            if (TogetherAdSea.testDeviceID != null) addTestDevice(
                TogetherAdSea.testDeviceID
            )
        }.build())
    }

    private fun requestAdRewardFacebook(
        level: Int,
        rewardConfigStr: String?,
        indexFacebook: Int,
        @NonNull adListener: AdListenerReward
    ) {

        val idList = if (level != -1) {
            TogetherAdSea.idListFacebookMap[adConstStr]?.filterIndexed { index, _ ->
                level == index
            }
        } else {
            TogetherAdSea.idListFacebookMap[adConstStr]
        }

        if (indexFacebook >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newRewardConfig = rewardConfigStr?.replace(AdNameType.FACEBOOK.type, AdNameType.NO.type)
            requestAdRewardVertical(newRewardConfig, adListener, level)
            return
        }

        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(context.getString(R.string.ad_id_no))
            return
        }

        logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.start_request)}")
        adListener.onStartRequest(AdNameType.FACEBOOK.type)
        val mRewardedVideoAdFacebook = com.facebook.ads.RewardedVideoAd(context, idList[indexFacebook])
        mRewardedVideoAdFacebook.setAdListener(object : MultipleRewarListener() {
            override fun onStartRequest(channel: String) {}

            override fun onAdClick(channel: String) {
                adListener.onAdClick(channel)
            }

            override fun onAdFailed(failedMsg: String?) {
                destoryAd()
                loge("${AdNameType.FACEBOOK.type}: indexFacebook:$indexFacebook, errorMsg:$failedMsg")
                val newIndexFacebook = indexFacebook + 1
                requestAdRewardFacebook(level, rewardConfigStr, newIndexFacebook, adListener)
            }

            override fun onAdShow(channel: String) {
                logd("$channel: ${context.getString(R.string.show)}")
                adListener.onAdShow(channel)
            }

            override fun onAdClose(channel: String, isReward: Boolean) {
                adListener.onAdClose(channel, isRewarded)
            }

            override fun onAdPrepared(channel: String) {
                TogetherAdSea.loadingAdTask.remove(adConstStr)
                logd("$channel: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(channel)
            }
        })
        TogetherAdSea.adCacheMap[adConstStr] = mRewardedVideoAdFacebook
        mRewardedVideoAdFacebook.loadAd()

    }

    /**
     * 用来清除监听器  在activity退出的时候调用
     * @return Unit
     */
    fun clearRewarListener() {
        multipleRewarListenerMap.remove(adConstStr)
        val ad = TogetherAdSea.adCacheMap[adConstStr]
        if (ad is RewardedVideoAd?) {
            if (ad?.isLoaded == true) {
                ad?.rewardedVideoAdListener = null
            }

        } else if (ad is com.facebook.ads.RewardedVideoAd?) {
            if (ad?.isAdLoaded == true) {
                ad?.setAdListener(null)
            }
        }
    }

    /**
     * 判断是否已经加载/缓存 且未过期
     * @return Boolean
     */
    fun isLoaded(): Boolean {
        val ad = TogetherAdSea.adCacheMap[adConstStr]
        if (ad is RewardedVideoAd?) {
            return ad?.isLoaded == true
        } else if (ad is com.facebook.ads.RewardedVideoAd?) {
            return ad?.isAdLoaded == true && ad?.isAdInvalidated == false
        }
        return false
    }

    /**
     * 销毁广告  仅仅在广告加载失败时调用
     * @return Unit
     */
    private fun destoryAd() {
        TogetherAdSea.loadingAdTask.remove(adConstStr)
        val ad = TogetherAdSea.adCacheMap[adConstStr]
        if (ad is RewardedVideoAd?) {
            ad?.destroy(context)
        } else if (ad is com.facebook.ads.RewardedVideoAd?) {
            ad?.destroy()
        }
        TogetherAdSea.adCacheMap.remove(adConstStr)
    }

    /**
     * 销毁广告及其所有监听器  仅仅在广告展示后进行调用
     * @return Unit
     */
    fun destoryAdAndListener() {
        destoryAd()
        multipleRewarListenerMap.remove(adConstStr)
    }

    /**
     * 显示广告
     * @return Boolean 是否显示成功  如果返回false  需要重新loadAd
     */
    fun showAdReward(): Boolean {
        val loaded = isLoaded()
        if (!loaded)
            return loaded
        val ad = TogetherAdSea.adCacheMap[adConstStr]
        if (ad is RewardedVideoAd?) {
            ad?.show()
        } else if (ad is com.facebook.ads.RewardedVideoAd?) {
            ad?.show()
        }
        return true
    }

    /**
     * 实现三个监听器  并进行合并导向
     * @property rewardDelete Boolean 激励展示后删除
     * @property isRewarded Boolean
     * @constructor
     */
    abstract class MultipleRewarListener : AdListenerReward, com.facebook.ads.RewardedVideoAdListener,
        RewardedVideoAdListener {
        var isRewarded = false
        override fun onRewardedVideoClosed() {
            loge("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.dismiss)}")
            onAdClose(AdNameType.FACEBOOK.type, isRewarded)
        }

        override fun onAdClicked(p0: Ad?) {
            loge("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
            onAdClick(AdNameType.FACEBOOK.type)
        }

        override fun onError(p0: Ad?, p1: AdError?) {
            loge("${AdNameType.FACEBOOK.type}: errorCode:${p1?.errorCode} ${p1?.errorMessage}")
            onAdFailed(p1?.errorMessage ?: "")
        }

        override fun onAdLoaded(p0: Ad?) {
            onAdPrepared(AdNameType.FACEBOOK.type)
        }

        override fun onLoggingImpression(p0: Ad?) {
            loge("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.show)}")
            onAdShow(AdNameType.FACEBOOK.type)
        }

        //分割线

        override fun onRewardedVideoCompleted() {
            loge(context.getString(R.string.complete))
            isRewarded = true
        }
        override fun onRewardedVideoAdClosed() {
            loge("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.dismiss)} $isRewarded")
            onAdClose(AdNameType.GOOGLE_ADMOB.type, isRewarded)
        }

        override fun onRewardedVideoAdLeftApplication() {
            loge("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.clicked)}")
            onAdClick(AdNameType.GOOGLE_ADMOB.type)
        }

        override fun onRewardedVideoAdLoaded() {
            loge("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
            onAdPrepared(AdNameType.GOOGLE_ADMOB.type)
        }

        override fun onRewardedVideoAdOpened() {
            loge("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.show)}")
            onAdShow(AdNameType.GOOGLE_ADMOB.type)
        }

        override fun onRewarded(p0: RewardItem?) {
        }

        override fun onRewardedVideoStarted() {
        }

        override fun onRewardedVideoAdFailedToLoad(p0: Int) {
            loge("${AdNameType.GOOGLE_ADMOB.type}: errorCode:$p0")
            onAdFailed(p0.toString())
        }

    }

    /**
     * 监听器
     */
    interface AdListenerReward {

        //开始请求
        fun onStartRequest(channel: String)

        //点击了
        fun onAdClick(channel: String)

        //失败了
        fun onAdFailed(failedMsg: String?)

        //展示了
        fun onAdShow(channel: String)

        //关闭了
        fun onAdClose(channel: String, isReward: Boolean)

        //准备好了
        fun onAdPrepared(channel: String)
    }
}