package com.ndhzs.slideshow.utils

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow.R
import com.ndhzs.slideshow.myinterface.IIndicator

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/27
 */
class SlideShowAttrs private constructor(){

    companion object {
        internal const val Library_name = "SlideShow"
    }

    var imgWidth = ViewGroup.LayoutParams.MATCH_PARENT
        internal set
    var imgHeight = ViewGroup.LayoutParams.MATCH_PARENT
        internal set
    var imgDefaultColor = 0xFFFAFAFA.toInt()
        internal set

    private var imgMargin = 0
    var imgMarginHorizontal = 0
        internal set
    var imgMarginVertical = 0
        internal set
    var adjacentPageInterval = -1
        internal set
    var outPageInterval = -1
        internal set

    private var imgRadius = 0F
    var imgLeftTopRadius = 0F
        internal set
    var imgRightTopRadius = 0F
        internal set
    var imgLeftBottomRadius = 0F
        internal set
    var imgRightBottomRadius = 0F
        internal set

    @ViewPager2.Orientation
    var orientation = ViewPager2.ORIENTATION_HORIZONTAL
        internal set

    /**
     * 该值还需进行一次转换才是相邻页面边距值
     */
    internal var pageInterval = 0

    lateinit var mIndicatorsAttrs: IndicatorsAttrs
        internal set

    internal fun initialize(context: Context, attrs: AttributeSet) {
        val ty = context.obtainStyledAttributes(attrs, R.styleable.SlideShow)

        imgWidth = ty.getLayoutDimension(R.styleable.SlideShow_slide_imgWight, ViewGroup.LayoutParams.MATCH_PARENT)
        imgHeight = ty.getLayoutDimension(R.styleable.SlideShow_slide_imgHeight, ViewGroup.LayoutParams.MATCH_PARENT)

        imgDefaultColor = ty.getColor(R.styleable.SlideShow_slide_imgDefaultColor, imgDefaultColor)

        imgMargin = ty.getDimension(R.styleable.SlideShow_slide_imgMargin, 0F).toInt()
        imgMarginHorizontal = ty.getDimension(R.styleable.SlideShow_slide_imgMarginHorizontal, 0F).toInt()
        imgMarginVertical = ty.getDimension(R.styleable.SlideShow_slide_imgMarginVertical, 0F).toInt()


        orientation = ty.getInt(R.styleable.SlideShow_slide_orientation, orientation)

        adjacentPageInterval = ty.getDimension(R.styleable.SlideShow_slide_adjacentPageInterval, -1F).toInt()
        outPageInterval = ty.getDimension(R.styleable.SlideShow_slide_outPageInterval, -1F).toInt()

        imgRadius = ty.getDimension(R.styleable.SlideShow_slide_imgRadius, 0F)
        imgLeftTopRadius = ty.getDimension(R.styleable.SlideShow_slide_imgLeftTopRadius, imgLeftTopRadius)
        imgRightTopRadius = ty.getDimension(R.styleable.SlideShow_slide_imgRightTopRadius, imgRightTopRadius)
        imgLeftBottomRadius = ty.getDimension(R.styleable.SlideShow_slide_imgLeftBottomRadius, imgLeftBottomRadius)
        imgRightBottomRadius = ty.getDimension(R.styleable.SlideShow_slide_imgRightBottomRadius, imgRightBottomRadius)

//        pageInterval = ty.getDimension(R.styleable.SlideShow_slide_pageInterval, pageInterval)

        // 以下为指示器的属性
        mIndicatorsAttrs = IndicatorsAttrs.Builder().build()
        mIndicatorsAttrs.initialize(ty)

        ty.recycle()
        setAttrs()
    }

