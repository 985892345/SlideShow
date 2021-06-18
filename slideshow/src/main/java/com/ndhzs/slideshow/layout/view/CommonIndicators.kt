package com.ndhzs.slideshow.layout.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Path
import com.ndhzs.slideshow.layout.AbstractIndicatorsView

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/15
 */
@SuppressLint("ViewConstructor")
class CommonIndicators(
    context: Context,
    private val backgroundCircleColor: Int = 0x8E8E8E8E.toInt(),
    private val movePathColor: Int = 0xFFEAEAEA.toInt(),
) : AbstractIndicatorsView(context) {

    override fun onDrawMovePath(
        path: Path,
        radius: Float,
        offsetPixels: Float,
        intervalMargin: Float
    ) {
        path.addCircle(offsetPixels, 0F, radius, Path.Direction.CCW)
    }

    override fun getBackgroundCircleColor(): Int {
        return backgroundCircleColor
    }

    override fun getMovePathColor(): Int {
        return movePathColor
    }
}