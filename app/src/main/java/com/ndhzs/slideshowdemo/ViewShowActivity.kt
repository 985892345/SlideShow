package com.ndhzs.slideshowdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ndhzs.slideshowdemo.fragment.FF1
import com.ndhzs.slideshowdemo.fragment.FF2
import com.ndhzs.slideshowdemo.fragment.FF3
import com.ndhzs.slideshow.SlideShow

class ViewShowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewshow)

        val slideShow = findViewById<SlideShow>(R.id.viewpager_slideShow)

        val fragments = ArrayList<Fragment>()
        fragments.add(FF1())
        fragments.add(FF2())
        fragments.add(FF3())

        slideShow
            .setUserInputEnabled(true)
            .setOpenNestedScroll(true)
            .setAdapter(this, fragments)

        findViewById<Button>(R.id.btn_1).setOnClickListener {
            slideShow.setCurrentItem(0)
        }

        findViewById<Button>(R.id.btn_2).setOnClickListener {
            slideShow.setCurrentItem(1)
        }

        findViewById<Button>(R.id.btn_3).setOnClickListener {
            slideShow.setCurrentItem(2)
        }
    }
}