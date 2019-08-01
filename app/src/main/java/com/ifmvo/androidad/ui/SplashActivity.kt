package com.ifmvo.androidad.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ifmvo.androidad.R
import com.ifmvo.androidad.adExtend.SplashAdManagerNew

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-07-04.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val onResult: () -> Unit = {
            MainActivity.action(this)
            finish()
        }

        //在超时时间内请求广告有了结果（ 成功或失败 ）
        SplashAdManagerNew.requestAd(overTimeSecond = 4, onSuccess = onResult, onFailed = onResult)
    }
}