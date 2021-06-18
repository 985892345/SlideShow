package com.ndhzs.slideshow.utils

import android.content.res.TypedArray
import com.ndhzs.slideshow.R

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/17
 */
class IndicatorsAttrs private constructor() {

    var isShowIndicators = true
        internal set
    @Indicators.Style
    var indicatorStyle = Indicators.Style.NO_SHOW
        internal set
    @Indicators.OuterGravity
    var indicatorOuterGravity = Indicators.OuterGravity.BOTTOM
        internal set
    @Indicators.InnerGravity
    var indicatorInnerGravity = Indicators.InnerGravity.CENTER
        internal set

    var indicatorRadius = 20F
        internal set
    var indicatorColor = 0xFFFAFAFA.toInt()
        internal set
    var indicatorBannerColor = 0x00000000
        internal set

    internal fun initialize(ty: TypedArray) {
        isShowIndicators = ty.getBoolean(R.styleable.SlideShow_slide_isShowIndicators, isShowIndicators)
        indicatorStyle = ty.getInt(R.styleable.SlideShow_slide_indicatorsStyle, indicatorStyle)
        indicatorOuterGravity = ty.getInt(R.styleable.SlideShow_slide_indicatorsOuterGravity, indicatorOuterGravity)
        indicatorInnerGravity = ty.getInt(R.styleable.SlideShow_slide_indicatorsOuterGravity, indicatorInnerGravity)
        indicatorRadius = ty.getDimension(R.styleable.SlideShow_slide_indicatorsRadius, indicatorRadius)
        indicatorColor = ty.getColor(R.styleable.SlideShow_slide_indicatorsColor, indicatorColor)
        indicatorBannerColor = ty.getColor(R.styleable.SlideShow_slide_indicatorsBannerColor, indicatorBannerColor)
        setAttrs()
    }

    private fun setAttrs() {
    }

    class Builder {

        private val mAttrs = IndicatorsAttrs()

        /**
         * 设置指示器的横幅背景颜色
         */
        fun setIndicatorsBannerColor(color: Int): Builder {
            mAttrs.indicatorBannerColor = color
            return this
        }

        /**
         * 设置指示器的圆点颜色
         */
        fun setIndicatorsColor(color: Int): Builder {
            mAttrs.indicatorColor = color
            return this
        }

        /**
         * 设置指示器的圆点半径
         */
        fun setIndicatorsRadius(radius: Float): Builder {
            mAttrs.indicatorRadius = radius
            return this
        }

        /**
         * 设置指示器外部的位置（整个横幅）
         *
         * @param gravity 数据来自 [Indicators.OuterGravity]
         */
        fun setIndicatorsOuterGravity(@Indicators.OuterGravity gravity: Int): Builder {
            mAttrs.indicatorOuterGravity = gravity
            return this
        }

        /**
         * 设置指示器内部的位置（小圆点）
         *
         * @param gravity 数据来自 [Indicators.InnerGravity]
         */
        fun setIndicatorsInnerGravity(@Indicators.InnerGravity gravity: Int): Builder {
            mAttrs.indicatorOuterGravity = gravity
            return this
        }

        /**
         * 设置指示器的样式
         *
         * @see style 数据来自 [Indicators.Style]
         */
        fun setIndicatorsStyle(@Indicators.Style style: Int): Builder {
            mAttrs.indicatorStyle = style
            return this
        }

        fun build(): IndicatorsAttrs {
            mAttrs.setAttrs()
            return mAttrs
        }
    }
}