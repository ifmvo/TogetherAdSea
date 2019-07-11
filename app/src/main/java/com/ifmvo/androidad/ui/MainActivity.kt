package com.ifmvo.androidad.ui

import android.app.ListActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.ifmvo.androidad.ad.logd
import com.ifmvo.androidad.adExtend.RewardAdHelper
import com.ifmvo.androidad.adExtend.SplashAdHelper

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
        SplashAdHelper.showAd()
        //再请求一个留着下次用
        SplashAdHelper.requestAd(30)

        cacheReward()

        val arr = arrayListOf(
            "踩坑指南",
            "常用横幅 （ 细长条版 ）",
            "原生广告 （ RecyclerView 版 ）",
            "插页广告",
            "激励广告"
        )

        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arr)
    }

    private fun cacheReward() = RewardAdHelper.requestAd(overTimeSecond = 30, onClosed = mOnClosed)


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
                RecyclerViewActivity.action(this)
            }
            3 -> {
                SplashAdHelper.requestAd(overTimeSecond = 4) {
                    SplashAdHelper.showAd()
                    SplashAdHelper.requestAd()//保证展示了一个，就请求下一个的原则
                }
            }
            4 -> {
                RewardAdHelper.requestAd(overTimeSecond = 6,
                    onSuccess = {
                        logd(tag, "onSuccess")
                        RewardAdHelper.showAd()
                    }, onFailed = {
                        logd(tag, "onFailed")

                    }, onClosed = mOnClosed
                )
            }
        }
    }

    private val mOnClosed: (isReward: Boolean) -> Unit = {
        logd(tag, "onClosed: $it")
        cacheReward()
    }
}
