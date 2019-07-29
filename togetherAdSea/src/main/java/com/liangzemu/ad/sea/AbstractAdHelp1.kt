package com.liangzemu.ad.sea

import android.os.CountDownTimer
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.*
import java.lang.ref.WeakReference
import kotlin.math.max

/**
 * 广告开始请求时监听器与广告实体(or loader)对应存储
 * 广告正在请求时替换最新一个的监听器
 *
 * 是否每次只能请求一个
 * @property adConstStr String
 * @constructor
 */
abstract class AbstractAdHelp1(val adConstStr: String, val timeOutMillsecond:Long= TogetherAdSea.timeoutMillsecond, val owner:String=adConstStr) : AdBase, IAdListener {
    val levelHorizontal: Int = max(TogetherAdSea.idListGoogleMap[adConstStr]?.size ?: 0, TogetherAdSea.idListFacebookMap[adConstStr]?.size ?: 0) - 1
    /**
     * 本对象内部正在请求的广告个数
     */
    var loadingCount=0
        set(value) {
            field = max(value,0)
        }
    internal val unUseListenerList = ArrayList<IAdListener>()//list<监听器>

    fun requestAd(
        configStr: String?,
        @NonNull userListener: IAdListener,
        direction: Direction = Direction.HORIZONTAL,
        onlyOnce: Boolean = false,
        caCheFilter:(AdWrapper)->Boolean={true}
    ) {
        if (onlyOnce) {
            //正在加载中
            if (loadingAdType.contains(adConstStr)) {
                addListener(userListener, true)
                logi("$adConstStr 正在加载中")
                return
            }
        }
        //查看是否有缓存
        val adFromCache = getAdFromCache(caCheFilter)
        adFromCache?.let {
            logi("$adConstStr 已经有缓存了")
            adFromCache.setListener(userListener,owner)
            adFromCache.getListener()?.onAdPrepared("adCache", adFromCache)
            return
        }
        logi("$adConstStr 开始请求")
        addListener(userListener)
        loadingAdType.add(adConstStr)
        loadingCount++
        if (direction == Direction.HORIZONTAL) {
            requestAdHorizontal(configStr, this)
        } else {
            requestAdVertical(configStr, this)
        }
    }

    /**
     * 横向切换
     */
    private fun requestAdHorizontal(
        configStr: String?,
        iAdListener: IAdListener
    ) {

        //取最高等级

        var level = 0
        logi("$adConstStr total level:$levelHorizontal level:$level start")
        //循环等级请求
        fun requestAdByLevel() {
            //这里虽然是竖向请求  但是因为有level的原因  每一个竖向只有一个  相当于就是横向了
            requestAdVertical(configStr, object : IAdListener {
                override fun onAdShow(channel: String, key: String) {
                    iAdListener.onAdShow(channel, key)
                }

                override fun onAdClose(channel: String, key: String, other: Any) {
                    iAdListener.onAdClose(channel, key, other)
                }

                override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {//每个整体请求的结束
                    logi("AbstractAdHelp:$adConstStr level:$level success:$channel")
                    iAdListener.onAdPrepared(channel, adWrapper)
                }

                override fun onStartRequest(channel: String, key: String) {//每个请求的开始
                    iAdListener.onStartRequest(channel, key)
                }

                override fun onAdClick(channel: String, key: String) {
                    iAdListener.onAdClick(channel, key)
                }

                override fun onAdFailed(failedMsg: String?, key: String) {//每个请求的结束
                    loge("AbstractAdHelp:$adConstStr level:$level failed:$failedMsg")
                    if (level >= levelHorizontal) {
                        iAdListener.onAdFailed(failedMsg, key)
                    } else {
                        level++
                        requestAdByLevel()
                    }

                }

            }, level)

        }
        //开始请求
        requestAdByLevel()
    }

    /**
     * 无论横竖向都会走到这开始进行广告随机
     * @param configStr String?
     * @param adListener IAdListener
     * @param level Int 横向请求时区分第几级，竖向为-1
     * @return Unit
     */
    protected fun requestAdVertical(
        configStr: String?,
        @NonNull adListener: IAdListener,
        level: Int = -1
    ) {
        val randomAdName = AdRandomUtil.getRandomAdName(configStr)
        when (randomAdName) {
            AdNameType.NO -> {
                adListener.onAdFailed(TogetherAdSea.context.getString(R.string.all_ad_error), "ALL")
            }
            else -> {
                dispatchAdRequest(randomAdName, level, configStr, 0, adListener)
            }
        }
    }

    /**
     * 内部调用  添加到缓存
     * @param adWrapper AdWrapper
     * @return Unit
     */
    internal fun addtoAdCache(adWrapper: AdWrapper) {
        val list = adCacheMap[adConstStr]
        if (list == null)
            adCacheMap[adConstStr] = mutableListOf(adWrapper)
        else
            list.add(adWrapper)
    }

