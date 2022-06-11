package com.ndhzs.slideshow2.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow2.indicators.attrs.IndicatorsAttrs
import com.ndhzs.slideshow2.utils.OnPageChangeCallback
import com.ndhzs.slideshow2.utils.lazyUnlock

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/23 16:38
 */
abstract class AbstractIndicatorsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes), OnPageChangeCallback {

    fun getAttrs(): IndicatorsAttrs = mAttrs

    private val mAttrs: IndicatorsAttrs =
        IndicatorsAttrs.newInstance(this, attrs, defStyleAttr, defStyleRes)

    private var mAmount = 0

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

    internal fun setAmount(amount: Int) {
        mAmount = amount
        invalidate()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        this.position = position
        positionFloat = position + positionOffset
        val offset =
            if (position >= idlePosition || positionFloat == 0F) positionOffset
            else positionOffset - 1 // 当向左滑动时，positionOffset 是从 0.99999 -> 0 的，所以要减一改为 0 -> -0.99999
        offsetPixels = offset * mAttrs.intervalMargin
        invalidate()
    }

    override fun onPageSelected(position: Int) {
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
        val width: Int
        val height: Int
        if (mAttrs.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            width = (mAttrs.circleRadius * 2 + (mAmount - 1) * mAttrs.intervalMargin).toInt()
            height = (mAttrs.circleRadius * 2).toInt()
        } else {
            width = (mAttrs.circleRadius * 2).toInt()
            height = (mAttrs.circleRadius * 2 + (mAmount - 1) * mAttrs.intervalMargin).toInt()
        }
        val newWidthMS = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> widthMeasureSpec
            else -> MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        }
        val newHeightMS = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> heightMeasureSpec
            else -> MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(newWidthMS, newHeightMS)
    }

    private val mPath = Path()
    private val mMatrix = Matrix()
    private val mCircleBackgroundPaint by lazyUnlock {
        Paint().apply {
            color = getAttrs().circleBackground
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircleBackground(canvas)
        drawMovePath(canvas)
    }

    private fun drawCircleBackground(canvas: Canvas) {
        mPath.reset()
        onDrawBackgroundCircle(mPath, mAttrs.circleRadius)
        if (mAttrs.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            mMatrix.setTranslate(mAttrs.circleRadius, height / 2F)
            mPath.transform(mMatrix)
            canvas.drawPath(mPath, mCircleBackgroundPaint)
            repeat(mAmount - 1) {
                mMatrix.setTranslate(mAttrs.intervalMargin, 0F)
                mPath.transform(mMatrix)
                canvas.drawPath(mPath, mCircleBackgroundPaint)
            }
        } else {
            mMatrix.setTranslate(width / 2F, mAttrs.circleRadius)
            mPath.transform(mMatrix)
            canvas.drawPath(mPath, mCircleBackgroundPaint)
            repeat(mAmount - 1) {
                mMatrix.setTranslate(0F, mAttrs.intervalMargin)
                mPath.transform(mMatrix)
                canvas.drawPath(mPath, mCircleBackgroundPaint)
            }
        }
    }

    private val mMovePathPaint by lazyUnlock {
        Paint().apply {
            color = getAttrs().circleColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }

    private fun drawMovePath(canvas: Canvas) {
        mPath.reset()
        onDrawMovePath(mPath, mAttrs.circleRadius, offsetPixels, mAttrs.intervalMargin)
        val p = if (position >= idlePosition || positionFloat == 0F) position else position + 1
        if (mAttrs.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            mMatrix.setTranslate(mAttrs.circleRadius + p * mAttrs.intervalMargin, height / 2F)
        } else {
            mMatrix.setTranslate(width / 2F, mAttrs.circleRadius + p * mAttrs.intervalMargin)
            mMatrix.postRotate(90F, width / 2F, mAttrs.circleRadius + p * mAttrs.intervalMargin)
        }
        mPath.transform(mMatrix)
        canvas.drawPath(mPath, mMovePathPaint)
    }

    /**
     * 用于绘制背景的小圆点
     *
     * **NOTE：** 坐标会在内部转换, 只需修改 path, 每个圆的中心坐标为(0, 0), 且每个圆点都会以该 path
     * 来绘制(意思是你只需绘制一个小圆点即可). 有三个 path 只是用来绘制复杂图形使用
     */
    open fun onDrawBackgroundCircle(path: Path, radius: Float) {
        path.addCircle(0F, 0F, radius, Path.Direction.CCW)
    }

    /**
     * 用于在移动时绘制图形，请自己实现 path 的绘制，只需绘制一个区间的轨迹. 有三个 path 只是用来绘制复杂图形使用
     *
     * ```
     * 1、你只需要使用 offsetPixels 的值来绘制从 -intervalMargin 到 +intervalMargin 之间对应的 path 即可
     *
     * 2、参考系是水平的，坐标为 (-intervalMargin, 0) <---> (0, 0) <---> (+intervalMargin, 0)，
     *    在内部绘图时会自动对 path 进行旋转或移动来以一个路径而显示全部路径的圆点动画
     *
     * 3、看不懂? 那你去看我写的一些实现类 MoveIndicators
     * ```
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