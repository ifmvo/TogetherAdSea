package com.ifmvo.androidad.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ifmvo.androidad.R
import kotlinx.android.synthetic.main.activity_readme.*

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-07-05.
 */
class ReadmeActivity : AppCompatActivity() {

    companion object {
        fun action(context: Context) {
            context.startActivity(Intent(context, ReadmeActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_readme)
        val txt = """

Google 广告的 AppId 在 AndroidManifest.xml 和 Application 初始化 两个地方都要配置

-----------------------------------------------------

请求 Facebook 广告的前提：
1. VPN
2. 安装 Facebook App
3. Facebook App 必须登录

-----------------------------------------------------

Facebook 广告 后台可以添加测试设备，也可在初始化的时候设置 TestMode = true

-----------------------------------------------------

Google 广告 可以在初始化的时候设置 测试设备ID，请求 Google 广告的时候可以在 Logcat 中看到：
I/Ads: Use AdRequest.Builder.addTestDevice("69758C95501DD877201C4F23EEC6E3FD") to get test ads on this device.

-----------------------------------------------------

Google AdMob 插页广告无法关闭、激励广告倒计时不走的情况是因为在广告展示之后 webView.pauseTimers() 会应用级别暂停所有 WebView 的 Js 执行，对广告造成干扰

-----------------------------------------------------

Facebook 广告的上面不能有任何 View 将其盖住，否则会影响广告的曝光、并且广告无法点击。ViewGroup 可以。

-----------------------------------------------------

Google AdMob 错误码对照：
public static final int ERROR_CODE_INTERNAL_ERROR
Something happened internally; for instance, an invalid response was received from the ad server.
Constant Value: 0

public static final int ERROR_CODE_INVALID_REQUEST
The ad request was invalid; for instance, the ad unit ID was incorrect.
Constant Value: 1

public static final int ERROR_CODE_NETWORK_ERROR
The ad request was unsuccessful due to network connectivity.
Constant Value: 2

public static final int ERROR_CODE_NO_FILL
The ad request was successful, but no ad was returned due to lack of ad inventory.
Constant Value: 3

-----------------------------------------------------

Facebook 广告在列表中展示的时候，要相对内容明显区分，否则 Facebook 会以诱导用户误点 的原因停止该广告位的使用
例：广告加一个不一样的背景颜色区分等...

-----------------------------------------------------

Google 广告 文档说："横幅广告不能距离用户操作区域太近"

-----------------------------------------------------

Facebook 规定：只有游戏类型的应用才能使用激励广告 (●ﾟωﾟ●)

-----------------------------------------------------

为了提高广告的展示次数，可以提前缓存广告，Google、Facebook 也建议这样做

-----------------------------------------------------


        """.trimIndent()

        tv.text = txt
    }

}