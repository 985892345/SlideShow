package com.ndhzs.slideshow.layout.view

import android.content.Context
import android.graphics.Path
import com.ndhzs.slideshow.layout.AbstractIndicatorsView

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/7/16
 */
class FlashIndicators(
    context: Context
) : AbstractIndicatorsView(context)  {
    override fun onDrawMovePath(
        path: Path,
        radius: Float,
        offsetPixels: Float,
        intervalMargin: Float,
    ) {
        path.addCircle(0F, 0F, radius, Path.Direction.CCW)
    }
}