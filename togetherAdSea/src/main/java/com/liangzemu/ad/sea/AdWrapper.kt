package com.liangzemu.ad.sea

import com.facebook.ads.NativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.reward.RewardedVideoAd

class AdWrapper(val realAd:Any,val key:String=realAd.toString()) {
    var showedTime=0L
    private var listener:IAdListener?=null
    private var listenerOwner:String?=null

    fun setListener(listener:IAdListener?,listenerOwner:String){
        this.listener=listener
        this.listenerOwner=listenerOwner
    }
    fun getListener():IAdListener?{
        return listener
    }
    fun resetListener(){
        this.listener=null
    }
    fun getOwner():String?{
        return listenerOwner
    }
    fun destory(){
        when(realAd){
            is RewardedVideoAd->realAd.destroy(TogetherAdSea.context)
            is UnifiedNativeAd ->realAd.destroy()


            is com.facebook.ads.RewardedVideoAd->realAd.destroy()
            is NativeAd ->realAd.destroy()
        }
        listener=null
    }
}