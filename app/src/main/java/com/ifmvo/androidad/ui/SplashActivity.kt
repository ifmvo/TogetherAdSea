package com.ifmvo.androidad.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ifmvo.androidad.R
import com.ifmvo.androidad.adExtend.SplashAdManager

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-07-04.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //在超时时间内请求广告有了结果（ 成功或失败 ）
        SplashAdManager.requestAd(overTimeSecond = 4) {
            MainActivity.action(this)
            finish()
        }
    }
}