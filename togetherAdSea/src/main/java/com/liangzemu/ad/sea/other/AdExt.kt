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
    Log.d("TogetherAdSeaInfo",  "${this.javaClass.simpleName}: $msg")
}
internal fun Any.logw(msg: String?) {
    Log.v("TogetherAdSeaInfo",  "${this.javaClass.simpleName}: $msg")
}
internal fun Any.logi(msg: String?) {
    Log.i("TogetherAdSeaInfo",  "${this.javaClass.simpleName}: $msg")
}
internal fun Any.logv(msg: String?) {
    Log.v("TogetherAdSeaInfo",  "${this.javaClass.simpleName}: $msg")
}