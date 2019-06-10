package com.ifmvo.androidad;

import android.app.Application;
import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.AdRequest;
import com.ifmvo.androidad.ad.TogetherAdConst;
import com.liangzemu.ad.sea.TogetherAdSea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * (●ﾟωﾟ●)
 *
 * Created by Matthew_Chen on 2019-04-22.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Google 广告的测试 ID
        Map<String, String> googleIdMap = new HashMap<>();
        googleIdMap.put(TogetherAdConst.AD_SPLASH, "ca-app-pub-3940256099942544/2247696110");
        googleIdMap.put(TogetherAdConst.AD_BANNER, "ca-app-pub-3940256099942544/6300978111");
       // googleIdMap.put(TogetherAdConst.AD_FLOW, "ca-app-pub-3940256099942544/2247696110");
        googleIdMap.put(TogetherAdConst.AD_INTER, "ca-app-pub-3940256099942544/1033173712");
        TogetherAdSea.INSTANCE.initGoogleAd(this, "ca-app-pub-3940256099942544~3347511713", googleIdMap);

        Map<String, String> facebookIdMap = new HashMap<>();
        facebookIdMap.put(TogetherAdConst.AD_SPLASH, "2418474145048681_2419052008324228");
        facebookIdMap.put(TogetherAdConst.AD_BANNER, "2418474145048681_2419053121657450");
        //facebookIdMap.put(TogetherAdConst.AD_FLOW, "2418474145048681_2419055058323923");
        facebookIdMap.put(TogetherAdConst.AD_INTER, "2418474145048681_2423241911238571");
        TogetherAdSea.INSTANCE.initFacebookAd(this, facebookIdMap);


        /*
         * 初始化 Google 广告
         */
        Map<String, List<String>> googleIdListMap = new HashMap<>();

        //插页广告
        List<String> googleInterList = new ArrayList<>();
        googleInterList.add("ca-app-pub-6810306854458967/3276250160");
        googleInterList.add("ca-app-pub-6810306854458967/5710841812");
        googleIdListMap.put(TogetherAdConst.AD_INTER, googleInterList);

        //播放暂停
        List<String> googlePauseList = new ArrayList<>();
        googlePauseList.add("ca-app-pub-3940256099942544/6300978111");
        googlePauseList.add("ca-app-pub-3940256099942544/6300978111");
        googleIdListMap.put(TogetherAdConst.AD_PAUSE, googlePauseList);

        //激励
        List<String> googleRewardList = new ArrayList<>();
        googleRewardList.add("ca-app-pub-3940256099942544/5224354917");
        googleRewardList.add("ca-app-pub-3940256099942544/5224354917");
        googleIdListMap.put(TogetherAdConst.AD_REWARD, googleRewardList);
        //原生 ID是错的  用的激励的。。
        List<String> googleFlowList = new ArrayList<>();
        googleFlowList.add("ca-app-pub-3940256099942544/5224354917");
        //right
        //googleFlowList.add("ca-app-pub-3940256099942544/2247696110");
        googleFlowList.add("ca-app-pub-3940256099942544/5224354917");
        googleFlowList.add("ca-app-pub-3940256099942544/2247696110");

        googleIdListMap.put(TogetherAdConst.AD_FLOW, googleFlowList);

        /**
         * "AD84300E9B7D7E2DC6479CFB2F31E5C7"
         */
        TogetherAdSea.INSTANCE.initAdGoogle(this, "ca-app-pub-6810306854458967~4172564328", googleIdListMap,"AD84300E9B7D7E2DC6479CFB2F31E5C7");



        /*
         * 初始化 Facebook 广告
         */
        Map<String, List<String>> facebookIdListMap = new HashMap<>();

        //插页广告
        List<String> facebookInterList = new ArrayList<>();
        facebookInterList.add("308281803417604_308823620030089");
        facebookInterList.add("308281803417604_308823840030067");
        facebookIdListMap.put(TogetherAdConst.AD_INTER, facebookInterList);

        //播放暂停
        List<String> facebookPauseList = new ArrayList<>();
        facebookPauseList.add("290080388575176_298008991115649");
        facebookPauseList.add("290080388575176_299645454285336");
        facebookIdListMap.put(TogetherAdConst.AD_PAUSE, facebookPauseList);

        //激励
        List<String> facebookRewardList = new ArrayList<>();
        facebookRewardList.add("308281803417604_312311299681321");
        facebookRewardList.add("308281803417604_312311643014620");
        facebookIdListMap.put(TogetherAdConst.AD_REWARD, facebookRewardList);

        //原生 ID是错的  用的激励的。。
        List<String> facebookFlowList = new ArrayList<>();
        facebookFlowList.add("308281803417604_312311299681321");
        //right
        //facebookFlowList.add("2418474145048681_2419055058323923");
        facebookFlowList.add("308281803417604_312311299681321");
        facebookFlowList.add("2418474145048681_2419055058323923");
        facebookIdListMap.put(TogetherAdConst.AD_FLOW, facebookFlowList);

        TogetherAdSea.INSTANCE.initAdFacebook(this, facebookIdListMap,true);
    }
}
