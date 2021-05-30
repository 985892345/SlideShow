package com.ndhzs.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ndhzs.demo.fragment.FirstFragment
import com.ndhzs.demo.fragment.SecondFragment
import com.ndhzs.demo.fragment.ThirdFragment
import com.ndhzs.slideshow.SlideShow

class ViewShowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewshow)

        val viewShow = findViewById<SlideShow>(R.id.viewpager_slideShow)

        val fragments = ArrayList<Fragment>()
        fragments.add(FirstFragment())
        fragments.add(SecondFragment())
        fragments.add(ThirdFragment())

        viewShow.setAdapter(fragments, this)
    }
}