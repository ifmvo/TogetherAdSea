package com.liangzemu.ad.sea.other

import android.util.Log
import androidx.annotation.NonNull
import com.liangzemu.ad.sea.TogetherAdSea

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2018/12/24.
 */
internal fun addOneLog(value: String) {
    TogetherAdSeaSP.getInstance(TogetherAdSea.mContext).putString(System.currentTimeMillis().toString(), value)
}

internal fun Any.loge(@NonNull msg: String) {
    Log.e("TogetherAdSeaInfo", "${this.javaClass.simpleName}: $msg")
    addOneLog("${this.javaClass.simpleName}: $msg")
}

internal fun Any.logd(@NonNull msg: String) {
    Log.d("TogetherAdSeaInfo", "${this.javaClass.simpleName}: $msg")
    addOneLog("${this.javaClass.simpleName}: $msg")
}
