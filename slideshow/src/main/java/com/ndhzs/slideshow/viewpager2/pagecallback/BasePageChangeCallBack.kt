package com.ndhzs.slideshow.viewpager2.pagecallback

import android.util.Log
import androidx.viewpager2.widget.ViewPager2

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/28
 */
class BasePageChangeCallBack(
        private val viewPager2: ViewPager2,
        private val positionChangeCallback: ((position: Int) -> Unit)
) : ViewPager2.OnPageChangeCallback() {

    fun setPageChangeCallBack(callBack: ViewPager2.OnPageChangeCallback) {
        mCallBack = callBack
    }

    fun removePageChangeCallback() {
        mCallBack = null
    }

    fun openCirculateEnabled(itemCount: Int) {
        if (itemCount > 1) {
            mIsCirculate = true
            mItemCount = itemCount + 4
        }
    }

    private var mCallBack: ViewPager2.OnPageChangeCallback? = null
    private var mPositionFloat = 0F
    private var mIsCirculate = false
    private var mItemCount = 1

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mPositionFloat = position + positionOffset
        pageScrolledCallback(position, positionOffset, positionOffsetPixels)
    }

    private fun pageScrolledCallback(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        var realPosition = position
        var isCallback = true
        if (mIsCirculate) {
            if (position <= 1) {
                isCallback = false
            }else if (position == mItemCount - 3) {
                if (positionOffset > 0) {
                    isCallback = false
                }
            }else if (position > mItemCount - 3) {
                isCallback = false
            }
            realPosition -= 2
        }
        if (isCallback) {
            mCallBack?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels)
        }
    }

    override fun onPageSelected(position: Int) {
        pageSelected(position)
    }

    private fun pageSelected(position: Int) {
        var realPosition = position
        var isCallback = true
        if (mIsCirculate) {
            if (position <= 1) {
                isCallback = false
            }else if (position > mItemCount - 3) {
                isCallback = false
            }
            realPosition -= 2
        }
        if (isCallback) {
            positionChangeCallback.invoke(realPosition)
            mCallBack?.onPageSelected(realPosition)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager2.SCROLL_STATE_IDLE -> {
                if (mItemCount != 1) {
                    if (mPositionFloat <= 1F) {
                        viewPager2.setCurrentItem(mPositionFloat.toInt() + mItemCount - 4, false)
                    }else if (mPositionFloat >= (mItemCount - 2).toFloat()) {
                        viewPager2.setCurrentItem(mItemCount - mPositionFloat.toInt(), false)
                    }
                }
            }
            ViewPager2.SCROLL_STATE_DRAGGING -> {

            }
            ViewPager2.SCROLL_STATE_SETTLING -> {

            }
        }
        mCallBack?.onPageScrollStateChanged(state)
    }
}