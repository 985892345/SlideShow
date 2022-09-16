package com.ndhzs.slideshow.viewpager

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow.utils.OnPageChangeCallback
import com.ndhzs.slideshow.utils.forEachReversed

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/22 20:48
 */
class PageChangeCallback(
  private val viewPager2: ViewPager2,
) : ViewPager2.OnPageChangeCallback() {
  
  fun addPageChangeCallBack(callBack: OnPageChangeCallback) {
    mCallbacks.add(callBack)
  }
  
  fun removePageChangeCallback(callBack: OnPageChangeCallback) {
    mCallbacks.remove(callBack)
  }
  
  init {
    viewPager2.registerOnPageChangeCallback(this)
  }
  
  private val innerAdapter: InnerAdapter<*>
    get() = viewPager2.adapter as InnerAdapter<*>
  
  private val outerAdapter: RecyclerView.Adapter<*>
    get() = innerAdapter.outAdapter
  
  private val isCyclical: Boolean
    get() = innerAdapter.itemCount > outerAdapter.itemCount
  
  private var mCallbacks = ArrayList<OnPageChangeCallback>()
  private var mPositionFloat = 0F
  private var mIdlePosition = -1
  
  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    if (mIdlePosition == -1) mIdlePosition = position
    mPositionFloat = position + positionOffset
    pageScrolledCallback(position, positionOffset, positionOffsetPixels)
  }
  
  private fun pageScrolledCallback(
    position: Int,
    positionOffset: Float,
    positionOffsetPixels: Int,
  ) {
    if (isCyclical) {
      if (mPositionFloat % outerAdapter.itemCount > outerAdapter.itemCount - 1) { // 当划出边界时
        val pf =
          (1 - positionOffset) * (outerAdapter.itemCount - 1) // 这里可以表示从右边界到左边界(或相反)经过的值
        mCallbacks.forEachReversed {
          it.onPageScrolled(pf.toInt(), pf - pf.toInt(), positionOffsetPixels)
        }
        return
      }
    }
    val outerPosition = getOuterPos(innerAdapter, position)
    mCallbacks.forEachReversed {
      it.onPageScrolled(outerPosition, positionOffset, positionOffsetPixels)
    }
  }
  
  override fun onPageSelected(position: Int) {
    pageSelected(position)
  }
  
  private fun pageSelected(position: Int) {
    val outerPosition = getOuterPos(innerAdapter, position)
    mCallbacks.forEachReversed {
      it.onPageSelected(outerPosition)
    }
  }
  
  override fun onPageScrollStateChanged(state: Int) {
    if (state == ViewPager2.SCROLL_STATE_IDLE) {
      mIdlePosition = mPositionFloat.toInt()
      /*
      * 开启循环后，vp 一来就处于中间位置，且左右两边划到边界时需要划很久，只要一旦停下来就快速移到中间位置
      * */
      if (isCyclical) {
        viewPager2.setCurrentItem(
          getCenterPos(
            viewPager2.currentItem, innerAdapter
          ), false
        )
      }
    }
    mCallbacks.forEachReversed {
      it.onPageScrollStateChanged(state)
    }
  }
  
  companion object {
    
    /**
     * 由 position 取余而得到的显示的数据位置
     */
    fun getOuterPos(isCyclical: Boolean, innerPosition: Int, outerItemCount: Int): Int {
      return if (isCyclical) {
        innerPosition % outerItemCount
      } else innerPosition
    }
    
    fun getOuterPos(innerAdapter: InnerAdapter<*>, innerPosition: Int): Int {
      val outerAdapter = innerAdapter.outAdapter
      return getOuterPos(
        innerAdapter.itemCount > outerAdapter.itemCount,
        innerPosition,
        outerAdapter.itemCount
      )
    }
    
    /**
     * 得到该回到的居中的位置
     */
    fun getCenterPos(currentItem: Int, innerAdapter: InnerAdapter<*>): Int {
      val innerItemCount = innerAdapter.itemCount
      val outerItemCount = innerAdapter.outAdapter.itemCount
      val dataPosition = currentItem % outerItemCount
      val centerPosition = innerItemCount / 2
      return centerPosition + (dataPosition - centerPosition % outerItemCount)
    }
  }
}