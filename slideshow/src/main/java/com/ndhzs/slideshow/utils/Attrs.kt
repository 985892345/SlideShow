package com.ndhzs.slideshow.utils

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow.R

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/27
 */
internal object Attrs {

    const val Library_name = "SlideShow"

    var imgWidth = -1
    var imgHeight = -1

    var imgMarginHorizontal = 0
    var imgMarginVertical = 0

    var imgLeftTopRadius = 0F
    var imgRightTopRadius = 0F
    var imgLeftBottomRadius = 0F
    var imgRightBottomRadius = 0F

    var orientation = ViewPager2.ORIENTATION_HORIZONTAL

    var pageInterval = 0F

    var isShowIndicators = true
    var indicatorStyle = IndicatorsStyle.NORMAL
    var indicatorGravity = IndicatorsGravity.BOTTOM_CENTER

    var indicatorRadius = 20F
    var indicatorColor = 0xFFFAFAFA.toInt()
    var indicatorBannerColor = 0x66A5A5A5

    fun initialize(context: Context, attrs: AttributeSet) {
        val ty = context.obtainStyledAttributes(attrs, R.styleable.SlideShow)

        imgWidth = ty.getDimension(R.styleable.SlideShow_slide_imgWight, -1F).toInt()
        imgHeight = ty.getDimension(R.styleable.SlideShow_slide_imgHeight, -1F).toInt()

        val imgMargin = ty.getDimension(R.styleable.SlideShow_slide_imgMargin, 0F).toInt()
        if (imgMargin == 0) {
            imgMarginHorizontal = ty.getDimension(R.styleable.SlideShow_slide_imgMarginHorizontal, 0F).toInt()
            imgMarginVertical = ty.getDimension(R.styleable.SlideShow_slide_imgMarginVertical, 0F).toInt()
        }else {
            imgMarginHorizontal = imgMargin
            imgMarginVertical = imgMargin
        }

        val imgRadius = ty.getDimension(R.styleable.SlideShow_slide_imgRadius, 0F)
        if (imgRadius == 0F) {
            imgLeftTopRadius = ty.getDimension(R.styleable.SlideShow_slide_imgLeftTopRadius, imgLeftTopRadius)
            imgRightTopRadius = ty.getDimension(R.styleable.SlideShow_slide_imgRightTopRadius, imgRightTopRadius)
            imgLeftBottomRadius = ty.getDimension(R.styleable.SlideShow_slide_imgLeftBottomRadius, imgLeftBottomRadius)
            imgRightBottomRadius = ty.getDimension(R.styleable.SlideShow_slide_imgRightBottomRadius, imgRightBottomRadius)
        }else {
            imgLeftTopRadius = imgRadius
            imgRightTopRadius = imgRadius
            imgLeftBottomRadius = imgRadius
            imgRightBottomRadius = imgRadius
        }

        orientation = ty.getInt(R.styleable.SlideShow_slide_orientation, orientation)

        pageInterval = ty.getDimension(R.styleable.SlideShow_slide_pageInterval, pageInterval)

        isShowIndicators = ty.getBoolean(R.styleable.SlideShow_slide_isShowIndicators, isShowIndicators)
        if (isShowIndicators) {
            val indicatorGravity =
                ty.getInt(R.styleable.SlideShow_slide_indicatorsGravity, indicatorGravity)
            if (IndicatorsGravity.isError(indicatorGravity)) {
                throw IllegalAccessException("Your $Library_name of indicatorGravity is error!")
            } else {
                this.indicatorGravity = indicatorGravity
            }
            indicatorStyle = ty.getInt(R.styleable.SlideShow_slide_indicatorsStyle, indicatorStyle)
            indicatorRadius =
                ty.getDimension(R.styleable.SlideShow_slide_indicatorsRadius, indicatorRadius)
            indicatorColor = ty.getColor(R.styleable.SlideShow_slide_indicatorsColor, indicatorColor)
            indicatorBannerColor =
                ty.getColor(R.styleable.SlideShow_slide_indicatorsBannerColor, indicatorBannerColor)
        }
        ty.recycle()
    }
}