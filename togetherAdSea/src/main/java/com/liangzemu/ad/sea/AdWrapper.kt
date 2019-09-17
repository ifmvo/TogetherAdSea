package com.liangzemu.ad.sea

import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.reward.RewardedVideoAd

class AdWrapper(val realAd:Any,val key:String=realAd.hashCode().toString()) {
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
        }
        listener=null
    }
}