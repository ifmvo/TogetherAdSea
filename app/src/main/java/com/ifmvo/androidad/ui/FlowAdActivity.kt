package com.ifmvo.androidad.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ifmvo.androidad.R

/* 
 * (●ﾟωﾟ●) 原生 列表中展示
 * 
 * Created by Matthew_Chen on 2019-07-05.
 */
class FlowAdActivity : AppCompatActivity() {

    companion object {
        fun action(context: Context) {
            context.startActivity(Intent(context, FlowAdActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_common)

        val bt = supportFragmentManager.beginTransaction()
        bt.replace(R.id.fgContainer, FlowAdFragment())
        bt.commit()
    }

}