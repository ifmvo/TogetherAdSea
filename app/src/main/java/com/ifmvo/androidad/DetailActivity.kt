package com.ifmvo.androidad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.ads.Ad
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.ifmvo.androidad.ad.Config
import com.ifmvo.androidad.ad.TogetherAdConst
import com.liangzemu.ad.sea.helper.TogetherAdSeaBanner
import com.liangzemu.ad.sea.helper.TogetherAdSeaFlow
import kotlinx.android.synthetic.main.activity_detail.*

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-04-23.
 */
class DetailActivity : AppCompatActivity() {

    object Detail {
        fun action(context: Context) {
            context.startActivity(Intent(context, DetailActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)

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


        TogetherAdSeaBanner.showAdBanner(this, Config.bannerAdConfig(), TogetherAdConst.AD_BANNER, mFlAdBannerContainer,
            object : TogetherAdSeaBanner.AdListenerBanner {
                override fun onStartRequest(channel: String) {
                }

                override fun onAdClick(channel: String) {
                }

                override fun onAdFailed(failedMsg: String?) {
                }

                override fun onAdPrepared(channel: String) {
                }
            })
    }
}