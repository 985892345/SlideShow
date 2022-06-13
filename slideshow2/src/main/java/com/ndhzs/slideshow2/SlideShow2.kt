package com.ndhzs.slideshow2

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.cardview.widget.CardView
import androidx.core.animation.addListener
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow2.attrs.SlideShowAttrs
import com.ndhzs.slideshow2.indicators.AbstractIndicatorsView
import com.ndhzs.slideshow2.utils.OnPageChangeCallback
import com.ndhzs.slideshow2.utils.lazyUnlock
import com.ndhzs.slideshow2.viewpager.InnerAdapter
import com.ndhzs.slideshow2.viewpager.PageChangeCallback
import com.ndhzs.slideshow2.viewpager.transformer.MultipleTransformer
import kotlin.math.max

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/22 10:47
 */
class SlideShow2 @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0,
) : CardView(context, attrs, defStyleAttr) {
  
  /**
   * 设置一般的 Adapter
   */
  fun <VH : RecyclerView.ViewHolder> setAdapter(adapter: RecyclerView.Adapter<VH>) {
    mViewPager.adapter = InnerAdapter(adapter, mAttrs)
    mIndicators.forEach {
      setIndicators(it)
    }
  }
  
  fun getOuterAdapter(): RecyclerView.Adapter<*>? {
    return getInnerAdapter()?.outAdapter
  }
  
  fun getInnerAdapter(): InnerAdapter<*>? {
    return mViewPager.adapter as InnerAdapter<*>?
  }
  
  fun transformPosition(layoutPosition: Int): Int {
    val adapter = mViewPager.adapter
    if (adapter is InnerAdapter) {
      return PageChangeCallback.getOuterPos(adapter, layoutPosition)
    }
    return -1
  }
  
  fun addPageChangeCallback(call: OnPageChangeCallback): SlideShow2 {
    mPageChangeCallback.addPageChangeCallBack(call)
    return this
  }
  
  fun removePageChangeCallback(call: OnPageChangeCallback): SlideShow2 {
    mPageChangeCallback.removePageChangeCallback(call)
    return this
  }
  
  fun addIndicatorsView(view: AbstractIndicatorsView): SlideShow2 {
    addPageChangeCallback(view)
    mIndicators.add(view)
    setIndicators(view)
    return this
  }
  
  fun removeIndicatorsView(view: AbstractIndicatorsView): SlideShow2 {
    removePageChangeCallback(view)
    mIndicators.remove(view)
    return this
  }
  
  fun setIsAutoSlide(boolean: Boolean): SlideShow2 {
    if (isAutoSlide() != boolean) {
      if (boolean) {
        mAttrs.isAutoSlide = true
        startAutoSlide()
      } else {
        mAttrs.isAutoSlide = false
        stopAutoSlide()
      }
    }
    return this
  }
  
  /**
   * 开启循环轮播图
   */
  fun setIsCyclical(boolean: Boolean): SlideShow2 {
    if (mViewPager.adapter == null) {
      mAttrs.isCyclical = boolean
    } else {
      getInnerAdapter()!!.setIsCyclical(boolean)
    }
    return this
  }
  
  fun setAutoSlideTime(
    duration: Int = mAttrs.autoSlideDuration,
    intervalDt: Int = mAttrs.autoSlideIntervalDt,
  ): SlideShow2 {
    mAttrs.autoSlideDuration = duration
    mAttrs.autoSlideIntervalDt = intervalDt
    return this
  }
  
  /**
   * 设置自动滑动动画的插值器
   */
  fun setTimeInterpolator(interpolator: TimeInterpolator): SlideShow2 {
    mInterpolator = interpolator
    return this
  }
  
  /**
   * 与 ViewPager2#setCurrentItem 相同，默认有动画
   */
  fun setCurrentItem(item: Int, smoothScroll: Boolean = true): SlideShow2 {
    mStartPosition = item
    if (mViewPager.isFakeDragging) {
      mViewPager.endFakeDrag()
    }
    if (mViewPager.adapter != null) {
      val realItem = PageChangeCallback.getCenterPos(item, getInnerAdapter()!!)
      mViewPager.setCurrentItem(realItem, smoothScroll)
    } else {
      mViewPager.setCurrentItem(item, smoothScroll)
    }
    return this
  }
  
  /**
   * 进行可控制时间的滑动
   */
  fun smoothSlide(
    startPosition: Int,
    endPosition: Int,
    duration: Int,
    interpolator: TimeInterpolator = LinearInterpolator(),
  ) {
    if (mViewPager.adapter != null && !mViewPager.isFakeDragging) {
      fakeDrag(endPosition - startPosition, duration, interpolator)
    }
  }
  
  /**
   * 该方法只能设置一个 transformer（页面移动动画）
   *
   * 已内置了几个 transformer，请点击 [SlideShow] 查看注释
   *
   * @see [addTransformer]
   */
  fun setTransformer(transformer: ViewPager2.PageTransformer): SlideShow2 {
    clearTransformer()
    mPageTransformers.addTransformer(transformer)
    return this
  }
  
  /**
   * 该方法可以设置多个 transformer（页面移动动画）
   * @see [setTransformer]
   */
  fun addTransformer(transformer: ViewPager2.PageTransformer): SlideShow2 {
    mPageTransformers.addTransformer(transformer)
    return this
  }
  
  /**
   * 该方法用于删除 transformer（页面移动动画）
   * @see [clearTransformer]
   */
  fun removeTransformer(transformer: ViewPager2.PageTransformer) {
    mPageTransformers.removeTransformer(transformer)
  }
  
  /**
   * 该方法用于删除全部 transformer（页面移动动画）
   * @see [removeTransformer]
   */
  fun clearTransformer() {
    mPageTransformers.mTransformers.clear()
  }
  
  /**
   * 设置 Vp2 的边距值
   */
  fun setMargin(marginTop: Int, marginBottom: Int) {
    mAttrs.marginTop = marginTop
    mAttrs.marginBottom = marginBottom
    val lp = mViewPager.layoutParams as LayoutParams
    lp.topMargin = marginTop
    lp.bottomMargin = marginBottom
    mViewPager.layoutParams = lp
  }
  
  fun isAutoSlide(): Boolean = mAttrs.isAutoSlide
  fun isCyclical(): Boolean = mAttrs.isCyclical
  fun getAutoSlideDuration(): Int = mAttrs.autoSlideDuration
  fun getAutoSlideIntervalDt(): Int = mAttrs.autoSlideIntervalDt
  
  @ViewPager2.Orientation
  fun getOrientation(): Int = mAttrs.orientation
  fun isUserInputEnabled(): Boolean = mViewPager.isUserInputEnabled
  
  private val mAttrs: SlideShowAttrs
  private val mViewPager: ViewPager2
  private var mStartPosition = 0
  
  init {
    mAttrs = SlideShowAttrs.newInstance(this, attrs, defStyleAttr, defStyleRes)
    mViewPager = ViewPager2(context, attrs, defStyleAttr, defStyleRes)
    cardElevation = 0F
    attachViewToParent(
      mViewPager, 0,
      LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
        topMargin = mAttrs.marginTop
        bottomMargin = mAttrs.marginBottom
      }
    )
    mViewPager.postOnAnimation {
      setPageInterval()
      if (mStartPosition == 0) {
        if (isCyclical()) {
          setCurrentItem(0, false)
        } else {
          /*
          * 下面调用两次是故意这样的，在设置 currentItem 时有 bug，
          * 如果你设置了 show_frameDistance 后首次打开页面时会卡在左边，
          * 即使调用 setCurrentItem(0, false) 也是无效的，
          * 最后得出结论：初始时不带动画的设置 currentItem，它都会卡在左边
          * */
          setCurrentItem(1, true)
          setCurrentItem(0, false)
        }
      }
    }
    
    mViewPager.postOnAnimation { setPageInterval() }
  }
  
  private val mIndicators = ArrayList<AbstractIndicatorsView>(2)
  private val mPageChangeCallback by lazyUnlock { PageChangeCallback(mViewPager) }
  private val mPageTransformers by lazyUnlock {
    MultipleTransformer().apply {
      mViewPager.setPageTransformer(this)
    }
  }
  
  override fun onFinishInflate() {
    super.onFinishInflate()
    children.forEach {
      if (it is AbstractIndicatorsView) {
        addIndicatorsView(it)
      }
    }
    if (mAttrs.indicatorsViewId != View.NO_ID) {
      var parent = parent
      while (parent is ViewGroup) {
        val indicatorsView = parent.findViewById<AbstractIndicatorsView>(mAttrs.indicatorsViewId)
        if (indicatorsView != null) {
          if (!mIndicators.contains(indicatorsView)) {
            addIndicatorsView(indicatorsView)
          }
          break
        }
        parent = parent.parent
      }
    }
  }
  
  override fun onViewRemoved(child: View) {
    if (child is AbstractIndicatorsView) {
      removeIndicatorsView(child)
    }
  }
  
  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    if (isUserInputEnabled()) {
      if (isAutoSlide()) {
        when (ev.action) {
          MotionEvent.ACTION_DOWN -> {
            stopAutoSlide()
          }
          MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
            startAutoSlide()
          }
        }
      }
    }
    return super.dispatchTouchEvent(ev)
  }
  
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (mAttrs.isAutoSlide) {
      startAutoSlide()
    }
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    stopAutoSlide()
  }
  
  private var mIsInAutoSlide = false
  private var mInterpolator: TimeInterpolator = LinearInterpolator()
  private val mAutoSlideRunnable = object : Runnable {
    override fun run() {
      val duration = getAutoSlideDuration()
      fakeDrag(1, duration, mInterpolator)
      val next = duration + getAutoSlideIntervalDt()
      postDelayed(this, next.toLong())
    }
  }
  
  private fun startAutoSlide() {
    if (!mIsInAutoSlide) {
      mIsInAutoSlide = true
      postDelayed(mAutoSlideRunnable, getAutoSlideIntervalDt().toLong())
    }
  }
  
  private fun stopAutoSlide() {
    if (mIsInAutoSlide) {
      mIsInAutoSlide = false
      removeCallbacks(mAutoSlideRunnable)
    }
  }
  
  private var mPreFakeDrag = 0F
  private var mFakeDragAnimator: ValueAnimator? = null
  private fun fakeDrag(
    diffPosition: Int,
    duration: Int,
    interpolator: TimeInterpolator = LinearInterpolator(),
  ) {
    val innerAdapter = getInnerAdapter() ?: return
    mPreFakeDrag = 0F
    val childView = mViewPager.getChildAt(0)
    val pixelDistance = if (getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
      mViewPager.width - childView.paddingLeft - childView.paddingRight
    } else {
      mViewPager.height - childView.paddingTop - childView.paddingBottom
    }
    var end = -diffPosition.toFloat()
    if (!mAttrs.isCyclical) {
      if (mViewPager.currentItem == innerAdapter.itemCount - 1) {
        end = mViewPager.currentItem.toFloat()
      }
    }
    mFakeDragAnimator = ValueAnimator.ofFloat(0F, end).apply {
      addUpdateListener {
        val nowFakeDrag = it.animatedValue as Float
        val differentOffsetPixel = (nowFakeDrag - mPreFakeDrag) * pixelDistance
        mViewPager.fakeDragBy(differentOffsetPixel)
        mPreFakeDrag = nowFakeDrag
      }
      addListener(
        onStart = {
          mViewPager.beginFakeDrag()
          mViewPager.isUserInputEnabled = false
        },
        onEnd = {
          mViewPager.endFakeDrag()
          mViewPager.isUserInputEnabled = true
          mFakeDragAnimator = null
        },
        onCancel = {
          mViewPager.endFakeDrag()
          mViewPager.isUserInputEnabled = true
          mFakeDragAnimator = null
        }
      )
      this.interpolator = interpolator
      this.duration = duration.toLong()
      start()
    }
  }
  
  /**
   * 设置 ViewPager2 内部页面的边距，Orientation 为水平时设置左右的间隔，垂直时设置上下的间隔
   *
   * ---------------------------------------------------------------------------------
   * |     RecyclerView
   * |                     ----------------------------------------------------------
   * | <---- padding ----> |    RV 里面 item 的本来摆放范围
   * |                     |                            --------------------------
   * |                     | <--- pageDistance / 2 ---> |        实际 item 在这区域
   * |                     |                            |
   * |                     |                            |
   * |                                                  |
   * | <------------- frameDistance ------------------> |
   * |                     |                            |
   * |                     |                            -------------------------
   * |                     ---------------------------------------------------------
   * --------------------------------------------------------------------------------
   */
  private fun setPageInterval() {
    if (mViewPager.adapter is InnerAdapter<*>) {
      val childView = mViewPager.getChildAt(0) as RecyclerView
      val frameDistance = mAttrs.frameDistance
      val pageDistance = mAttrs.pageDistance
      val padding = max(frameDistance - pageDistance / 2, 0)
      when (getOrientation()) {
        ViewPager2.ORIENTATION_HORIZONTAL -> {
          childView.setPadding(padding, 0, padding, 0)
        }
        else -> {
          childView.setPadding(0, padding, 0, padding)
        }
      }
      childView.clipToPadding = false
    }
  }
  
  private fun setIndicators(view: AbstractIndicatorsView) {
    val outerAdapter = getOuterAdapter()
    if (outerAdapter != null) {
      view.setAmount(outerAdapter.itemCount)
    }
  }
}