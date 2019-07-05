package com.ifmvo.androidad.ui

import com.chad.library.adapter.base.entity.MultiItemEntity

/* 
 * (●ﾟωﾟ●) 多类型列表的 Bean
 * 
 * Created by Matthew_Chen on 2019-07-05.
 */
class MutitypeBean(

    val type: Int,
    val adOrContentObject: Any

) : MultiItemEntity {

    override fun getItemType(): Int {
        return type
    }

    companion object {
        const val type_facebook = 10
        const val type_google = 11
        const val type_content = 12
    }
}

/**
 * 列表的主要内容 Bean
 */
class MyContentBean(

    val imgRes: Int,
    val txtTitle: String

)