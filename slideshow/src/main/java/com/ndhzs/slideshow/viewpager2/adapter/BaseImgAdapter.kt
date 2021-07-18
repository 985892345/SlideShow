package com.ndhzs.slideshow.viewpager2.adapter

import android.util.SparseArray
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.ndhzs.slideshow.myinterface.OnImgRefreshListener
import com.ndhzs.slideshow.utils.SlideShowAttrs
import com.ndhzs.slideshow.indicators.utils.Refresh
import com.ndhzs.slideshow.viewpager2.pagecallback.BasePageChangeCallBack

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/27
 */
abstract class BaseImgAdapter<T> : BaseViewAdapter<ShapeableImageView>(ShapeableImageView::class.java) {

    private val datas = ArrayList<T>()
    private val array = SparseArray<ConditionWithListener>()
    private var mIsCirculate = false
    private var size = 0
    private lateinit var mViewPager2: ViewPager2

    override fun onConfigureView(view: ShapeableImageView, viewType: Int) {
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        view.shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setTopLeftCornerSize(attrs.imgLeftTopRadius)
            .setTopRightCornerSize(attrs.imgRightTopRadius)
            .setBottomLeftCornerSize(attrs.imgLeftBottomRadius)
            .setBottomRightCornerSize(attrs.imgRightBottomRadius)
            .build()
        view.setBackgroundColor(attrs.imgDefaultColor)
    }

    /**
     * **WARNING：** 因为该方法有特殊实现，所以禁止重写
     */
    @Deprecated(
        "禁止重写! ",
        ReplaceWith("onBindImageView(data, imageView, holder, position)"))
    override fun onBindViewHolder(
        view: ShapeableImageView,
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        val falsePosition = BasePageChangeCallBack.getFalsePosition(mIsCirculate, position, datas.size)
        if (array.indexOfKey(falsePosition) >= 0) {
            val conditionWithListener = array[falsePosition]
            when (conditionWithListener.condition) {
                Refresh.Condition.COEXIST -> {
                    onBindImageView(datas[falsePosition], view, holder, falsePosition)
                    conditionWithListener.l.onRefresh(view, holder, falsePosition)
                }
                Refresh.Condition.COVERED -> {
                    conditionWithListener.l.onRefresh(view, holder, falsePosition)
                }
                Refresh.Condition.ONLY_ONE -> {
                    payloads.forEach {
                        if (it == BaseRecyclerAdapter.ITEM_REFRESH) {
                            array[falsePosition].l.onRefresh(view, holder, falsePosition)
                            if (array[falsePosition].condition == Refresh.Condition.ONLY_ONE) {
                                array.remove(falsePosition)
                            }
                        }
                    }
                }
            }
        }else if (payloads.isEmpty()) {
            onBindImageView(datas[falsePosition], view, holder, falsePosition)
        }
    }

    /**
     * **WARNING：** 可以调用，但禁止重写!
     */
    override fun getItemCount(): Int {
        return size
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    @Deprecated("禁止自己调用! ")
    internal fun initialize(datas: List<T>, viewPager2: ViewPager2, attrs: SlideShowAttrs) {
        this.datas.addAll(datas)
        size = datas.size
        initializeAttrs(attrs)
        mViewPager2 = viewPager2
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    @Deprecated("禁止自己调用! ")
    internal fun refreshData(datas: List<T>) {
        this.datas.clear()
        this.datas.addAll(datas)
        size = datas.size
        if (mIsCirculate) {
            mIsCirculate = false
            openCirculateEnabled()
        }
        notifyDataSetChanged()
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    @Deprecated("禁止自己调用! ")
    internal fun openCirculateEnabled() {
        if (!mIsCirculate) {
            if (datas.size > 1) {
                mIsCirculate = true
                size = if (datas.size <= 13) 39 else datas.size * 3
            }
        }
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    internal fun setImgRefreshListener(falsePosition: Int,
                              @Refresh.Condition
                              condition: Int,
                              l: OnImgRefreshListener) {
        array.put(falsePosition, ConditionWithListener(condition, l))
        val currentItem = mViewPager2.currentItem
        val dataSize = datas.size
        val p = currentItem - (currentItem % dataSize - falsePosition)
        repeat(5) {
            notifyItemChanged(p - 2 * dataSize + it * dataSize, BaseRecyclerAdapter.ITEM_REFRESH)
        }
    }

    fun removeImgRefreshListener(position: Int) {
        return array.remove(position)
    }

    fun clearImgRefreshListener() {
        return array.clear()
    }

    abstract fun onBindImageView(data: T, imageView: ShapeableImageView, holder: BaseViewHolder, position: Int)

    private class ConditionWithListener(
            val condition: Int,
            val l: OnImgRefreshListener
    )
}