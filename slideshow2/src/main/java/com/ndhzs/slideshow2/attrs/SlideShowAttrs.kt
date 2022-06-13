package com.ndhzs.slideshow2.attrs

import android.util.AttributeSet
import android.view.View
import androidx.annotation.Dimension
import androidx.annotation.IdRes
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow2.R
import com.ndhzs.slideshow2.base.BaseViewAttrs
import com.ndhzs.slideshow2.base.BaseViewAttrs.Companion.newAttrs
import com.ndhzs.slideshow2.base.int

/**
 *
 *
 * @param orientation 滑动方向
 * @param frameDistance 页面到边框的距离
 * @param pageDistance 相邻页面间的距离
 * @param isAutoSlide 是否自动滚动
 * @param isCyclical 是否开启循环
 * @param autoSlideDuration 一次滑动需消耗的时间
 * @param autoSlideIntervalDt 下一次滑动的间隔时间
 * @param
 * @param
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/22 10:50
 */
class SlideShowAttrs(
  @ViewPager2.Orientation
  internal val orientation: Int = ORIENTATION,
  @Dimension
  internal val frameDistance: Int = FRAME_DISTANCE,
  @Dimension
  internal val pageDistance: Int = PAGE_DISTANCE,
  internal var isAutoSlide: Boolean = false,
  internal var isCyclical: Boolean = false,
  @IntRange(from = 0, to = Int.MAX_VALUE.toLong())
  internal var autoSlideDuration: Int = AUTO_SLIDE_DURATION,
  @IntRange(from = 0, to = Int.MAX_VALUE.toLong())
  internal var autoSlideIntervalDt: Int = AUTO_SLIDE_INTERVAL_DT,
  @IdRes
  internal val indicatorsViewId: Int = View.NO_ID,
  @Dimension
  internal var marginTop: Int = 0,
  @Dimension
  internal var marginBottom: Int = 0,
) : BaseViewAttrs {
  
  companion object {
    fun newInstance(
      view: View,
      attrs: AttributeSet?,
      defStyleAttr: Int,
      defStyleRes: Int,
    ): SlideShowAttrs {
      return newAttrs(view, attrs, R.styleable.SlideShow2, defStyleAttr, defStyleRes) {
        SlideShowAttrs(
          R.styleable.SlideShow2_show_orientation.int(ORIENTATION),
          R.styleable.SlideShow2_show_frameDistance.dimens(FRAME_DISTANCE),
          R.styleable.SlideShow2_show_pageDistance.dimens(PAGE_DISTANCE),
          R.styleable.SlideShow2_show_isAutoSlide.boolean(false),
          R.styleable.SlideShow2_show_isCyclical.boolean(false),
          R.styleable.SlideShow2_show_autoSlideDuration.int(AUTO_SLIDE_DURATION),
          R.styleable.SlideShow2_show_autoSlideIntervalDt.int(AUTO_SLIDE_INTERVAL_DT),
          R.styleable.SlideShow2_show_indicatorsViewId.int(View.NO_ID),
          R.styleable.SlideShow2_show_marginTop.dimens(0),
          R.styleable.SlideShow2_show_marginBottom.dimens(0),
        )
      }
    }
    
    const val ORIENTATION = ViewPager2.ORIENTATION_HORIZONTAL
    const val FRAME_DISTANCE = 0
    const val PAGE_DISTANCE = 0
    const val AUTO_SLIDE_DURATION = 1000
    const val AUTO_SLIDE_INTERVAL_DT = 4000
  }
}