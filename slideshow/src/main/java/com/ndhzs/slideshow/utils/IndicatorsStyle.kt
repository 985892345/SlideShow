package com.ndhzs.slideshow.utils

import androidx.annotation.IntDef

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/28
 */
class IndicatorsStyle {
    companion object {
        const val NULL_VIEW = 0
        const val NORMAL = 1
    }
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        NULL_VIEW, NORMAL
    )
    annotation class Style
}