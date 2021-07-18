package com.ndhzs.slideshow.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow.myinterface.IIndicator
import com.ndhzs.slideshow.indicators.utils.Indicators
import com.ndhzs.slideshow.indicators.utils.IndicatorsAttrs
import com.ndhzs.slideshow.utils.SlideShowUtils

/**
 * 如果你想实现自己的指示器，可以继承于该抽象类
 *
 * 继承后，你只需要实现 [onDrawMovePath] 方法即可，该方法可以实现一个区间的轨迹绘制而实现全部轨迹的绘制
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/28
 */
abstract class AbstractIndicatorsView(
    context: Context,
) : View(context), IIndicator {

    /**
     * 设置为 wrap_content 时的厚度值
     */
    var wrapWidth = WRAP_WIDTH
        private set

    /**
     * 圆点颜色
     */
    var circleColor = CIRCLE_COLOR
        private set

    /**
     * 圆点的半径值
     */
    var circleRadius = CIRCLE_RADIUS
        private set

    /**
     * 两个圆点间的距离值
     */
    var intervalMargin = INTERVAL_MARGIN
        private set

    /**
     * 小圆点背景的颜色
     */
    var backgroundCircleColor = BACKGROUND_CIRCLE_COLOR
        private set

    /**
     * 设置指示器属性
     */
    internal fun setIndicatorsAttrs(attrs: IndicatorsAttrs) {
        indicatorsAttrs = attrs
        wrapWidth = attrs.indicatorWrapWidth
        circleColor = attrs.indicatorCircleColor
        circleRadius = attrs.indicatorCircleRadius
        intervalMargin = attrs.intervalMargin
        backgroundCircleColor = attrs.indicatorBackgroundCircleColor
    }

    companion object {

        /**
         * 指示器横幅最小边的宽度默认值
         */
        val WRAP_WIDTH = SlideShowUtils.dpToPx(30)

        /**
         * 指示器圆点颜色默认值
         */
        const val CIRCLE_COLOR = 0xFFFAFAFA.toInt()

        /**
         * 指示器圆点半径大小默认值
         */
        val CIRCLE_RADIUS = SlideShowUtils.dpToPx(3)

        /**
         * 指示器两个圆点间的距离默认值
         */
        val INTERVAL_MARGIN = SlideShowUtils.dpToPx(18)

        /**
         * 指示器横幅背景颜色默认值
         */
        const val BACKGROUND_COLOR = 0x00000000

        /**
         * 指示器小圆点背景颜色默认值
         */
        const val BACKGROUND_CIRCLE_COLOR = 0x8E8E8E8E.toInt()
    }

    private lateinit var indicatorsAttrs: IndicatorsAttrs

    private var amount = 0
    private var frontMargin = 0F

    /**
     * 圆点位置
     */
    private var position = 0

    /**
     * 圆点上一次停留时的位置
     */
    private var idlePosition = 0

    /**
     * 圆点的位置（带小数）
     */
    private var positionFloat = 0F

    /**
     * 用于 [onDrawMovePath] 中，是该点的像素偏离值，只会在 -intervalMargin 到 +intervalMargin 之间
     */
    private var offsetPixels = 0F

    private var outerGravity = Indicators.OuterGravity.BOTTOM

    private var innerGravity = Indicators.InnerGravity.CENTER

    override fun getIndicatorView(): View {
        return this
    }

    override fun setAmount(amount: Int) {
        this.amount = amount
        post { // 写在这里是为了在调用 changeAmount() 后能修改 frontMargin 值
            judgeStyle(
                horizontal = {
                    when (innerGravity) {
                        Indicators.InnerGravity.FRONT -> {
                            frontMargin = intervalMargin
                        }
                        Indicators.InnerGravity.BACK -> {
                            frontMargin = width - amount * intervalMargin
                        }
                        Indicators.InnerGravity.CENTER -> {
                            frontMargin = width / 2F - (amount - 1) / 2F * intervalMargin
                        }
                    }
                },
                vertical = {
                    when (innerGravity) {
                        Indicators.InnerGravity.FRONT -> {
                            frontMargin = intervalMargin
                        }
                        Indicators.InnerGravity.BACK -> {
                            frontMargin = height - amount * intervalMargin
                        }
                        Indicators.InnerGravity.CENTER -> {
                            frontMargin = height / 2F - (amount - 1) / 2F * intervalMargin
                        }
                    }
                }
            )
            invalidate()
        }
    }

    override fun changeAmount(amount: Int) {
        setAmount(amount)
    }

    override fun setIndicatorsOuterGravity(gravity: Int): FrameLayout.LayoutParams {
        outerGravity = gravity
        return super.setIndicatorsOuterGravity(gravity)
    }

    override fun setIndicatorsInnerGravity(gravity: Int) {
        innerGravity = gravity
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        this.position = position
        positionFloat = position + positionOffset
        val offset =
            if (position >= idlePosition || positionFloat == 0F) positionOffset
            else positionOffset - 1 // 当向左滑动时，positionOffset 是从 0.99999 -> 0 的，所以要减一改为 0 -> 0.99999
        offsetPixels = offset * intervalMargin
        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager2.SCROLL_STATE_IDLE -> {
                idlePosition = position
                offsetPixels = 0F
                invalidate()
            }
            ViewPager2.SCROLL_STATE_DRAGGING -> {
            }
            ViewPager2.SCROLL_STATE_SETTLING -> {
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wrap = wrapWidth.toInt()
        val newWidthMS = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> widthMeasureSpec
            else -> MeasureSpec.makeMeasureSpec(wrap, MeasureSpec.EXACTLY)
        }
        val newHeightMS = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> heightMeasureSpec
            else -> MeasureSpec.makeMeasureSpec(wrap, MeasureSpec.EXACTLY)
        }
        super.onMeasure(newWidthMS, newHeightMS)
    }

    private val mPath = Path()
    private val mMatrix = Matrix()
    private val mBackgroundCirclePaint by lazy {
        val paint = Paint()
        paint.color = backgroundCircleColor
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint
    }
    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
        drawBackgroundCircle(canvas)
        drawMovePath(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        onDrawBackground(canvas, width, height)
    }

    private fun drawBackgroundCircle(canvas: Canvas) {
        mPath.reset()
        onDrawBackgroundCircle(mPath, circleRadius)
        judgeStyle(
            horizontal = {
                mMatrix.setTranslate(frontMargin, wrapWidth / 2)
                mPath.transform(mMatrix)
                canvas.drawPath(mPath, mBackgroundCirclePaint)
                repeat(amount - 1) {
                    mMatrix.setTranslate(intervalMargin, 0F)
                    mPath.transform(mMatrix)
                    canvas.drawPath(mPath, mBackgroundCirclePaint)
                }
            },
            vertical = {
                mMatrix.setTranslate(wrapWidth / 2, frontMargin)
                mPath.transform(mMatrix)
                canvas.drawPath(mPath, mBackgroundCirclePaint)
                repeat(amount - 1) {
                    mMatrix.setTranslate(0F, intervalMargin)
                    mPath.transform(mMatrix)
                    canvas.drawPath(mPath, mBackgroundCirclePaint)
                }
            }
        )
    }

    private val mMovePathPaint by lazy {
        val paint = Paint()
        paint.color = circleColor
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint
    }
    private fun drawMovePath(canvas: Canvas) {
        mPath.reset()
        onDrawMovePath(mPath, circleRadius, offsetPixels, intervalMargin)
        val p = if (position >= idlePosition) position else position + 1
        judgeStyle(
            horizontal = {
                mMatrix.setTranslate(frontMargin + p * intervalMargin, wrapWidth / 2)
                mPath.transform(mMatrix)
                canvas.drawPath(mPath, mMovePathPaint)
            },
            vertical = {
                mMatrix.setTranslate(wrapWidth / 2, frontMargin + p * intervalMargin)
                mMatrix.postRotate(90F, wrapWidth / 2, frontMargin + p * intervalMargin)
                mPath.transform(mMatrix)
                canvas.drawPath(mPath, mMovePathPaint)
            }
        )
    }

    private inline fun judgeStyle(horizontal: () -> Unit, vertical: () -> Unit) {
        when (outerGravity) {
            Indicators.OuterGravity.TOP,
            Indicators.OuterGravity.BOTTOM,
            Indicators.OuterGravity.HORIZONTAL_CENTER,
            -> horizontal.invoke()
            Indicators.OuterGravity.LEFT,
            Indicators.OuterGravity.RIGHT,
            Indicators.OuterGravity.VERTICAL_CENTER,
            -> vertical.invoke()
        }
    }

    /**
     * 用于绘制背景
     */
    open fun onDrawBackground(canvas: Canvas, width: Int, height: Int) {
    }

    /**
     * 用于绘制背景的小圆点
     *
     * **NOTE：** 你只需要绘制一个 path 即可
     */
    open fun onDrawBackgroundCircle(path: Path, radius: Float) {
        path.addCircle(0F, 0F, radius, Path.Direction.CCW)
    }

    /**
     * 用于在移动时绘制图形，请自己实现 path 的绘制，只需绘制一个区间的轨迹
     *
     * **NOTE：**
     *
     * 1、你只需要使用 offsetPixels 的值来绘制从 -intervalMargin 到 +intervalMargin 之间对应的 path 即可
     *
     * 2、参考系是水平的，坐标为 (-intervalMargin, 0) <---> (0, 0) <---> (+intervalMargin, 0)，
     *     在绘图时会对 path 自动进行旋转或移动来展示全部的圆点动画
     *
     * @param offsetPixels 值只会在 -intervalMargin 到 +intervalMargin 之间
     * @param intervalMargin 两个圆点间的距离值
     */
    abstract fun onDrawMovePath(
        path: Path,
        radius: Float,
        offsetPixels: Float,
        intervalMargin: Float,
    )
}