package com.liangzemu.ad.sea.other

import com.liangzemu.ad.sea.TogetherAdSea

/*
 * (●ﾟωﾟ●)
 *
 * 参数 configStr : "google:2,facebook:8"
 *
 * 按照 2 ：8 的比例随机返回 GOOGLE_ADMOB or FACEBOOK
 *
 * return AdNameType.GOOGLE_ADMOB  || AdNameType.FACEBOOK || ...
 *
 * Created by Matthew_Chen on 2018/8/24.
 */
object AdRandomUtil {

    /**
     * configStr : "google:2,facebook:8"
     *
     * return AdNameType.GOOGLE_ADMOB  || AdNameType.FACEBOOK || ...
     */
    fun getRandomAdName(configStr: String?): AdNameType {

        logd("广告的配置：$configStr")
        if (configStr.isNullOrEmpty()) {
            return AdNameType.NO
        }

        val list = ArrayList<AdNameType>()
        val split = configStr.split(",")
        repeat(split.size) { index ->
            // google:2
            val itemStr = split[index]
            if (itemStr.isNotEmpty()) {
                val splitKeyValue = itemStr.split(":")
                if (splitKeyValue.size == 2) {
                    val keyStr = splitKeyValue[0]//google
                    var valueStr = splitKeyValue[1]//2
                    if (keyStr.isNotEmpty() && valueStr.isNotEmpty()) {
                        //加到 list 里面 2 个 "google"
                        //当中介模式时只管 Google , 其他的都忽略
                        if (TogetherAdSea.isMediationMode && keyStr != AdNameType.GOOGLE_ADMOB.type) {
                            valueStr = "0"
                        }

                        repeat(valueStr.toInt()) {
                            when (keyStr) {
                                AdNameType.GOOGLE_ADMOB.type -> {
                                    list.add(AdNameType.GOOGLE_ADMOB)
                                }
                                AdNameType.FACEBOOK.type -> {
                                    list.add(AdNameType.FACEBOOK)
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
            }
        }

        if (list.size == 0) {
            return AdNameType.NO
        }

        val adNameType = list[(getRandomInt(list.size)) - 1]
        logd("随机到的广告: ${adNameType.type}")
        return adNameType
    }

    private fun getRandomInt(max: Int): Int {
        return (1 + Math.random() * (max - 1 + 1)).toInt()
    }
}