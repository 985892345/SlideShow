package com.ndhzs.slideshow.indicators.view

import android.content.Context
import android.graphics.Path
import com.ndhzs.slideshow.indicators.AbstractIndicatorsView

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/15
 */
class CommonIndicators(
    context: Context,
) : AbstractIndicatorsView(context) {

    override fun onDrawMovePath(
        path: Path,
        radius: Float,
        offsetPixels: Float,
        intervalMargin: Float
    ) {
        path.addCircle(offsetPixels, 0F, radius, Path.Direction.CCW)
    }
}