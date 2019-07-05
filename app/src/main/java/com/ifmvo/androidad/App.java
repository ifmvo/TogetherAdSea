//package com.ifmvo.androidad;
//
//import android.app.Application;
//import com.ifmvo.androidad.ad.AdIdFactory;
//import com.liangzemu.ad.sea.BuildConfig;
//import com.liangzemu.ad.sea.TogetherAdSea;
//
///*
// * (●ﾟωﾟ●)
// *
// * Created by Matthew_Chen on 2019-04-22.
// */
//public class App extends Application {
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        TogetherAdSea.INSTANCE.initAdGoogle(this, AdIdFactory.INSTANCE.getGoogleAdId(),
//                AdIdFactory.INSTANCE.getGAdIdList(), "AD84300E9B7D7E2DC6479CFB2F31E5C7");
//
//        TogetherAdSea.INSTANCE.initAdFacebook(this, AdIdFactory.INSTANCE.getFbAdIdList(),
//                BuildConfig.DEBUG);
//    }
//}
