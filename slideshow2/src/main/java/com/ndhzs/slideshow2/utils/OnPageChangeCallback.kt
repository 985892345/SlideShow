package com.ndhzs.slideshow2.utils

import androidx.annotation.Px
import androidx.viewpager2.widget.ViewPager2.ScrollState

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/23 10:28
 */
interface OnPageChangeCallback {
  fun onPageScrolled(position: Int, positionOffset: Float, @Px positionOffsetPixels: Int) {}
  
  fun onPageSelected(position: Int) {}
  
  fun onPageScrollStateChanged(@ScrollState state: Int) {}
}