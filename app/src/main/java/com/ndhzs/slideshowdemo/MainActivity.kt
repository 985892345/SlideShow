package com.ndhzs.slideshowdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import com.ndhzs.slideshow2.SlideShow2
import com.ndhzs.slideshow2.base.ImageViewAdapter
import com.ndhzs.slideshow2.utils.setImgAdapter
import com.ndhzs.slideshow2.viewpager.transformer.ScaleInTransformer

class MainActivity : AppCompatActivity() {

    private lateinit var mSlideShow: SlideShow2

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
//            .setIsAutoSlide(true)
//            .setIsCyclical(true)
            .setTimeInterpolator(AccelerateDecelerateInterpolator())
            .setImgAdapter(
                ImageViewAdapter(colorList) { img, pos ->
                    img.setBackgroundColor(colorList[pos])
                }
            )
    }
}