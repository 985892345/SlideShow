package com.ndhzs.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow.SlideShow
import com.ndhzs.slideshow.viewpager2.transformer.*

class MainActivity : AppCompatActivity() {

    private lateinit var mSlideShow: SlideShow

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

        mSlideShow = findViewById(R.id.slideShow)

        val list = MutableLiveData<List<Int>>()
        val colorList = listOf(0xFFFF9800.toInt(), 0xFF616161.toInt(), 0xFFC8C8C8.toInt())

        mSlideShow.setTransformer(ScaleInTransformer()) // 设置移动动画
                .setAutoSlideEnabled(true)
                .setDelayTime(5000)
                .openCirculateEnabled()
                .setTimeInterpolator(AccelerateDecelerateInterpolator())
                .setStartItem(1) // 设置起始位置
                .setAdapter(this, list) {
                    data, imageView, holder, position ->
                    imageView.setBackgroundColor(data)
        }
        list.value = colorList

        /*
        * 进行滑动监听
        * */
        mSlideShow.setPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })

        findViewById<Button>(R.id.button).setOnClickListener {
            val intent = Intent(this, ViewShowActivity::class.java)
            startActivity(intent)
        }

        /*
        * 下面是演示刷新
        * */
        mSlideShow.postDelayed({
            Toast.makeText(this, "开始更新", Toast.LENGTH_SHORT).show()
            list.value = listOf(0xFF009688.toInt(), 0xFFC8C8C8.toInt(), 0xFFFF9800.toInt())
//            mSlideShow.notifyImageViewRefresh(0, Refresh.Condition.COVERED) { imageView, holder, position ->
//                imageView.setBackgroundColor()
//            }
        }, 5000)
    }
}