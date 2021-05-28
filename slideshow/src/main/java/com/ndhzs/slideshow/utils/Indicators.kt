package com.ndhzs.slideshow.utils

import androidx.annotation.IntDef

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/28
 */
class Indicators {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        IndicatorsStyle.NULL_VIEW,
        IndicatorsStyle.NORMAL
    )
    annotation class Style

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        IndicatorsGravity.TOP,
        IndicatorsGravity.BOTTOM,
        IndicatorsGravity.LEFT,
        IndicatorsGravity.RIGHT,
        IndicatorsGravity.TOP_LEFT,
        IndicatorsGravity.TOP_CENTER,
        IndicatorsGravity.TOP_RIGHT,
        IndicatorsGravity.BOTTOM_LEFT,
        IndicatorsGravity.BOTTOM_CENTER,
        IndicatorsGravity.BOTTOM_RIGHT,
        IndicatorsGravity.LEFT_CENTER,
        IndicatorsGravity.RIGHT_CENTER,
    )
    annotation class Gravity
}