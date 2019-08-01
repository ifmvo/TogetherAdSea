package com.liangzemu.ad.sea.helper

import android.os.CountDownTimer
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.NativeAdListener
import com.facebook.ads.NativeBannerAd
import com.liangzemu.ad.sea.*
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.AdNameType
import com.liangzemu.ad.sea.other.logi


/**
 * 原生横幅广告  因为仅在facebook有  所以不采用比例，也不需要分方向  仅仅使用分级
 *
 * Created by Matthew_Chen on 2019-04-22.
 */
class FlowBannerHelper(adConstStr: String,  timeOutMillsecond:Long= TogetherAdSea.timeoutMillsecond,  owner:String=adConstStr) : BaseAdHelp(adConstStr,timeOutMillsecond,owner) {
    override fun initAD(id: String, adNameType: AdNameType): Pair<Any, String> {
        return when(adNameType){
            AdNameType.FACEBOOK->{
                val ad = NativeBannerAd(context, id)
                Pair(ad,ad.hashCode().toString())
            }
            else ->{
                throw IllegalArgumentException("没有此广告类型:${adNameType.type}")
            }
        }
    }

    override fun setGoogleAdListenerAndStart(
        id: String,
        adOrBuilder: Any,
        adListener: IAdListener,
        timer: CountDownTimer,
        errorCallback: (String?) -> Unit
    ) {

    }

    override fun setFaceBookAdListenerAndStart(
        adOrBuilder: Any,
        adListener: IAdListener,
        timer: CountDownTimer,
        errorCallback: (String?) -> Unit
    ) {
        adOrBuilder as NativeBannerAd
        adOrBuilder.setAdListener(object : NativeAdListener {
            override fun onAdClicked(ad: Ad) {
                logi("${AdNameType.FACEBOOK.type}:$adConstStr ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.FACEBOOK.type,ad.hashCode().toString())
            }

            override fun onMediaDownloaded(ad: Ad?) {
            }

            override fun onError(ad: Ad?, adError: AdError?) {
                errorCallback(adError?.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                logi("${AdNameType.FACEBOOK.type} $adConstStr:$adConstStr ${context.getString(R.string.prepared)}")
                timer.cancel()
                adListener.onAdPrepared(AdNameType.FACEBOOK.type, AdWrapper(ad,ad.hashCode().toString()))
            }

            override fun onLoggingImpression(ad: Ad) {
                logi("${AdNameType.FACEBOOK.type} :$adConstStr ${context.getString(R.string.exposure)}")
                adListener.onAdShow(AdNameType.FACEBOOK.type,ad.hashCode().toString())
            }
        })
        adOrBuilder.loadAd()
    }
}