package com.liangzemu.ad.sea

import android.app.ListActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import com.liangzemu.ad.sea.other.TogetherAdSeaSP

/* 
 * (●ﾟωﾟ●) 查看单次启动的日志
 * 
 * Created by Matthew_Chen on 2019-06-06.
 */
class TogetherAdLogActivity : ListActivity() {

    companion object {
        fun action(context: Context) {
            context.startActivity(Intent(context, TogetherAdLogActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val keyList = mutableListOf<String>()
        TogetherAdSeaSP.getInstance(this).allMap.keys.forEach {
            if (it is String) {
                keyList.add(it)
            }
        }
        keyList.sort()

        val valueList = mutableListOf<String>()
        keyList.forEach {
            valueList.add(TogetherAdSeaSP.getInstance(this).getString(it, ""))
        }

        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, valueList)
    }
}