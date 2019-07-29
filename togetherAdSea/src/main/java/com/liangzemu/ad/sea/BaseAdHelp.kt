package com.liangzemu.ad.sea

import android.os.CountDownTimer
import androidx.annotation.NonNull
import com.google.android.gms.ads.AdRequest
import com.liangzemu.ad.sea.other.*
import com.liangzemu.ad.sea.other.loge
import com.liangzemu.ad.sea.other.logw

abstract class BaseAdHelp(adConstStr: String,timeOutMillsecond:Long= TogetherAdSea.timeoutMillsecond):AbstractAdHelp(adConstStr,timeOutMillsecond) {
    /**
     * 比例随机完 开始请求广告
     * @param type AdNameType
     * @param level Int
     * @param configStr String?
     * @param requestIndex Int
     * @param adListener IAdListener
     * @return Unit
     */
    override fun dispatchAdRequest(type: AdNameType, level: Int, configStr: String?, requestIndex: Int, adListener: IAdListener) {
        when(type){
            AdNameType.FACEBOOK->{
                requestAdFaceBook(level,configStr,requestIndex,adListener)
            }
            AdNameType.GOOGLE_ADMOB->{
                requestAdGoogle(level,configStr,requestIndex,adListener)
            }
        }
    }
    /**
     * 请求谷歌广告
     * @param level Int
     * @param rewardConfigStr String?
     * @param indexGoogle Int
     * @param adListener IAdListener
     * @return Unit
     */
    private fun requestAdGoogle(
        level: Int,
        rewardConfigStr: String?,
        indexGoogle: Int,
        @NonNull adListener: IAdListener
    ) {
        val adNameType=AdNameType.GOOGLE_ADMOB
        startAdRequest(adListener, adNameType, indexGoogle, level, rewardConfigStr,::requestAdGoogle)
    }
    /**
     * 请求FaceBook广告
     * @param level Int
     * @param rewardConfigStr String?
     * @param indexGoogle Int
     * @param adListener IAdListener
     * @return Unit
     */
    private fun requestAdFaceBook(
        level: Int,
        rewardConfigStr: String?,
        indexGoogle: Int,
        @NonNull adListener: IAdListener
    ) {
        val adNameType=AdNameType.FACEBOOK
        startAdRequest(adListener, adNameType, indexGoogle, level, rewardConfigStr,::requestAdFaceBook)
    }
    private fun checkIdList(index: Int,idList:List<String>?, adNameType: AdNameType,adListener: IAdListener,level: Int, configStr: String?):Boolean{

        //分档位请求完毕  都没广告
        if (index >= idList?.size ?: 0) {
            //如果所有档位都请求失败了，就切换另外一种广告
            val newRewardConfig = configStr?.replace(adNameType.type, AdNameType.NO.type)
            requestAdVertical(newRewardConfig, adListener,level)
            return false
        }
        if (idList.isNullOrEmpty()) {
            //如果在 Map 里面获取不到该广告位的 idList 意味着初始化的时候没有设置这个广告位
            loge("${adNameType.type}: ${TogetherAdSea.context.getString(R.string.ad_id_no)}")
            adListener.onAdFailed(TogetherAdSea.context.getString(R.string.ad_id_no),"ALL")
            return false
        }
        return true
    }
    private fun startAdRequest(
        adListener: IAdListener,
        adNameType: AdNameType,
        index: Int,
        level: Int,
        configStr: String?,
        next:(Int,String?,Int,IAdListener)->Unit
    ) {
        /**
         * 判断id数组是否满足此次请求
         */
        val idList=getIDList(level,adNameType)
        val checkResult = checkIdList(index, idList, adNameType, adListener, level, configStr)
        if(!checkResult)
            return

        logi("${adNameType.type}: ${TogetherAdSea.context.getString(R.string.start_request)}")

        val ad_id = getIDList(level, adNameType)!![index]
        /**
         * 请求广告
         */
        val pair=initAD(ad_id,adNameType) //<AD,key>
        //回调监听器，开始请求
        adListener.onStartRequest(adNameType.type,pair.second)
        /**
         * 设置定时器
         */
        val timer = creatTimer {
            addTimeOut(pair.second)
            logw("${adNameType.type}: indexGoogle:$index, 超时")
            next(level, configStr, index + 1, adListener)
        }.apply {
            start()
        }
        /**
         * 开始请求
         */
        setAdListenerAndStart(ad_id,pair.first,adListener,timer,adNameType) {
            loge("${adNameType.type}: index:$index, errorCode:$it")
            //判断是否已经超时过了
            if(isTimeOut(pair.second)){
                logw("${pair.second} 之前已经过时了")
            }else {
                timer.cancel()
                next(level, configStr, index + 1, adListener)
            }

        }
    }

    /**
     * 设置监听器并请求广告
     * @param id String
     * @param ad Any
     * @param adListener IAdListener
     * @param timer CountDownTimer
     * @param adNameType AdNameType
     * @param errorCallback (String?)->Unit
     * @return Unit
     */
    fun setAdListenerAndStart(id:String,ad:Any,adListener: IAdListener,timer:CountDownTimer,adNameType: AdNameType,errorCallback:(String?)->Unit){
        when(adNameType){
            AdNameType.GOOGLE_ADMOB->{
                setGoogleAdListenerAndStart(id, ad, adListener, timer,errorCallback)
            }
            AdNameType.FACEBOOK->{
                setFaceBookAdListenerAndStart( ad, adListener, timer,errorCallback)
            }
            else ->{
                throw IllegalArgumentException("没有此广告类型:${adNameType.type}")
            }

        }
    }
    abstract fun initAD(id:String,adNameType: AdNameType):Pair<Any,String>
    abstract fun setGoogleAdListenerAndStart(id:String,adOrBuilder:Any,adListener: IAdListener,timer:CountDownTimer,errorCallback:(String?)->Unit)
    abstract fun setFaceBookAdListenerAndStart(adOrBuilder:Any,adListener: IAdListener,timer:CountDownTimer,errorCallback:(String?)->Unit)
    /**
     * 根据level获取对应类型的广告id数组
     * @param level Int
     * @param type AdNameType
     * @return List<String>?
     */
    private fun getIDList(level: Int, type: AdNameType): List<String>? {
        val map =
            if (type == AdNameType.GOOGLE_ADMOB)
                TogetherAdSea.idListGoogleMap
            else TogetherAdSea.idListFacebookMap
        return if (level != -1) {
            map[adConstStr]?.filterIndexed { index, _ ->
                level == index
            }
        } else {
            map[adConstStr]
        }
    }

    /**
     * 便于google测试设备设置
     * @return AdRequest
     */
    fun getGoogleAdRequest(): AdRequest {
        return  AdRequest.Builder().apply {
            if (TogetherAdSea.testDeviceID != null) addTestDevice(
                TogetherAdSea.testDeviceID
            )
        }.build()
    }
}