    fun getAdFromCache(predicate: (AdWrapper) -> Boolean): AdWrapper? {
        val list = adCacheMap[adConstStr]
        return if (list.isNullOrEmpty())
            null
        else
            list.find { predicate.invoke(it) }
    }



    /**
     * 外部广告使用后需要销毁时调用
     * @param adWrapper AdWrapper
     * @return Unit
     */
    fun destoryAd(adWrapper: AdWrapper) {
        adCacheMap[adConstStr]?.remove(adWrapper)
        adWrapper.destory()
    }


    /**
     * 移除缓存
     * @param key String
     * @return Unit
     */
     fun removeAdFromCache(key: String): AdWrapper? {
        return adCacheMap[adConstStr]?.find {
            it.key == key
        }?.apply {
            adCacheMap[adConstStr]?.remove(this)
        }
    }

    /**
     * 根据条件过滤清除广告
     * @param predicate Function1<AdWrapper, Boolean>
     * @return Unit
     */
    fun removeAdFromCache(predicate: (AdWrapper) -> Boolean){
        adCacheMap[adConstStr]?.filter {
            predicate.invoke(it)
        }?.forEach {
            removeAdFromCache(it.key)
        }
    }
    fun getAdByKey(key:String):AdWrapper?{
        return adCacheMap[adConstStr]?.find {
            it.key==key
        }
    }
    /**
     * 退出Activity时调用 主要用于清空监听器
     * @param other 其他需要删除的广告 返回true 表示删除
     * @receiver AdWrapper
     * @return Unit
     */
    fun onDestory(other:(AdWrapper)->Boolean={false}) {
        /**
         * 多页面同时请求同类型，并且其中一方先调用这个方法时  可能会导致另一个页面的广告无法回调
         */
        unUseListenerList.clear()
        adCacheMap[adConstStr]?.filter {
            (it.getOwner()==owner)||other.invoke(it)
        }?.forEach {
            it.resetListener()
        }
    }

    internal abstract fun dispatchAdRequest(
        type: AdNameType,
        level: Int,
        configStr: String?,
        requestIndex: Int,
        adListener: IAdListener
    )

    companion object {
        /**
         * 已加载广告缓存
         */
        internal val adCacheMap = HashMap<String, MutableList<AdWrapper>>()
        /**
         * 正在加载的广告类型 主要用于某些时候每次只能加载一个的时候   防止重复加载
         */
        internal val loadingAdType = HashSet<String>()


        /**
         * 超时key
         */
        internal val timeOutSet = HashSet<String>()//HashSet<Key>
    }

    /**
     * 添加超时的key
     * @param key String
     * @return Unit
     */
    internal fun addTimeOut(key: String) {
        timeOutSet.add(key)
    }

    internal fun isTimeOut(key: String): Boolean {
        return timeOutSet.contains(key)
    }

    /**
     *  ======================== 这堆监听器回调是为了方便子类扩展 =======================
     *  key 一般是 ADhash or loaderhash 区别在于能不能再加载前拿到ad
     *  这是仅次于userListener的回调
     */
    override fun onStartRequest(channel: String, key: String) {
        //这个是否有必要回调

        listenerMap[key]?.get()?.onStartRequest(channel,key)
    }

    override fun onAdClick(channel: String, key: String) {
        listenerMap[key]?.get()?.onAdClick(channel, key)
    }

    override fun onAdFailed(failedMsg: String?, key: String) {
        loadingAdType.remove(adConstStr)
        loadingCount--
        //移出超时
        timeOutSet.remove(key)
        //绑定监听器
        bindListener(key)
        listenerMap[key]?.get()?.onAdFailed(failedMsg, key)
        removeListener(key)
    }

    override fun onAdShow(channel: String, key: String) {
         adCacheMap[adConstStr]?.find {
            it.key == key
        }?.apply {
            showedTime=System.currentTimeMillis() //标记为已展示
        }
        listenerMap[key]?.get()?.onAdShow(channel, key)
    }

    override fun onAdClose(channel: String, key: String, other: Any) {
        listenerMap[key]?.get()?.onAdClose(channel, key, other)
    }

    override fun onAdPrepared(channel: String, adWrapper: AdWrapper) {
        //移出加载中
        loadingAdType.remove(adConstStr)
        loadingCount--
        //移出超时
        timeOutSet.remove(adWrapper.key)
        //绑定监听器
        bindListener(adWrapper.key)
        //加入缓存
        addtoAdCache(adWrapper)
        //回调
        listenerMap[adWrapper.key]?.get()?.onAdPrepared(channel, adWrapper)
    }

    /**
     * 创建超时倒计时
     * @param callback ()->Unit
     * @return CountDownTimer
     */
    protected fun creatTimer(callback: () -> Unit): CountDownTimer {
        return object : CountDownTimer(timeOutMillsecond, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                logv("倒计时: $millisUntilFinished")
            }

            override fun onFinish() {
                callback()
                logv("倒计时: ${context.getString(R.string.dismiss)}")
            }
        }
    }
}
