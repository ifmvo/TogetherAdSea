package com.liangzemu.ad.sea

interface IAdListener {

    //开始请求
    fun onStartRequest(channel: String,key:String)

    //点击了
    fun onAdClick(channel: String,key:String)

    //失败了
    fun onAdFailed(failedMsg: String?,key:String)

    //展示了
    fun onAdShow(channel: String,key:String)

    //关闭了
    fun onAdClose(channel: String, key:String,other:Any)

    //准备好了
    fun onAdPrepared(channel: String,adWrapper: AdWrapper,key:String)

}