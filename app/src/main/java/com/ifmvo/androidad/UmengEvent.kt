package com.ifmvo.androidad

import com.ifmvo.androidad.ad.logd

/**
 * ================================================
 * 框架要求框架中的每个 {@link } 都需要实现此类,以满足规范
 *
 * @function 作用
 * Created by Joe_ZBB on 2019/1/31 1:34 PM
 * ================================================
 */
object UmengEvent {

    const val AD_DOWNLOADED_LOCATION = "原生_已下载列表"
    const val AD_DOWNLOADING_LOCATION = "原生_下载中列表"
    const val AD_VIDEO_LOCATION = "原生_前贴"
    const val AD_SPLASH_LOCATION = "原生_开屏"
    const val AD_INDEX_LOCATION = "原生_首页"
    const val AD_EXIT_LOCATION = "原生_退出"
    const val AD_CHECK_LOCATION = "横幅_选择下载页面"
    const val AD_REWARD_LOCATION = "激励_高速下载"
    const val AD_PLAYER_PAUSE_INTER_LOCATION = "插页_播放暂停"


    /**
     * 广告点击
     */
    fun eventAdClick(name: String, location: String) {
        printLog(Exception().stackTrace[0].methodName, "name:$name,location:$location")
    }

    /**
     * 广告展示
     */
    fun eventAdShow(name: String, location: String) {
        printLog(Exception().stackTrace[0].methodName, "name:$name,location:$location")
    }

    /**
     * 广告请求到了
     */
    fun eventAdFills(name: String, location: String) {
        printLog(Exception().stackTrace[0].methodName, "name:$name,location:$location")
    }

    /**
     * 广告请求
     */
    fun eventAdRequest(name: String, location: String) {
        printLog(Exception().stackTrace[0].methodName, "name:$name,location:$location")
    }


    private fun printLog(methodName: String, msg: String) {
        logd("UmengEvent", "$methodName:$msg")
    }

}