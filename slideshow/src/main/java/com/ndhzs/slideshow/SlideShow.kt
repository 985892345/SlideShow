package com.ndhzs.slideshow

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.cardview.widget.CardView
import androidx.core.animation.addListener
import androidx.core.view.iterator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow.attrs.SlideShowAttrs
import com.ndhzs.slideshow.indicators.AbstractIndicatorsView
import com.ndhzs.slideshow.utils.OnPageChangeCallback
import com.ndhzs.slideshow.utils.forEachInline
import com.ndhzs.slideshow.utils.lazyUnlock
import com.ndhzs.slideshow.viewpager.InnerAdapter
import com.ndhzs.slideshow.viewpager.PageChangeCallback
import com.ndhzs.slideshow.viewpager.transformer.MultipleTransformer
import kotlin.math.max

/**
 *
 * ## 注意
 * 因为继承的 CardView，所以默认有层白色的背景，如果需要取消的话，请使用 app:cardBackgroundColor="@android:color/transparent"
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/22 10:47
 */
class SlideShow @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0,
) : CardView(context, attrs, defStyleAttr) {
  
  /**
   * 设置 Adapter
   */
  fun <VH : RecyclerView.ViewHolder> setAdapter(adapter: RecyclerView.Adapter<VH>) {
    if (getOuterAdapter() != null) error("不允许再次设置 Adapter!")
    if (adapter is FragmentStateAdapter && isCyclical()) {
      error("暂不支持循环的 FragmentStateAdapter")
    }
    mViewPager.adapter = InnerAdapter(adapter, mAttrs)
    mIndicators.forEach {
      setIndicators(it)
    }
    if (mAttrs.frameDistance != SlideShowAttrs.FRAME_DISTANCE
      && mAttrs.pageDistance != SlideShowAttrs.PAGE_DISTANCE
    ) {
      setPageInterval()
    }
    setCurrentItem(0, false)
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
  
  fun addPageChangeCallback(call: OnPageChangeCallback): SlideShow {
    mPageChangeCallback.addPageChangeCallBack(call)
    return this
  }
  
  fun removePageChangeCallback(call: OnPageChangeCallback): SlideShow {
    mPageChangeCallback.removePageChangeCallback(call)
    return this
  }
  
  fun addIndicatorsView(view: AbstractIndicatorsView): SlideShow {
    addPageChangeCallback(view)
    mIndicators.add(view)
    setIndicators(view)
    return this
  }
  
  fun removeIndicatorsView(view: AbstractIndicatorsView): SlideShow {
    removePageChangeCallback(view)
    mIndicators.remove(view)
    return this
  }
  
  /**
   * 设置自动滑动
   */
  fun setIsAutoSlide(boolean: Boolean): SlideShow {
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
  fun setIsCyclical(boolean: Boolean): SlideShow {
    mAttrs.isCyclical = boolean
    val outerAdapter = getOuterAdapter()
    if (outerAdapter is FragmentStateAdapter && boolean) {
      error("暂不支持循环的 FragmentStateAdapter")
    }
    if (outerAdapter != null) {
      getInnerAdapter()!!.setIsCyclical(boolean)
    }
    return this
  }
  
  /**
   * 设置自动滑动时间
   *
   * 单位：毫秒
   */
  fun setAutoSlideTime(
    duration: Int = mAttrs.autoSlideDuration,
    intervalDt: Int = mAttrs.autoSlideIntervalDt,
  ): SlideShow {
    mAttrs.autoSlideDuration = duration
    mAttrs.autoSlideIntervalDt = intervalDt
    return this
  }
  
  /**
   * 设置自动滑动动画的插值器
   */
  fun setTimeInterpolator(interpolator: TimeInterpolator): SlideShow {
    mInterpolator = interpolator
    return this
  }
  
  /**
   * 与 ViewPager2#setCurrentItem 相同，默认有动画
   */
  fun setCurrentItem(item: Int, smoothScroll: Boolean = true): SlideShow {
    mStartPosition = item
    if (mViewPager.isFakeDragging) {
      mViewPager.endFakeDrag()
    }
    if (getOuterAdapter() != null) {
      val realItem = PageChangeCallback.getCenterPos(item, getInnerAdapter()!!)
      mViewPager.setCurrentItem(realItem, smoothScroll)
    } else {
      mViewPager.setCurrentItem(item, smoothScroll)
    }
    return this
  }
  
  /**
   * 得到你说看到的位置，但这个位置时取余后的结果，并不是真实的位置
   */
  fun getCurrentItem(): Int {
    if (getOuterAdapter() != null) {
      return PageChangeCallback.getOuterPos(getInnerAdapter()!!, mViewPager.currentItem)
    }
    return mViewPager.currentItem
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
    if (getOuterAdapter() != null && !mViewPager.isFakeDragging) {
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
  fun setTransformer(transformer: ViewPager2.PageTransformer): SlideShow {
    clearTransformer()
    mPageTransformers.addTransformer(transformer)
    return this
  }
  
  /**
   * 该方法可以设置多个 transformer（页面移动动画）
   * @see [setTransformer]
   */
  fun addTransformer(transformer: ViewPager2.PageTransformer): SlideShow {
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
  fun setMargin(
    marginTop: Int = mAttrs.marginTop,
    marginBottom: Int = mAttrs.marginBottom,
    marginLeft: Int = mAttrs.marginLeft,
    marginRight: Int = mAttrs.marginRight
  ) {
    mAttrs.marginTop = marginTop
    mAttrs.marginBottom = marginBottom
    mAttrs.marginLeft = marginLeft
    mAttrs.marginRight = marginRight
    val lp = mViewPager.layoutParams as? LayoutParams
    if (lp != null) {
      lp.topMargin = marginTop
      lp.bottomMargin = marginBottom
      lp.leftMargin = marginLeft
      lp.rightMargin = marginRight
      mViewPager.layoutParams = lp
    }
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
    mViewPager.setBackgroundColor(Color.TRANSPARENT)
    mViewPager.getChildAt(0).apply {
      overScrollMode = OVER_SCROLL_NEVER
      setBackgroundColor(Color.TRANSPARENT)
    }
    cardElevation = 0F
    attachViewToParent(
      mViewPager, 0,
      LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
        topMargin = mAttrs.marginTop
        bottomMargin = mAttrs.marginBottom
        leftMargin = mAttrs.marginLeft
        rightMargin = mAttrs.marginRight
      }
    )
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
    iterator().forEach {
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
      child.removeCallback.forEachInline { it.invoke() }
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
      if (!mViewPager.isFakeDragging) {
        fakeDrag(1, duration, mInterpolator)
      }
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
   * ```
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
   * ```
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
      val observe = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
          view.setAmount(outerAdapter.itemCount)
        }
        
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
          view.setAmount(view.getAmount() + itemCount)
        }
        
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
          view.setAmount(view.getAmount() - itemCount)
        }
      }
      outerAdapter.registerAdapterDataObserver(observe)
      view.addRemoveCallback {
        outerAdapter.unregisterAdapterDataObserver(observe)
      }
    }
  }
}