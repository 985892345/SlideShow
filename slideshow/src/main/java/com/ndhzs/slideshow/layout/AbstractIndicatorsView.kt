package com.ndhzs.slideshow.layout

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
import com.ndhzs.slideshow.utils.Indicators
import com.ndhzs.slideshow.utils.SlideShowUtils

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/28
 */
abstract class AbstractIndicatorsView(context: Context) : View(context), IIndicator {

    /**
     * 设置为 wrap_content 时的宽度值
     */
    protected var wrapWidth = SlideShowUtils.dpToPx(50)

    /**
     * 圆点的半径值
     */
    protected var circleRadius = SlideShowUtils.dpToPx(5)

    /**
     * 两个圆点间的距离值
     */
    protected var intervalMargin = SlideShowUtils.dpToPx(30)

    private var amount = 0
    private var frontMargin = 0F

    private var position = 0
    private var idlePosition = 0
    private var positionFloat = 0F
    private var offsetPixels = 0F

    @Indicators.OuterGravity
    private var outerGravity = Indicators.OuterGravity.BOTTOM

    @Indicators.InnerGravity
    private var innerGravity = Indicators.InnerGravity.CENTER

    override fun getIndicatorView(): View {
        return this
    }

    override fun setAmount(amount: Int) {
        this.amount = amount
        post {
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
        offsetPixels = (
                if (position >= idlePosition) positionFloat - idlePosition
                else positionFloat - idlePosition
                ) * intervalMargin
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
        paint.color = getBackgroundCircleColor()
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
        paint.color = getMovePathColor()
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint
    }
    private fun drawMovePath(canvas: Canvas) {
        mPath.reset()
        onDrawMovePath(mPath, circleRadius, offsetPixels, intervalMargin)
        judgeStyle(
            horizontal = {
                mMatrix.setTranslate(frontMargin + idlePosition * intervalMargin, wrapWidth / 2)
                mPath.transform(mMatrix)
                canvas.drawPath(mPath, mMovePathPaint)
            },
            vertical = {
                mMatrix.setTranslate(wrapWidth / 2, frontMargin + idlePosition * intervalMargin)
                mMatrix.postRotate(-90F)
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
     * 用于在移动时绘制图形，请自己实现 path 的绘制
     *
     * **NOTE：**
     *
     * 1、你只需要使用 offsetPixels 来绘制从 -intervalMargin 到 +intervalMargin 之间对应的 path 即可
     *
     * 2、参考系是水平的，坐标为 (-intervalMargin, 0) <---> (0, 0) <---> (+intervalMargin, 0)
     *
     * @param offsetPixels 值只会在 -intervalMargin 到 +intervalMargin 之间
     * @param intervalMargin 两个圆点间的距离值
     */
    abstract fun onDrawMovePath(
        path: Path,
        radius: Float,
        offsetPixels: Float,
        intervalMargin: Float
    )

    /**
     * 用于得到背景小圆点的颜色
     */
    abstract fun getBackgroundCircleColor(): Int

    /**
     * 用于得到移动的图形的颜色
     */
    abstract fun getMovePathColor(): Int
}