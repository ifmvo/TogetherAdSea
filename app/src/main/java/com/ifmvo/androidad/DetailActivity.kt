package com.ifmvo.androidad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.ads.Ad
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.ifmvo.androidad.ad.Config
import com.ifmvo.androidad.ad.TogetherAdConst
import com.liangzemu.ad.sea.helper.TogetherAdSeaBanner
import com.liangzemu.ad.sea.helper.TogetherAdSeaFlow
import com.liangzemu.ad.sea.helper.TogetherAdSeaInter
import kotlinx.android.synthetic.main.activity_detail.*

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-04-23.
 */
class DetailActivity : AppCompatActivity() {

    private val tag = "DetailActivity"

    object Detail {
        fun action(context: Context) {
            context.startActivity(Intent(context, DetailActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)

        btnRequestInter.setOnClickListener {
            requestInter()
        }

        btnShowInter.setOnClickListener {
            TogetherAdSeaInter.showAdInter()
        }

        requestFlow()

        requestBanner()
    }

    private fun requestFlow() {
        TogetherAdSeaFlow.showAdFlow(
            this,
            Config.flowAdConfig(),
            TogetherAdConst.AD_FLOW,
            object : TogetherAdSeaFlow.AdListenerFlow {
                override fun onStartRequest(channel: String) {
                }

                override fun onAdClick(channel: String) {
                }

                override fun onAdFailed(failedMsg: String?) {
                }

                override fun onAdPrepared(channel: String, ad: Any) {
                    when (ad) {
                        is Ad -> {
                            Log.e("ifmvo", "TogetherAdSeaFlow:facebook")
                        }
                        is UnifiedNativeAd -> {
                            Log.e("ifmvo", "TogetherAdSeaFlow:google")
                        }
                    }
                }
            })
    }

    private fun requestBanner() {
        TogetherAdSeaBanner.showAdBanner(this, Config.bannerAdConfig(), TogetherAdConst.AD_BANNER, mFlAdBannerContainer,
            object : TogetherAdSeaBanner.AdListenerBanner {
                override fun onStartRequest(channel: String) {
                    Log.e(tag, "onStartRequest:$channel")
                }

                override fun onAdClick(channel: String) {
                    Log.e(tag, "onAdClick:$channel")
                }

                override fun onAdFailed(failedMsg: String?) {
                    Log.e(tag, "onAdFailed:$failedMsg")
                }

                override fun onAdPrepared(channel: String) {
                    Log.e(tag, "onAdPrepared:$channel")
                }
            })
    }

    private fun requestInter() {
        TogetherAdSeaInter.requestAdInter(
            this,
            Config.interAdConfig(),
            TogetherAdConst.AD_INTER,
            object : TogetherAdSeaInter.AdListenerInter {
                override fun onAdPrepared(channel: String) {
                    Log.e(tag, "onAdPrepared:$channel")
                }

                override fun onAdClose(channel: String) {
                    Log.e(tag, "onAdClose:$channel")
                }

                override fun onStartRequest(channel: String) {
                    Log.e(tag, "onStartRequest:$channel")
                }

                override fun onAdClick(channel: String) {
                    Log.e(tag, "onAdClick:$channel")
                }

                override fun onAdFailed(failedMsg: String?) {
                    Log.e(tag, "onAdFailed:$failedMsg")
                }

                override fun onAdShow(channel: String) {
                    Log.e(tag, "onAdPrepared:$channel")
                }

            })
    }
}