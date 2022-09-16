package com.ndhzs.slideshow.adapter

import android.view.ViewGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.ndhzs.slideshow.SlideShow

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/23 19:47
 */
class ImageViewAdapter<T> private constructor(
  builder: Builder<T>
) : ViewAdapter<T, ShapeableImageView>(builder) {
  
  class Builder<T>(
    data: List<T>,
    radius: Float = 0F
  ) : ViewAdapter.Builder<T, ShapeableImageView>(
    data,
    {
      ShapeableImageView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        if (radius > 0) {
          shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setTopLeftCornerSize(radius)
            .setTopRightCornerSize(radius)
            .setBottomLeftCornerSize(radius)
            .setBottomRightCornerSize(radius)
            .build()
        }
      }
    }
  ) {
  
    override fun onCreate(call: ViewAdapter<T, ShapeableImageView>.Wrapper.() -> Unit): Builder<T> {
      super.onCreate(call)
      return this
    }
  
    override fun onBind(call: ViewAdapter<T, ShapeableImageView>.Wrapper.() -> Unit): Builder<T> {
      super.onBind(call)
      return this
    }
    
    override fun build(): ImageViewAdapter<T> {
      return ImageViewAdapter(this)
    }
  }
}

fun <T> SlideShow.setImgAdapter(imgAdapter: ImageViewAdapter.Builder<T>) {
  setAdapter(imgAdapter.build())
}