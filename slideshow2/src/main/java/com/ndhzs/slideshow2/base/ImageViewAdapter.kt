package com.ndhzs.slideshow2.base

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/23 19:47
 */
class ImageViewAdapter<T>(
    val data: List<T>,
    val radius: Float = 0F,
    val onCreate: ((img: ShapeableImageView) -> Unit)? = null,
    val onBind: (img: ShapeableImageView, pos: Int) -> Unit
) : RecyclerView.Adapter<ImageViewAdapter.ImageHolder>() {
    class ImageHolder(val view: ShapeableImageView) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = ShapeableImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            if (radius > 0) {
                shapeAppearanceModel = ShapeAppearanceModel.builder()
                    .setTopLeftCornerSize(radius)
                    .setTopRightCornerSize(radius)
                    .setBottomLeftCornerSize(radius)
                    .setBottomRightCornerSize(radius)
                    .build()
            }
        }
        onCreate?.invoke(view)
        return ImageHolder(view)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        onBind.invoke(holder.view, position)
    }

    override fun getItemCount(): Int = data.size
}