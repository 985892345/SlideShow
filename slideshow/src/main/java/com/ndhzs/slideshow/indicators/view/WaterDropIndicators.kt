package com.ndhzs.slideshow.indicators.view

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import com.ndhzs.slideshow.indicators.AbstractIndicatorsView
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.pow

/**
 * 基本思路是两个圆点之间的上下方有两个半径很大的圆, 小圆点就在这两个大圆之间被挤压着移动
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/19
 */
class WaterDropIndicators(
  context: Context,
  attrs: AttributeSet,
) : AbstractIndicatorsView(context, attrs) {
  
  private val mPath1 = Path()
  private val mPath2 = Path()
  
  override fun onDrawMovePath(
    path: Path,
    radius: Float,
    offsetPixels: Float,
    intervalMargin: Float,
  ) {
    if (outerX == 0F || outerY == 0F || outerR == 0F) {
      outerX = intervalMargin / 2
      outerY = intervalMargin * 1F // 大圆的 y 坐标, 修改此值会影响大圆半径
      outerR = (outerX.pow(2) + outerY.pow(2)).pow(0.5F) - radius
    }
    drawFirstCircle(mPath1, offsetPixels, intervalMargin)
    path.op(mPath1, Path.Op.UNION)
    mPath1.reset()
    drawSecondCircle(mPath1, offsetPixels, intervalMargin)
    path.op(mPath1, Path.Op.UNION)
    mPath1.reset()
    drawMiddleArea(mPath1, offsetPixels)
    path.op(mPath1, Path.Op.UNION)
    mPath1.reset()
  }
  
  private var outerX = 0F
  private var outerY = 0F
  private var outerR = 0F
  
  private var firstPointX = 0F
  private var firstPointY = 0F
  private fun drawFirstCircle(path: Path, offset: Float, total: Float) {
    val radio = abs(offset / total)
    val startMove = 0.6F
    val k = 1 / (1 - startMove)
    val b = 1 - k
    val y = offset / abs(offset) * max(0F, k * radio + b) * total
    val r = getNewRadius(y)
    val dx = (outerX - abs(y)) / (outerR + r) * r
    val dy = outerY / (outerR + r) * r
    firstPointX = if (offset > 0) dx + y else -dx + y
    firstPointY = dy
    path.addCircle(y, 0F, r, Path.Direction.CCW)
  }
  
  private var secondPointX = 0F
  private var secondPointY = 0F
  private fun drawSecondCircle(path: Path, offset: Float, total: Float) {
    val r = getNewRadius(offset)
    val dx = (outerX - abs(offset)) / (outerR + r) * r
    val dy = outerY / (outerR + r) * r
    secondPointX = if (offset > 0) dx + offset else -dx + offset
    secondPointY = dy
    path.addCircle(offset, 0F, r, Path.Direction.CCW)
  }
  
  private fun getNewRadius(offset: Float): Float {
    return hypot(outerX - abs(offset), outerY) - outerR
  }
  
  private fun drawMiddleArea(path: Path, offset: Float) {
    mPath2.reset()
    var correctOuterX = outerX
    if (offset < 0) correctOuterX *= -1
    mPath2.addCircle(correctOuterX, outerY, outerR, Path.Direction.CCW)
    mPath2.addCircle(correctOuterX, -outerY, outerR, Path.Direction.CCW)
    
    path.moveTo(firstPointX, firstPointY)
    path.lineTo(firstPointX, -firstPointY)
    path.lineTo(secondPointX, -secondPointY)
    path.lineTo(secondPointX, secondPointY)
    path.close()
    
    path.op(mPath2, Path.Op.DIFFERENCE)
  }
}