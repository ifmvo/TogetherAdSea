package com.ifmvo.androidad.ad

/*
 * (●ﾟωﾟ●) 所有的广告位 ID
 * 
 * Created by Matthew_Chen on 2019-05-29.
 */
object AdIdFactory {

    /**
     * Facebook
     * 获取某个广告位 所有档位的广告ID,
     * adConst：广告位
     */
    fun getFbAdIdList(): Map<String, List<String>> {
        return mapOf(
            TogetherAdConst.banner to mutableListOf(
                "308281803417604_308289240083527", "308281803417604_308289343416850"
            ),
            TogetherAdConst.native to mutableListOf(
                "308281803417604_308288026750315", "308281803417604_308288183416966"
            ),
            TogetherAdConst.interstitial to mutableListOf(
                "308281803417604_308290183416766", "308281803417604_308289750083476"
            ),
            TogetherAdConst.reward to mutableListOf(
                "308281803417604_312311299681321", "308281803417604_312311643014620"
            ),
            TogetherAdConst.flow_banner to mutableListOf(
                "470252103736499_470845783677131", "470252103736499_470849730343403"
            ),

            TogetherAdConst.splash_interstitial_a to mutableListOf(
                "308281803417604_308290183416766"
            ),
            TogetherAdConst.splash_interstitial_d to mutableListOf(
                "308281803417604_308289750083476"
            ),
            TogetherAdConst.reward_a to mutableListOf(
                "308281803417604_312311299681321"
            ),
            TogetherAdConst.reward_d to mutableListOf(
                "308281803417604_312311643014620"
            ),
            TogetherAdConst.banner_pause to mutableListOf(
                "308281803417604_308289240083527", "308281803417604_308289343416850"
            ),
            TogetherAdConst.reward_temp to mutableListOf(
                "308281803417604_308290183416766", "308281803417604_308289750083476"
            )
        )
    }


    /**
     * Google
     * 获取某个广告位 所有档位的 广告ID
     * adConst：广告位
     */
    fun getGAdIdList(): Map<String, List<String>> {
        return mapOf(
            TogetherAdConst.banner to mutableListOf(
                "ca-app-pub-3940256099942544/6300978111"
            ),
            TogetherAdConst.native to mutableListOf(
                "ca-app-pub-3940256099942544/2247696110"
            ),
            TogetherAdConst.interstitial to mutableListOf(
                "ca-app-pub-3940256099942544/1033173712"
            ),
            TogetherAdConst.reward to mutableListOf(
                "ca-app-pub-3940256099942544/5224354917"
            ),
            TogetherAdConst.flow_banner to mutableListOf(
                "ca-app-pub-3940256099942544/2247696110"
            ),

            TogetherAdConst.splash_interstitial_a to mutableListOf(
                "ca-app-pub-3940256099942544/1033173712"
            ),
            TogetherAdConst.splash_interstitial_d to mutableListOf(
                "ca-app-pub-3940256099942544/1033173712"
            ),
            TogetherAdConst.reward_a to mutableListOf(
                "ca-app-pub-3940256099942544/5224354917"
            ),
            TogetherAdConst.reward_d to mutableListOf(
                "ca-app-pub-3940256099942544/5224354917"
            ),
            TogetherAdConst.banner_pause to mutableListOf(
                "ca-app-pub-3940256099942544/6300978111",
                "ca-app-pub-3940256099942544/6300978111"
            ),
            TogetherAdConst.reward_temp to mutableListOf(
                "ca-app-pub-3940256099942544/5224354917",
                "ca-app-pub-3940256099942544/5224354917"
            )
        )
    }

    /**
     * 谷歌广告应用ID
     */
    fun getGoogleAdId(): String {
        return "ca-app-pub-3940256099942544~3347511713"
    }
}