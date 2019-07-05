package com.ifmvo.androidad.ad

import android.util.Log
import com.ifmvo.androidad.BuildConfig

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-06-26.
 */
fun Any.loge(tag: String, msg: String) {
    if (BuildConfig.DEBUG)
        Log.e(tag, msg)
}

fun Any.logd(tag: String, msg: String) {
    if (BuildConfig.DEBUG)
        Log.d(tag, msg)
}