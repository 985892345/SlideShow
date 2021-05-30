package com.ndhzs.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow.SlideShow
import com.ndhzs.slideshow.utils.Refresh
import com.ndhzs.slideshow.viewpager2.transformer.*

class MainActivity : AppCompatActivity() {

    private lateinit var mSlideShow: SlideShow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSlideShow = findViewById(R.id.slideShow)

        val colorList = listOf(0xFFFF9800.toInt(), 0xFF616161.toInt(), 0xFFC8C8C8.toInt())

        mSlideShow.setTransformer(ScaleInTransformer()) // 设置移动动画
                .setAutoSlideEnabled(true)
                .setDelayTime(5000)
                .setTimeInterpolator(AccelerateDecelerateInterpolator())
                .openCirculateEnabled()
                .setStartItem(1) // 设置起始位置
                .setAdapter(colorList) {
                    data, imageView, holder, position ->
                    imageView.setBackgroundColor(data)
        }

        /*
        * 进行滑动监听
        * */
        mSlideShow.setPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })

        /*
        * 下面是演示刷新
        * */
//        mSlideShow.postDelayed({
//            mSlideShow.notifyImageViewRefresh(0, Refresh.Condition.COVERED) { imageView, holder, position ->
//                imageView.setBackgroundColor(0xFF009688.toInt())
//            }
//        }, 5000)
    }
}