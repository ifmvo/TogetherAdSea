package com.ifmvo.androidad;

import android.app.Application;
import com.ifmvo.androidad.ad.TogetherAdConst;
import com.liangzemu.ad.sea.TogetherAdSea;

import java.util.HashMap;
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

        Map<String, String> googleIdMap = new HashMap<>();
        googleIdMap.put(TogetherAdConst.AD_SPLASH, "ca-app-pub-3940256099942544/2247696110");
        googleIdMap.put(TogetherAdConst.AD_BANNER, "ca-app-pub-3940256099942544/6300978111");
        googleIdMap.put(TogetherAdConst.AD_FLOW, "ca-app-pub-3940256099942544/2247696110");
        TogetherAdSea.INSTANCE.initGoogleAd(this, "ca-app-pub-3940256099942544~3347511713", googleIdMap);

        Map<String, String> facebookIdMap = new HashMap<>();
        facebookIdMap.put(TogetherAdConst.AD_SPLASH, "290080388575176_290083468574868");
        facebookIdMap.put(TogetherAdConst.AD_BANNER, "290080388575176_298008991115649");
        facebookIdMap.put(TogetherAdConst.AD_FLOW, "290080388575176_298449734404908");
        TogetherAdSea.INSTANCE.initFacebookAd(this, facebookIdMap);

    }
}
