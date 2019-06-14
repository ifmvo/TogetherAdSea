package com.ifmvo.androidad

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        Thread {
//            val advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)
//            Log.e("ifmvo", advertisingIdInfo.id)
//        }.start()

//        TogetherAdSeaSplash.showAdFull(
//            this,
//            Config.splashAdConfig(),
//            TogetherAdConst.AD_SPLASH,
//            mFlAdContainer,
//            object : TogetherAdSeaSplash.AdListenerSplash {
//                override fun onStartRequest(channel: String) {
//                }
//
//                override fun onAdClick(channel: String) {
//                }
//
//                override fun onAdFailed(failedMsg: String?) {
//                    actionDetail(1000)
//                }
//
//                override fun onAdDismissed() {
//                    actionDetail(0)
//                }
//
//                override fun onAdPrepared(channel: String) {
//                }
//            })
        actionDetail(1000)
    }

    fun actionDetail(delayMillis: Long) {
        mFlAdContainer.postDelayed({
            DetailActivity.Detail.action(this)
            finish()
        }, delayMillis)
    }
}
