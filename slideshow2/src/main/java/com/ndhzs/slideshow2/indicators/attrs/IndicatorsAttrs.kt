package com.ndhzs.slideshow2.indicators.attrs

import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.GravityInt
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow2.R
import com.ndhzs.slideshow2.base.BaseViewAttrs
import com.ndhzs.slideshow2.base.BaseViewAttrs.Companion.newAttrs

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/23 17:05
 */
open class IndicatorsAttrs(
  @ViewPager2.Orientation
  val orientation: Int = ORIENTATION,
  @Dimension
  var circleRadius: Float = CIRCLE_RADIUS,
  @ColorInt
  var circleColor: Int = CIRCLE_COLOR,
  @Dimension
  var intervalMargin: Float = INTERVAL_MARGIN,
  @ColorInt
  var circleBackground: Int = CIRCLE_BACKGROUND,
  @GravityInt
  var gravity: Int = Gravity.CENTER,
  @Dimension
  var paddingTop: Int = 0,
  @Dimension
  var paddingBottom: Int = 0,
  @Dimension
  var paddingLeft: Int = 0,
  @Dimension
  var paddingRight: Int = 0
) : BaseViewAttrs {
  
  companion object {
    fun newInstance(
      view: View,
      attrs: AttributeSet?,
      defStyleAttr: Int,
      defStyleRes: Int,
    ): IndicatorsAttrs {
      return newAttrs(
        view,
        attrs,
        R.styleable.AbstractIndicatorsView,
        defStyleAttr,
        defStyleRes
      ) {
        IndicatorsAttrs(
          R.styleable.AbstractIndicatorsView_indicators_orientation.int(ORIENTATION),
          R.styleable.AbstractIndicatorsView_indicators_circleRadius.dimens(CIRCLE_RADIUS),
          R.styleable.AbstractIndicatorsView_indicators_circleColor.color(CIRCLE_COLOR),
          R.styleable.AbstractIndicatorsView_indicators_intervalMargin.dimens(
            INTERVAL_MARGIN
          ),
          R.styleable.AbstractIndicatorsView_indicators_circleBackground.color(
            CIRCLE_BACKGROUND
          ),
          R.styleable.AbstractIndicatorsView_indicators_gravity.int(Gravity.CENTER),
          R.styleable.AbstractIndicatorsView_indicators_paddingTop.dimens(0),
          R.styleable.AbstractIndicatorsView_indicators_paddingBottom.dimens(0),
          R.styleable.AbstractIndicatorsView_indicators_paddingLeft.dimens(0),
          R.styleable.AbstractIndicatorsView_indicators_paddingRight.dimens(0)
        )
      }
    }
    
    private const val ORIENTATION = ViewPager2.ORIENTATION_HORIZONTAL
    
    /**
     * 指示器圆点半径大小默认值
     */
    private const val CIRCLE_RADIUS = 16F
    
    /**
     * 指示器圆点颜色默认值
     */
    private const val CIRCLE_COLOR = 0xFFFAFAFA.toInt()
    
    /**
     * 指示器两个圆点间的距离默认值
     */
    private const val INTERVAL_MARGIN = 88F
    
    /**
     * 指示器小圆点背景颜色默认值
     */
    private const val CIRCLE_BACKGROUND = 0x8E8E8E8E.toInt()
  }
}