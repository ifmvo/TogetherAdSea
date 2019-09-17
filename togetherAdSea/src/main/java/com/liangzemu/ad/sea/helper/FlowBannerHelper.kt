package com.liangzemu.ad.sea.helper

import android.os.CountDownTimer
import com.liangzemu.ad.sea.BaseAdHelp
import com.liangzemu.ad.sea.IAdListener
import com.liangzemu.ad.sea.TogetherAdSea
import com.liangzemu.ad.sea.other.AdNameType


/**
 * 原生横幅广告  因为仅在facebook有  所以不采用比例，也不需要分方向  仅仅使用分级
 *
 * Created by Matthew_Chen on 2019-04-22.
 */
class FlowBannerHelper(adConstStr: String,  timeOutMillsecond:Long= TogetherAdSea.timeoutMillsecond,  owner:String=adConstStr) : BaseAdHelp(adConstStr,timeOutMillsecond,owner) {
    override fun initAD(id: String, adNameType: AdNameType): Pair<Any, String> {
        return when(adNameType){
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
        errorCallback("Delete FB")
    }
}