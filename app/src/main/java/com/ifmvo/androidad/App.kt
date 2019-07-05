package com.ifmvo.androidad

import android.app.Application
import com.ifmvo.androidad.ad.AdIdFactory
import com.liangzemu.ad.sea.BuildConfig
import com.liangzemu.ad.sea.TogetherAdSea

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-07-04.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        TogetherAdSea.initAdGoogle(
            this, AdIdFactory.getGoogleAdId(), AdIdFactory.getGAdIdList(), "AD84300E9B7D7E2DC6479CFB2F31E5C7"
        )

        TogetherAdSea.initAdFacebook(this, AdIdFactory.getFbAdIdList(), BuildConfig.DEBUG)
    }
}