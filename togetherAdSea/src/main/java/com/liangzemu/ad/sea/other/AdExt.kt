package com.liangzemu.ad.sea.other

import android.util.Log

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2018/12/24.
 */
internal fun Any.loge(msg: String?) {
    Log.e("TogetherAdSeaInfo",  "${this.javaClass.simpleName}: $msg")
}

internal fun Any.logd(msg: String?) {
    Log.e("TogetherAdSeaInfo",  "${this.javaClass.simpleName}: $msg")
}