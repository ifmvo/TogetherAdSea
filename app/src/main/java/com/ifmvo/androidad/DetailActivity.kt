package com.ifmvo.androidad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ifmvo.androidad.ad.Config
import com.ifmvo.androidad.ad.TogetherAdConst
import com.liangzemu.ad.sea.helper.TogetherAdSeaBanner
import com.liangzemu.ad.sea.helper.TogetherAdSeaInter
import com.liangzemu.ad.sea.helper.TogetherAdSeaPause
import com.liangzemu.ad.sea.helper.TogetherAdSeaReward
import com.liangzemu.ad.sea.other.Direction
import kotlinx.android.synthetic.main.activity_detail.*

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-04-23.
 */
class DetailActivity : AppCompatActivity() {

    private val tag = "DetailActivity"
    lateinit var togetherAdSeaReward: TogetherAdSeaReward

    object Detail {
        fun action(context: Context) {
            context.startActivity(Intent(context, DetailActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)
        togetherAdSeaReward = TogetherAdSeaReward(TogetherAdConst.AD_REWARD)
        btnRequestInter.setOnClickListener {
            requestInter()
        }

        btnShowInter.setOnClickListener {
            TogetherAdSeaInter.showAdInter()
        }

        btnRequestPause.setOnClickListener {
            requestPause()
        }

        btnRequestReward.setOnClickListener {
            requestReward()
            requestReward1()
        }

        btnShowReward.setOnClickListener {
            togetherAdSeaReward.showAdReward()
        }

        requestFlow()

        requestBanner()
    }

    private fun requestFlow() {
//        TogetherAdSeaFlow.showAdFlowHorizontal(
//            this,
//            Config.flowAdConfig(),
//            TogetherAdConst.AD_FLOW,
//            object : TogetherAdSeaFlow.AdListenerFlow {
//                override fun onAdShow(channel: String) {
//                }
//
//                override fun onStartRequest(channel: String) {
//                }
//
//                override fun onAdClick(channel: String) {
//                }
//
//                override fun onAdFailed(failedMsg: String?) {
//                }
//
//                override fun onAdPrepared(channel: String, ad: Any) {
//                    when (ad) {
//                        is Ad -> {
//                            Log.e("ifmvo", "TogetherAdSeaFlow:facebook")
//                        }
//                        is UnifiedNativeAd -> {
//                            Log.e("ifmvo", "TogetherAdSeaFlow:google")
//                        }
//                    }
//                }
//            })
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

    private fun requestPause() {
        TogetherAdSeaPause.showAdPause(
            this,
            Config.pauseAdConfig(),
            TogetherAdConst.AD_PAUSE,
            mFlAdPause,
            object : TogetherAdSeaPause.AdListenerPause {
                override fun onStartRequest(channel: String) {
                    Log.e(tag, "onAdPrepared:$channel")
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

    private fun requestReward() {
        togetherAdSeaReward.requestAdReward(
            Config.rewardAdConfig(),
            Direction.HORIZONTAL,
            object : TogetherAdSeaReward.MultipleRewarListener() {
                override fun onAdClose(channel: String, isReward: Boolean) {
                    Log.e(tag, "onAdClose:$channel $isReward")
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
                    Log.e(tag, "onAdShow:$channel")
                }


                override fun onAdPrepared(channel: String) {
                    Log.e(tag, "onAdPrepared:$channel")
                }
            })
    }

    private fun requestReward1() {
        togetherAdSeaReward.requestAdReward(
            Config.rewardAdConfig(),
            Direction.HORIZONTAL,
            object : TogetherAdSeaReward.MultipleRewarListener() {
                override fun onAdClose(channel: String, isReward: Boolean) {
                    Log.e(tag, "onAdClose1:$channel $isReward")
                    togetherAdSeaReward.destoryAdAndListener()
                }

                override fun onStartRequest(channel: String) {
                    Log.e(tag, "onStartRequest1:$channel")
                }

                override fun onAdClick(channel: String) {
                    Log.e(tag, "onAdClick1:$channel")
                }

                override fun onAdFailed(failedMsg: String?) {
                    Log.e(tag, "onAdFailed1:$failedMsg")
                }

                override fun onAdShow(channel: String) {
                    Log.e(tag, "onAdShow1:$channel")
                }

                override fun onAdPrepared(channel: String) {
                    Log.e(tag, "onAdPrepared1:$channel")
                }
            })
    }
}