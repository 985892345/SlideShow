package com.ndhzs.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ndhzs.slideshow.SlideShow
import com.ndhzs.slideshow.viewpager2.transformer.MZScaleInTransformer
import com.ndhzs.slideshow.viewpager2.transformer.ZoomOutPageTransformer
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var mSlideShow: SlideShow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSlideShow = findViewById(R.id.slideShow)

        val colorList = listOf(0xFFC8C8C8.toInt(), 0xFF616161.toInt(), 0xFFC8C8C8.toInt())

        mSlideShow.setTransformer(MZScaleInTransformer()) // 设置移动动画
            .setStartItem(1) // 设置起始位置
            .setAdapter(colorList) {
                data, imageView, holder, position ->
                imageView.setBackgroundColor(data)
        }

        /*
        * 下面是演示刷新
        * */
        thread {
            sleep(5000)
            mSlideShow.notifyImageViewRefresh(0, true) {
                    imageView, holder, position ->
                imageView.setBackgroundColor(0xFFFF9800.toInt())
            }
        }
    }
}