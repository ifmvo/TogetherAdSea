package com.ifmvo.androidad.ad

/*
 * (●ﾟωﾟ●) 任意配置广告位
 * 
 * Created by Matthew_Chen on 2018/12/26.
 */
class TogetherAdConst {

    companion object {

        /**
         ************************ 所有种类广告 ***********************
         *
         * 涵盖 Google Admob 和 Facebook 广告所有种类
         */
        //横幅
        const val banner = "banner"

        //原生
        const val native = "native"

        //插屏
        const val interstitial = "interstitial"

        //激励
        const val reward = "reward"

        //原生横幅  仅facebook有
        const val flow_banner = "flow_banner"


        /**
         * ************************ 实际应用场景特殊处理 ***********************
         * 1. 开屏：为了节约开屏时间，多个档位可以同时请求，详细请看 SplashAdManager
         * 2. 播放器暂停：使用大的 Banner 模拟实现暂停广告
         * 3. 激励广告：因为 Facebook 的激励广告只提供给游戏应用，所以非游戏应用使用 Facebook 激励广告可以用插页广告冒充
         */
        //开屏插屏a
        const val splash_interstitial_a = "interstitial_a"

        //开屏插屏d
        const val splash_interstitial_d = "interstitial_d"

        //激励a
        const val reward_a = "reward_a"

        //激励d
        const val reward_d = "reward_d"

        //使用暂停
        const val banner_pause = "banner_pause"

        //激励广告其中facebook使用插页广告实现
        const val reward_temp = "reward_temp"
    }
}