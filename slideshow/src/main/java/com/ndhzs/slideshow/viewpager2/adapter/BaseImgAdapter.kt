package com.ndhzs.slideshow.viewpager2.adapter

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.ndhzs.slideshow.SlideShow
import com.ndhzs.slideshow.utils.Attrs

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/27
 */
abstract class BaseImgAdapter<T> : RecyclerView.Adapter<BaseImgAdapter.BaseImgViewHolder>() {

    private lateinit var datas: List<T>

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
        frameLayout.layoutParams = lpFl
        frameLayout.addView(imageView, lp)
        return BaseImgViewHolder(frameLayout)
    }

    override fun onBindViewHolder(
        holder: BaseImgViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }else {
            payloads.forEach {
                if (it is SlideShow.OnRefreshListener) {
                    it.onRefresh(holder.imageView, holder, position)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: BaseImgViewHolder, position: Int) {
        holder.imageView.post {
            onBindImageView(datas[position], holder.imageView, holder, position)
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    fun setData(datas: List<T>) {
        this.datas = datas
    }

    abstract fun onBindImageView(data: T, imageView: ShapeableImageView, holder: BaseImgViewHolder, position: Int)

    class BaseImgViewHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.getChildAt(0) as ShapeableImageView
    }
}