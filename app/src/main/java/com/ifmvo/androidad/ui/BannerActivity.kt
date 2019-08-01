package com.ifmvo.androidad.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.ifmvo.androidad.R
import com.ifmvo.androidad.ad.Config
import com.ifmvo.androidad.ad.TogetherAdConst
import com.ifmvo.androidad.ad.logd
import com.ifmvo.androidad.ad.loge
import com.liangzemu.ad.sea.AdWrapper
import com.liangzemu.ad.sea.IAdListener
import com.liangzemu.ad.sea.helper.BannerHelper
import kotlinx.android.synthetic.main.activity_banner.*

/*
 * (●ﾟωﾟ●)
 *
 * Created by Matthew_Chen on 2019-04-23.
 */
class BannerActivity : AppCompatActivity() {

    val tag = "BannerActivity"

    private val bannerHelper = BannerHelper(TogetherAdConst.banner)

    companion object {
        fun action(context: Context) {
            context.startActivity(Intent(context, BannerActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)

        bannerHelper.requestAd(Config.bannerAdConfig(), object : IAdListener {
            override fun onStartRequest(channel: String, key: String) {
                logd(tag, "onStartRequest")
            }

            override fun onAdClick(channel: String, key: String) {
                logd(tag, "onAdClick")
            }

            override fun onAdFailed(failedMsg: String?, key: String) {
                loge(tag, "onAdFailed")
            }

            override fun onAdShow(channel: String, key: String) {
                logd(tag, "onAdShow")
            }

            override fun onAdClose(channel: String, key: String, other: Any) {
                logd(tag, "onAdClose")
            }

            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
                logd(tag, "onAdPrepared")
                val ad = adWrapper.realAd
                if (ad is View) {
                    val parent = ad.parent
                    if (parent is ViewGroup) {
                        parent.removeAllViews()
                    }
                    flBannerContainer.removeAllViews()
                    flBannerContainer.addView(adWrapper.realAd as View)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerHelper.removeAd { true }
    }
}