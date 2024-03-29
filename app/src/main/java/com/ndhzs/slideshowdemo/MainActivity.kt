package com.ndhzs.slideshowdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import com.ndhzs.slideshow.SlideShow
import com.ndhzs.slideshow.adapter.ImageViewAdapter
import com.ndhzs.slideshow.adapter.setImgAdapter
import com.ndhzs.slideshow.viewpager.transformer.ScaleInTransformer

class MainActivity : AppCompatActivity() {
  
  private lateinit var mSlideShow: SlideShow
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    mSlideShow = findViewById(R.id.slideShow)
    
    val colorList = listOf(
      0xFFFF9800.toInt(),
      0xFF616161.toInt(),
      0xFFC8C8C8.toInt(),
      0xFF2196F3.toInt()
    )
    
    mSlideShow.setTransformer(ScaleInTransformer()) // 设置移动动画
      .setIsAutoSlide(true)
      .setIsCyclical(true)
      .setTimeInterpolator(AccelerateDecelerateInterpolator())
      .setImgAdapter(
        ImageViewAdapter.Builder(colorList)
          .onBind {
            view.setBackgroundColor(data)
          }
      )
  }
}