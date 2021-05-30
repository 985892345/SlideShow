package com.ndhzs.slideshow.viewpager2.adapter

import android.util.SparseArray
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.ndhzs.slideshow.myinterface.OnImgRefreshListener
import com.ndhzs.slideshow.utils.Attrs
import com.ndhzs.slideshow.utils.Refresh

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/27
 */
abstract class BaseImgAdapter<T> : RecyclerView.Adapter<BaseImgAdapter.BaseImgViewHolder>() {

    private lateinit var datas: List<T>
    private val array = SparseArray<ConditionWithListener>()
    private var mIsCirculate = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseImgViewHolder {
        val imageView = ShapeableImageView(parent.context)
        val lp = FrameLayout.LayoutParams(Attrs.imgWidth, Attrs.imgHeight)
        lp.gravity = Gravity.CENTER
        lp.leftMargin = Attrs.imgMarginHorizontal
        lp.topMargin = Attrs.imgMarginVertical
        lp.rightMargin = Attrs.imgMarginHorizontal
        lp.bottomMargin = Attrs.imgMarginVertical
        imageView.layoutParams = lp
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setTopLeftCornerSize(Attrs.imgLeftTopRadius)
                .setTopRightCornerSize(Attrs.imgRightTopRadius)
                .setBottomLeftCornerSize(Attrs.imgLeftBottomRadius)
                .setBottomRightCornerSize(Attrs.imgRightBottomRadius)
                .build()
        val frameLayout = FrameLayout(parent.context)
        val lpFl = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.setBackgroundColor(Attrs.imgDefaultColor)
        frameLayout.layoutParams = lpFl
        frameLayout.addView(imageView, lp)
        return BaseImgViewHolder(frameLayout)
    }

    /**
     * **WARNING：** 因为该方法实现了接口刷新，请看 SlideShow#notifyImageViewRefresh，所以禁止重写
     * @see [onBindImageView]
     */
    @Deprecated("禁止重写! ", ReplaceWith("onBindImageView"))
    override fun onBindViewHolder(
        holder: BaseImgViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (array.contains(position)) {
            val conditionWithListener = array[position]
            when (conditionWithListener.condition) {
                Refresh.Condition.COEXIST -> {
                    onBindViewHolder(holder, position)
                    conditionWithListener.l.onRefresh(holder.imageView, holder, position)
                }
                Refresh.Condition.COVERED -> {
                    conditionWithListener.l.onRefresh(holder.imageView, holder, position)
                }
            }
        }
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }else {
            payloads.forEach {
                if (it == BaseRecyclerAdapter.ITEM_REFRESH) {
                    array[position].l.onRefresh(holder.imageView, holder, position)
                    if (array[position].condition == Refresh.Condition.ONLY_ONE) {
                        array.remove(position)
                    }
                }
            }
        }
    }

    /**
     * **WARNING：** 因为该方法有特殊实现，所以禁止重写
     * @see [onBindImageView]
     */
    @Deprecated("禁止重写! ", ReplaceWith("onBindImageView"))
    override fun onBindViewHolder(holder: BaseImgViewHolder, position: Int) {
        onBindImageView(datas[position], holder.imageView, holder, position)
    }

    /**
     * **WARNING：** 请不要重写
     */
    @Deprecated("禁止重写! ")
    override fun getItemCount(): Int {
        return datas.size
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    fun setData(datas: List<T>) {
        this.datas = datas
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    fun openCirculateEnabled() {
        if (!mIsCirculate) {
            if (datas.size > 1) {
                mIsCirculate = true
                val size = datas.size
                val newList = ArrayList<T>()
                newList.add(datas[size - 2])
                newList.add(datas[size - 1])
                newList.addAll(datas)
                newList.add(datas[0])
                newList.add(datas[1])
                datas = newList
            }
        }
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    fun setImgRefreshListener(position: Int,
                           @Refresh.Condition
                           condition: Int,
                           l: OnImgRefreshListener) {
        array.put(position, ConditionWithListener(condition, l))
    }

    fun removeImgRefreshListener(position: Int) {
        return array.remove(position)
    }

    fun clearImgRefreshListener() {
        return array.clear()
    }

    abstract fun onBindImageView(data: T, imageView: ShapeableImageView, holder: BaseImgViewHolder, position: Int)

    class BaseImgViewHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.getChildAt(0) as ShapeableImageView
    }

    class ConditionWithListener(
            val condition: Int,
            val l: OnImgRefreshListener
    )
}