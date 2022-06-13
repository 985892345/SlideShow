package com.ndhzs.slideshow2.indicators.view

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import com.ndhzs.slideshow2.indicators.AbstractIndicatorsView

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/7/16
 */
class FlashIndicators(
  context: Context,
  attrs: AttributeSet,
) : AbstractIndicatorsView(context, attrs) {
  
  override fun onDrawMovePath(
    path: Path,
    radius: Float,
    offsetPixels: Float,
    intervalMargin: Float,
  ) {
    path.addCircle(0F, 0F, radius, Path.Direction.CCW)
  }
}