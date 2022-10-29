package com.ndhzs.slideshow.viewpager

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow.SlideShow
import com.ndhzs.slideshow.attrs.SlideShowAttrs

/**
 * ## 使用者自定义 RV#Adapter 的适配器
 *
 * ### 注意事项：
 * - holder#get*Position() 方法在开启循环后得到的位置会不正确，请使用 [SlideShow.transformPosition] 方法
 * - 设置 [setHasStableIds] 为 true 后无法取消
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/22 20:06
 */
class InnerAdapter<VH : RecyclerView.ViewHolder>(
  val outAdapter: RecyclerView.Adapter<VH>,
  val attrs: SlideShowAttrs,
) : RecyclerView.Adapter<VH>() {
  
  private val mIsCyclical
    get() = attrs.isCyclical
  
  @SuppressLint("NotifyDataSetChanged")
  internal fun setIsCyclical(boolean: Boolean) {
    if (boolean != mIsCyclical) {
      outAdapter.notifyDataSetChanged()
    }
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val holder = outAdapter.onCreateViewHolder(parent, viewType)
    val view = holder.itemView
    var lp = view.layoutParams
    lp = if (lp is ViewGroup.LayoutParams) {
      ViewGroup.MarginLayoutParams(lp)
    } else {
      ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
    if (attrs.pageDistance != SlideShowAttrs.PAGE_DISTANCE) {
      val margin = attrs.pageDistance / 2
      if (attrs.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
        lp.leftMargin = margin
        lp.rightMargin = margin
      } else {
        lp.topMargin = margin
        lp.bottomMargin = margin
      }
    }
    view.layoutParams = lp
    return holder
  }
  
  override fun onBindViewHolder(holder: VH, position: Int) {
    // 这里面不用实现，因为该回调是 onBindViewHolder(VH, Int, MutableList<Any>) 调用的
  }
  
  override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
    outAdapter.onBindViewHolder(
      holder,
      PageChangeCallback.getOuterPos(
        this,
        position
      ),
      payloads
    )
  }
  
  override fun getItemCount(): Int {
    if (!mIsCyclical) {
      return outAdapter.itemCount
    }
    val outerItemCount = outAdapter.itemCount
    return if (outerItemCount <= 10) 50 else outerItemCount * 5
  }
  
  override fun getItemViewType(position: Int): Int {
    return outAdapter.getItemViewType(
      PageChangeCallback.getOuterPos(
        this,
        position
      )
    )
  }
  
  override fun getItemId(position: Int): Long {
    return outAdapter.getItemId(PageChangeCallback.getOuterPos(this, position))
  }
  
  override fun onViewRecycled(holder: VH) {
    outAdapter.onViewRecycled(holder)
  }
  
  override fun onFailedToRecycleView(holder: VH): Boolean {
    return outAdapter.onFailedToRecycleView(holder)
  }
  
  override fun onViewAttachedToWindow(holder: VH) {
    outAdapter.onViewAttachedToWindow(holder)
  }
  
  override fun onViewDetachedFromWindow(holder: VH) {
    outAdapter.onViewDetachedFromWindow(holder)
  }
  
  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    outAdapter.onAttachedToRecyclerView(recyclerView)
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    outAdapter.onDetachedFromRecyclerView(recyclerView)
  }
  
  init {
    
    /*
    * 在设置监听前检查是否设置了 StableIds
    * 不然会因为有监听者的存在而报错
    * */
    if (hasStableIds()) {
      setHasStableIds(true)
    }
    
    outAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      
      @SuppressLint("NotifyDataSetChanged")
      override fun onChanged() {
        notifyDataSetChanged()
      }
      
      override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        var position = positionStart
        val outerItemCount = outAdapter.itemCount
        while (position < this@InnerAdapter.itemCount) {
          notifyItemRangeChanged(positionStart, itemCount)
          position += outerItemCount
        }
      }
      
      override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        var position = positionStart
        val outerItemCount = outAdapter.itemCount
        while (position < this@InnerAdapter.itemCount) {
          notifyItemRangeChanged(positionStart, itemCount, payload)
          position += outerItemCount
        }
      }
      
      override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        if (mIsCyclical) error("开启了循环后不允许 insert 新值")
        notifyItemRangeInserted(positionStart, itemCount)
      }
      
      override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        if (mIsCyclical) error("开启了循环后不允许 remove 值")
        notifyItemRangeRemoved(positionStart, itemCount)
      }
      
      override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        if (mIsCyclical) error("开启了循环后不允许 move 值")
        // 官方传的 itemCount 是一个为 1 的常量
        notifyItemMoved(fromPosition, toPosition)
      }
    })
  }
}