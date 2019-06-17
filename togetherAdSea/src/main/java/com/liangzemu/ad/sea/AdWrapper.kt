package com.liangzemu.ad.sea

import com.google.android.gms.ads.reward.RewardedVideoAd

class AdWrapper(val realAd:Any,val key:String=realAd.toString()) {
    internal fun destory(){
        when(realAd){
            is RewardedVideoAd->realAd.destroy(TogetherAdSea.context)
            is com.facebook.ads.RewardedVideoAd->realAd.destroy()
        }
    }
}