    internal fun setAttrs() {
        if (imgMargin != 0) {
            imgMarginHorizontal = imgMargin
            imgMarginVertical = imgMargin
        }
        if (adjacentPageInterval == -1 && outPageInterval != -1) { // 这种情况下只是单独设置内部页面到外部页面的值，与设置 imgMarginHorizontal 效果一样
            if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                imgMarginHorizontal = outPageInterval
            }else {
                imgMarginVertical = outPageInterval
            }
        }else if (adjacentPageInterval != -1 && outPageInterval == -1) { // 这种情况下只有当宽度不为 match_parent 时才允许设置相邻边距
            if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (imgWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                    throw IllegalAccessException(
                            "Your $Library_name: " +
                                    "When slide_imgWidth is match_parent, " +
                                    "you must set slide_outPageInterval after setting the slide_adjacentPageInterval!")
                }
            }else {
                if (imgHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                    throw IllegalAccessException(
                            "Your $Library_name: " +
                                    "When slide_imgHeight is match_parent, " +
                                    "you must set slide_outPageInterval after setting the slide_adjacentPageInterval!")
                }
            }
            pageInterval = adjacentPageInterval
        }else if (adjacentPageInterval != -1 && outPageInterval != -1) {
            if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (imgWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                    imgMarginHorizontal = adjacentPageInterval / 2
                    pageInterval = outPageInterval - imgMarginHorizontal
                    if (pageInterval < 0) {
                        throw IllegalAccessException(
                                "Your $Library_name: " +
                                        "slide_outPageInterval must > slide_adjacentPageInterval / 2 !")
                    }
                }else {
                    pageInterval = adjacentPageInterval
                }
            }else {
                if (imgHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                    imgMarginVertical = adjacentPageInterval / 2
                    pageInterval = outPageInterval / imgMarginVertical
                    if (pageInterval < 0) {
                        throw IllegalAccessException(
                                "Your $Library_name: " +
                                        "slide_outPageInterval must > slide_adjacentPageInterval / 2 !")
                    }
                }else {
                    pageInterval = adjacentPageInterval
                }
            }
        }
        if (imgRadius != 0F) {
            imgLeftTopRadius = imgRadius
            imgRightTopRadius = imgRadius
            imgLeftBottomRadius = imgRadius
            imgRightBottomRadius = imgRadius
        }
    }

    class Builder {

        private val mAttrs = SlideShowAttrs()

        /**
         * 使用自带的图片加载的 setAdapter 方法后可以调用，用于设置内部 ImageView 的圆角
         */
        fun setImgRadius(radius: Float): Builder {
            mAttrs.imgLeftTopRadius = radius
            mAttrs.imgRightTopRadius = radius
            mAttrs.imgLeftBottomRadius = radius
            mAttrs.imgRightBottomRadius = radius
            return this
        }

        /**
         * 使用自带的图片加载的 setAdapter 方法后可以调用，用于设置内部 ImageView 的圆角
         */
        fun setImgRadius(leftTop: Float, rightTop: Float, leftBottom: Float, rightBottom: Float): Builder {
            mAttrs.imgLeftTopRadius = leftTop
            mAttrs.imgRightTopRadius = rightTop
            mAttrs.imgLeftBottomRadius = leftBottom
            mAttrs.imgRightBottomRadius = rightBottom
            return this
        }

        /**
         * 使用自带的图片加载的 setAdapter 方法后可以调用，用于设置内部 ImageView 的默认颜色
         */
        fun setImgDefaultColor(color: Int): Builder {
            mAttrs.imgDefaultColor = color
            return this
        }

        /**
         * -1 为 match_parent，-2 为 wrap_content
         */
        fun setImgWidth(pixel: Int): Builder {
            if (pixel < -2) {
                throw IllegalAccessException(
                    "Your $Library_name#setImgWidth(): The pixel is < -2")
            }
            mAttrs.imgWidth = pixel
            return this
        }

        /**
         * -1 为 match_parent，-2 为 wrap_content
         */
        fun setImgHeight(pixel: Int): Builder {
            if (pixel < -2) {
                throw IllegalAccessException(
                    "Your $Library_name#setImgHeight(): The pixel is < -2")
            }
            mAttrs.imgHeight = pixel
            return this
        }

        fun setImgMargin(imgMargin: Int): Builder {
            mAttrs.imgMargin = imgMargin
            return this
        }

        /**
         * **WARNING：** 如果在水平滑动时 imgWidth 不为 match_parent 或者设置了 slide_outPageInterval，设置 imgMarginHorizontal 将无效
         */
        fun setImgMarginHorizontal(imgMarginHorizontal: Int): Builder {
            mAttrs.imgMarginHorizontal = imgMarginHorizontal
            return this
        }

        /**
         * **WARNING：** 如果在垂直滑动时 imgHeight 不为 match_parent 或者设置了 slide_outPageInterval，设置 imgMarginVertical 将无效
         */
        fun setImgMarginVertical(imgMarginVertical: Int): Builder {
            mAttrs.imgMarginVertical = imgMarginVertical
            return this
        }

        /**
         * 设置内部 ViewPager2 的 orientation
         * @param orientation 数据来自 [ViewPager2.ORIENTATION_HORIZONTAL]、[ViewPager2.ORIENTATION_VERTICAL]
         */
        fun setOrientation(@ViewPager2.Orientation orientation: Int): Builder {
            mAttrs.orientation = orientation
            return this
        }

        /**
         * 设置相邻内部页面边距和内部页面与外部页面
         *
         * **WARNING：** 如果在水平滑动时 imgWidth 或在垂直滑动时 imgHeight 不为 match_parent，设置 [outPageInterval] 将无效
         *
         * **WARNING：** 只有在未加载视图时设置才有效
         *
         * @param adjacentPageInterval 相邻页面间距
         * @param outPageInterval 内部页面于外部页面的边距
         */
        fun setPageInterval(adjacentPageInterval: Int, outPageInterval: Int): Builder {
            mAttrs.adjacentPageInterval = adjacentPageInterval
            mAttrs.outPageInterval = outPageInterval
            mAttrs.pageInterval = outPageInterval - adjacentPageInterval / 2
            if (mAttrs.pageInterval < 0) {
                throw IllegalAccessException(
                    "Your $Library_name#setPageInterval(): " +
                            "outPageInterval must > adjacentPageInterval / 2 !")
            }
            return this
        }

        /**
         * 设置指示器
         */
        fun setIndicators(indicatorsAttrs: IndicatorsAttrs): Builder {
            mAttrs.mIndicatorsAttrs = indicatorsAttrs
            return this
        }

        fun build(): SlideShowAttrs {
            mAttrs.setAttrs()
            return mAttrs
        }
    }
}