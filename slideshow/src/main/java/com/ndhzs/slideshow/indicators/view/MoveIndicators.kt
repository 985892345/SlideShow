package com.ndhzs.slideshow.indicators.view

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import com.ndhzs.slideshow.indicators.AbstractIndicatorsView

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/15
 */
class MoveIndicators(
  context: Context,
  attrs: AttributeSet,
) : AbstractIndicatorsView(context, attrs) {
  
  override fun onDrawMovePath(
    path: Path,
    radius: Float,
    offsetPixels: Float,
    intervalMargin: Float,
  ) {
    // 这只是一个简单的平移小圆点的动画
    // 其中 offsetPixels 就相当于横坐标 x
    path.addCircle(offsetPixels, 0F, radius, Path.Direction.CCW)
  }
}