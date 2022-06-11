package com.ndhzs.slideshow2.attrs

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.Dimension
import androidx.annotation.LayoutRes
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow2.R
import com.ndhzs.slideshow2.base.BaseViewAttrs
import com.ndhzs.slideshow2.base.BaseViewAttrs.Companion.newAttrs

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
    internal var orientation: Int = ORIENTATION,
    @Dimension
    internal var frameDistance: Int = FRAME_DISTANCE,
    @Dimension
    internal var pageDistance: Int = PAGE_DISTANCE,
    internal var isAutoSlide: Boolean = false,
    internal var isCyclical: Boolean = false,
    internal var autoSlideDuration: Int = AUTO_SLIDE_DURATION,
    internal var autoSlideIntervalDt: Int = AUTO_SLIDE_INTERVAL_DT,
) : BaseViewAttrs {

    companion object {

        fun newInstance(
            view: View,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
        ) : SlideShowAttrs {
            val ty = view.context.obtainStyledAttributes(attrs, R.styleable.ViewPager2, defStyleAttr, defStyleRes)
            val orientation = ty.getInt(R.styleable.ViewPager2_android_orientation, ORIENTATION)
            ty.recycle()
            return newAttrs(view, attrs, R.styleable.SlideShow2, defStyleAttr, defStyleRes) {
                SlideShowAttrs(
                    orientation,
                    R.styleable.SlideShow2_show_frameDistance.dimens(FRAME_DISTANCE),
                    R.styleable.SlideShow2_show_pageDistance.dimens(PAGE_DISTANCE),
                    R.styleable.SlideShow2_show_isAutoSlide.boolean(false),
                    R.styleable.SlideShow2_show_isCyclical.boolean(false),
                    R.styleable.SlideShow2_show_autoSlideDuration.int(AUTO_SLIDE_DURATION),
                    R.styleable.SlideShow2_show_autoSlideIntervalDt.int(AUTO_SLIDE_INTERVAL_DT)
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