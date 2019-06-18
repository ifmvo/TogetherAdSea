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
import com.liangzemu.ad.sea.helper.TogetherAdSeaRewardTemp
import com.liangzemu.ad.sea.other.AdRandomUtil
import com.liangzemu.ad.sea.AdWrapper
import com.liangzemu.ad.sea.IAdListener
import com.liangzemu.ad.sea.helper.*
import com.liangzemu.ad.sea.other.Direction
import kotlinx.android.synthetic.main.activity_detail.*

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-04-23.
 */
class DetailActivity : AppCompatActivity() {

    private val tag = "DetailActivity"
    val togetherAdSeaReward by lazy { TogetherAdSeaRewardTemp(TogetherAdConst.AD_REWARD) }
    val rewardHelper = RewardHelper(TogetherAdConst.AD_REWARD)
    object Detail {
        fun action(context: Context) {
            context.startActivity(Intent(context, DetailActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)

        btnTestRandom.setOnClickListener {
            Log.e("ifmvo", "随机结果：${AdRandomUtil.getRandomAdName(Config.rewardAdConfig())}")
        }

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
            //requestReward1()
        }

        btnShowReward.setOnClickListener {
            //togetherAdSeaReward.showAdReward()
            startActivity(Intent().apply {
                this.setClass(this@DetailActivity,MainActivity::class.java)
            })
            this.finish()
        }
        btnRequestFlow.setOnClickListener {
            requestFlow()
        }
        //requestFlow()

        //requestBanner()
    }

    private fun requestFlow() {
        FlowHelper(TogetherAdConst.AD_FLOW).requestAd(Config.flowAdConfig(),object :IAdListener{
            override fun onStartRequest(channel: String, key: String) {
            }

            override fun onAdClick(channel: String, key: String) {
                Log.i("requestFlow","onAdClick$key")
            }

            override fun onAdFailed(failedMsg: String?, key: String) {
                Log.i("requestFlow","onAdFailed$key")
            }

            override fun onAdShow(channel: String, key: String) {
                Log.i("requestFlow","onAdShow$key")
            }

            override fun onAdClose(channel: String, key: String, other: Any) {
                Log.i("requestFlow","onAdClose$key")
            }

            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
                Log.i("requestFlow","onAdPrepared")
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
        rewardHelper.requestAd(Config.rewardAdConfig(),object :IAdListener{
            override fun onStartRequest(channel: String, key: String) {
            }

            override fun onAdClick(channel: String, key: String) {
                Log.i("requestReward","onAdClick$key")
            }

            override fun onAdFailed(failedMsg: String?, key: String) {
                Log.i("requestReward","onAdFailed$key")
            }

            override fun onAdShow(channel: String, key: String) {
                Log.i("requestReward","onAdShow$key")
            }

            override fun onAdClose(channel: String, key: String, other: Any) {
                Log.i("requestReward","onAdClose$key")
            }

            override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
                Log.i("requestReward","onAdPrepared")
            }

        },onlyOnce = true)
    }

    private fun requestReward1() {
        togetherAdSeaReward.requestAdReward(
            Config.rewardAdConfig(),
            Direction.HORIZONTAL,
            object : TogetherAdSeaRewardTemp.MultipleRewarListener() {
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

    override fun onDestroy() {
        rewardHelper.onDestory()
        super.onDestroy()
    }
}