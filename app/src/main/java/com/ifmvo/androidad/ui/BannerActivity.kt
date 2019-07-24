package com.ifmvo.androidad.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.ifmvo.androidad.R
import com.ifmvo.androidad.adExtend.BannerAdManager
import kotlinx.android.synthetic.main.activity_banner.*

/*
 * (●ﾟωﾟ●)
 *
 * Created by Matthew_Chen on 2019-04-23.
 */
class BannerActivity : AppCompatActivity() {

    companion object {
        fun action(context: Context) {
            context.startActivity(Intent(context, BannerActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)

        val bannerAdView = BannerAdManager.getAd()

        if (bannerAdView != null) {
            val adView = bannerAdView.realAd
            adView as View
            if (adView.parent != null) {
                (adView.parent as ViewGroup).removeAllViews()
            }
            flBannerContainer?.addView(adView)
        }

        BannerAdManager.requestAd()

    }
}