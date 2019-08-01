package com.ifmvo.androidad.ui

import android.app.ListActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.ifmvo.androidad.ad.logd
import com.ifmvo.androidad.adExtend.RewardAdManagerNew
import com.ifmvo.androidad.adExtend.SplashAdManagerNew

/*
 * (●ﾟωﾟ●)
 *
 * Created by Matthew_Chen on 2019-07-04.
 */
class MainActivity : ListActivity() {

    private val tag = "MainActivity"

    companion object {
        fun action(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //展示开屏广告
        SplashAdManagerNew.showAd()
        //再请求一个留着下次用
        SplashAdManagerNew.requestAd(overTimeSecond = 30)

        cacheReward()

        val arr = arrayListOf(
            "踩坑指南",
            "常用横幅 （ 细长条版 ）",
            "原生广告 （ RecyclerView 版 ）",
            "插页广告",
            "激励广告",
            "Facebook原生横幅+Google原生"
        )

        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arr)
    }

    private fun cacheReward() = RewardAdManagerNew.requestAd(overTimeSecond = 30)

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        when (position) {
            0 -> {
                ReadmeActivity.action(this)
            }
            1 -> {
                BannerActivity.action(this)
            }
            2 -> {
                FlowAdActivity.action(this)
            }
            3 -> {
                SplashAdManagerNew.requestAd(overTimeSecond = 4, onSuccess = {
                    SplashAdManagerNew.showAd()
                }, onFailed = {
                    SplashAdManagerNew.requestAd()
                }, onClosed = {
                    SplashAdManagerNew.requestAd()
                })
            }
            4 -> {
                RewardAdManagerNew.requestAd(overTimeSecond = 6,
                    onSuccess = {
                        logd(tag, "onSuccess")
                        RewardAdManagerNew.showAd()
                    }, onFailed = {
                        logd(tag, "onFailed")

                    }, onClosed = {
                        logd(tag, "onClosed: $it")
                        cacheReward()
                    }
                )
            }
            5 -> {
                FlowBannerFlowAdActivity.action(this)
            }
        }
    }
}
