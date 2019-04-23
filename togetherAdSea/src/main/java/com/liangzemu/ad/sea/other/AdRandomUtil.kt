package com.liangzemu.ad.sea.other

/*
 * (●ﾟωﾟ●)
 *
 * 参数 configStr : "google:2,facebook:8"
 *
 * 按照 2 ：8 的比例随机返回 GOOGLE or FACEBOOK
 *
 * return AdNameType.GOOGLE  || AdNameType.FACEBOOK || ...
 *
 * Created by Matthew_Chen on 2018/8/24.
 */
object AdRandomUtil {

    /**
     * configStr : "google:2,facebook:8"
     *
     * return AdNameType.GOOGLE  || AdNameType.FACEBOOK || ...
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
                    val valueStr = splitKeyValue[1]//2
                    if (keyStr.isNotEmpty() && valueStr.isNotEmpty()) {
                        //加到 list 里面 2 个 "google"
                        repeat(valueStr.toInt()) {
                            when (keyStr) {
                                AdNameType.GOOGLE.type -> {
                                    list.add(AdNameType.GOOGLE)
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