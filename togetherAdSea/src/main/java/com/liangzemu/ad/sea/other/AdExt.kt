package com.liangzemu.ad.sea.other

import android.util.Log
import com.liangzemu.ad.sea.AdBase

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2018/12/24.
 */
internal fun AdBase.logd(msg: String?) {
    Log.d("TogetherAdSea", "${this.javaClass.simpleName}: $msg")
}

internal fun AdBase.loge(msg: String?) {
    Log.e("TogetherAdSea", "${this.javaClass.simpleName}: $msg")
}

internal fun AdRandomUtil.logd(msg: String?) {
    Log.d("TogetherAdSea", "${this.javaClass.simpleName}: $msg")
}

internal fun AdRandomUtil.loge(msg: String?) {
    Log.e("TogetherAdSea", "${this.javaClass.simpleName}: $msg")
}