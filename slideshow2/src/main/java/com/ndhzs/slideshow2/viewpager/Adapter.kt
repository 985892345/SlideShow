package com.ndhzs.slideshow2.viewpager

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow2.SlideShow2
import com.ndhzs.slideshow2.attrs.SlideShowAttrs

/**
 * ## 使用者自定义 RV#Adapter 的适配器
 *
 * ### 注意事项：
 * - holder#get*Position() 方法在开启循环后得到的位置会不正确，请使用 [SlideShow2.transformPosition] 方法
 * - 设置 [setHasStableIds] 为 true 后无法取消
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/22 20:06
 */
class Adapter<VH: RecyclerView.ViewHolder>(
    val outAdapter: RecyclerView.Adapter<VH>,
    val attrs: SlideShowAttrs
) : RecyclerView.Adapter<VH>() {

    private var mIsCyclical = attrs.isCyclical

    @SuppressLint("NotifyDataSetChanged")
    internal fun setIsCyclical(boolean: Boolean) {
        if (boolean != mIsCyclical) {
            mIsCyclical = boolean
            if (boolean) {
                outAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val holder = outAdapter.onCreateViewHolder(parent, viewType)
        val view = holder.itemView
        var lp = view.layoutParams
        if (lp is ViewGroup.LayoutParams) {
            lp = ViewGroup.MarginLayoutParams(lp)
        } else if (lp == null) {
            lp = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        if (lp is ViewGroup.MarginLayoutParams) {
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
        return if (outerItemCount <= 10) 30 else outerItemCount * 3
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
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                notifyItemRangeChanged(positionStart, itemCount, payload)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                // 官方传的 itemCount 是一个为 1 的常量
                notifyItemMoved(fromPosition, toPosition)
            }
        })
    }
}