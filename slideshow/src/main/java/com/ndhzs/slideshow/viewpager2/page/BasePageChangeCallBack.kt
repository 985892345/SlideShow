package com.ndhzs.slideshow.viewpager2.page

import androidx.viewpager2.widget.ViewPager2

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/28
 */
class BasePageChangeCallBack : ViewPager2.OnPageChangeCallback() {

    private lateinit var callBack: ViewPager2.OnPageChangeCallback

    fun setPageChangeCallBack(callBack: ViewPager2.OnPageChangeCallback) {
        this.callBack = callBack
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (this::callBack.isInitialized) {
            callBack.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    override fun onPageSelected(position: Int) {
        if (this::callBack.isInitialized) {
            callBack.onPageSelected(position)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (this::callBack.isInitialized) {
            callBack.onPageScrollStateChanged(state)
        }
    }
}