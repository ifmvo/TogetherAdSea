package com.liangzemu.ad.sea.helper

import androidx.annotation.NonNull
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.liangzemu.ad.sea.*
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.logd
import com.liangzemu.ad.sea.other.loge


/**
 * (●ﾟωﾟ●) 激励视频广告：已实现档位
 *
 * Created by Matthew_Chen on 2019-06-05.
 */
class RewardHelper(adConstStr: String) : AbstractAdHelp(adConstStr) {
    /**
     * 比例随机完 开始请求广告
     * @param type AdNameType
     * @param level Int
     * @param configStr String?
     * @param requestIndex Int
     * @param adListener IAdListener
     * @return Unit
     */
    override fun dispatchAdRequest(type:AdNameType,level: Int, configStr: String?, requestIndex: Int, adListener: IAdListener) {
        when(type){
            AdNameType.FACEBOOK->{
                requestAdRewardFacebook(level,configStr,requestIndex,adListener)
            }
            AdNameType.GOOGLE_ADMOB->{
                requestAdRewardGoogle(level,configStr,requestIndex,adListener)
            }
        }
    }

    /**
     * 请求谷歌广告
     * @param level Int
     * @param rewardConfigStr String?
     * @param indexGoogle Int
     * @param adListener IAdListener
     * @return Unit
     */
    private fun requestAdRewardGoogle(
        level: Int,
        rewardConfigStr: String?,
        indexGoogle: Int,
        @NonNull adListener: IAdListener
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
            requestAdVertical(newRewardConfig, adListener,level)
            return
        }
        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(context.getString(R.string.ad_id_no),"ALL")
            return
        }

        logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.start_request)}")


        val mRewardedVideoAdGoogle = MobileAds.getRewardedVideoAdInstance(context)
        //添加监听器
        adListener.onStartRequest(AdNameType.GOOGLE_ADMOB.type,mRewardedVideoAdGoogle.toString())

        mRewardedVideoAdGoogle.rewardedVideoAdListener = object :RewardedVideoAdListener {
            var rewarded=false
            override fun onRewardedVideoAdClosed() {
                adListener.onAdClose(AdNameType.GOOGLE_ADMOB.type,mRewardedVideoAdGoogle.toString(),rewarded)
            }

            override fun onRewardedVideoAdLeftApplication() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type,mRewardedVideoAdGoogle.toString())

            }

            override fun onRewardedVideoAdLoaded() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type,AdWrapper(mRewardedVideoAdGoogle))
            }

            override fun onRewardedVideoAdOpened() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.show)}")
                adListener.onAdShow(AdNameType.GOOGLE_ADMOB.type,mRewardedVideoAdGoogle.toString())

            }

            override fun onRewardedVideoCompleted() {
                logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.complete)}")
                rewarded = true
            }

            override fun onRewarded(p0: RewardItem?) {
            }

            override fun onRewardedVideoStarted() {
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {

                loge("${AdNameType.GOOGLE_ADMOB.type}: indexGoogle:$indexGoogle, errorCode:$p0")
                val newIndexGoogle = indexGoogle + 1
                requestAdRewardGoogle(level, rewardConfigStr, newIndexGoogle, adListener)
                removeListener(mRewardedVideoAdGoogle.toString())
            }
        }
        mRewardedVideoAdGoogle.loadAd(idList[indexGoogle], AdRequest.Builder().apply {
            if (TogetherAdSea.testDeviceID != null) addTestDevice(
                TogetherAdSea.testDeviceID
            )
        }.build())
    }

    /**
     * 请求facebook广告
     * @param level Int
     * @param rewardConfigStr String?
     * @param indexFacebook Int
     * @param adListener IAdListener
     * @return Unit
     */
    private fun requestAdRewardFacebook(
        level: Int,
        rewardConfigStr: String?,
        indexFacebook: Int,
        @NonNull adListener: IAdListener
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
            requestAdVertical(newRewardConfig, adListener,level)
            return
        }

        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(context.getString(R.string.ad_id_no),"ALL")
            return
        }

        logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.start_request)}")
        val mRewardedVideoAdFacebook = com.facebook.ads.RewardedVideoAd(context, idList[indexFacebook])

        adListener.onStartRequest(AdNameType.FACEBOOK.type,mRewardedVideoAdFacebook.toString())

        mRewardedVideoAdFacebook.setAdListener(object : com.facebook.ads.RewardedVideoAdListener{
            var isRewarded=false
            override fun onRewardedVideoClosed() {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.dismiss)}")
                adListener.onAdClose(AdNameType.FACEBOOK.type,mRewardedVideoAdFacebook.toString(), isRewarded)
            }

            override fun onAdClicked(p0: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.FACEBOOK.type,p0.toString())
            }

            override fun onRewardedVideoCompleted() {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.complete)}")
                isRewarded = true
            }

            override fun onError(p0: Ad, adError: AdError?) {

                loge("${AdNameType.FACEBOOK.type}: indexFacebook:$indexFacebook, errorCode:${adError?.errorCode} ${adError?.errorMessage}")
                val newIndexFacebook = indexFacebook + 1
                requestAdRewardFacebook(level, rewardConfigStr, newIndexFacebook, adListener)

                removeListener(mRewardedVideoAdFacebook.toString())

            }

            override fun onAdLoaded(p0: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.FACEBOOK.type,AdWrapper(p0))
            }

            override fun onLoggingImpression(p0: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.show)}")
                adListener.onAdShow(AdNameType.FACEBOOK.type,p0.toString())
            }
        })
        TogetherAdSea.adCacheMap[adConstStr] = mRewardedVideoAdFacebook
        mRewardedVideoAdFacebook.loadAd()

    }

    /**
     * 关闭后销毁广告
     * @param channel String
     * @param key String
     * @param other Any
     * @return Unit
     */
    override fun onAdClose(channel: String, key: String, other: Any) {
        super.onAdClose(channel, key, other)
        removeAd(key,true)
    }
}