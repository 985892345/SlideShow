package com.ndhzs.slideshow.viewpager2.pagecallback

import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.slideshow.myinterface.IIndicator

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/28
 */
internal class BasePageChangeCallBack(
    private val viewPager2: ViewPager2,
) : ViewPager2.OnPageChangeCallback() {

    companion object {
        /**
         * 开启循环后，两边会多出4块，且是重复的
         * **WARNING：** [mItemCount] 在以下图中为 7                        mItemCount - 3  mItemCount - 2  mItemCount - 1
         * ------------    ------------    ------------    ------------    ------------    ------------    ------------
         * |          |    |          |    |          |    |          |    |          |    |          |    |          |
         * |          |    |          |    |     0    |    |     1    |    |     2    |    |          |    |          |
         * |     b    |    |     c    |    |     a    |    |     b    |    |     c    |    |     a    |    |     b    |
         * |     0    |    |     1    |    |     2    |    |     3    |    |     4    |    |     5    |    |     6    |
         * |          |    |          |    |          |    |          |    |          |    |          |    |          |
         * ------------    ------------    ------------    ------------    ------------    ------------    ------------
         *                                 |------------------------------------------|
         *                                                 FalsePosition
         * |----------------------------------------------------------------------------------------------------------|
         *                                                  RealPosition
         */
        fun getRealPosition(isCirculate: Boolean, falsePosition: Int): Int {
            return if (isCirculate) {
                falsePosition + 2
            }else falsePosition
        }

        fun getFalsePosition(isCirculate: Boolean, realPosition: Int, realAmount: Int): Int {
            return if (isCirculate) {
                if (realPosition <= 1) {
                    realPosition + realAmount - 6
                }else if (realPosition >= realAmount - 2) {
                    realPosition - realAmount + 2
                }else realPosition - 2
            }else realPosition
        }

        fun getFalseAmount(isCirculate: Boolean, realAmount: Int): Int {
            return if (isCirculate) {
                realAmount - 4
            }else realAmount
        }

        fun getRealAmount(isCirculate: Boolean, falsePosition: Int): Int {
            return if (isCirculate) {
                falsePosition + 4
            }else falsePosition
        }

        fun getAllRealPositionArray(isCirculate: Boolean, falsePosition: Int, realAmount: Int): IntArray {
            return if (isCirculate) {
                if (falsePosition <= 1) {
                    intArrayOf(falsePosition + 2, falsePosition + realAmount - 2)
                }else if (falsePosition >= realAmount - 6)  {
                    intArrayOf(falsePosition - realAmount + 6, falsePosition + 2)
                }else {
                    intArrayOf(falsePosition + 2)
                }
            }else {
                intArrayOf(falsePosition)
            }
        }

        fun getAnotherRealPosition(isCirculate: Boolean, realPosition: Int, realAmount: Int): Int {
            return if (isCirculate) {
                if (realPosition <= 3) {
                    realPosition + realAmount - 4
                }else if (realPosition >= realAmount - 4) {
                    realPosition - realAmount + 4
                }else {
                    realPosition
                }
            }else realPosition
        }
    }

    fun setPageChangeCallBack(callBack: ViewPager2.OnPageChangeCallback) {
        mCallBack = callBack
    }

    fun removePageChangeCallback() {
        mCallBack = null
    }

    fun openCirculateEnabled(itemCount: Int) {
        if (!mIsCirculate) {
            if (itemCount > 1) {
                mIsCirculate = true
                mItemCount = itemCount + 4
            }
        }
    }

    fun setIndicators(indicators: IIndicator) {
        mIndicators = indicators
    }

    private var mIndicators: IIndicator? = null
    private var mCallBack: ViewPager2.OnPageChangeCallback? = null
    private var mPositionFloat = 0F
    private var mIsCirculate = false
    private var mItemCount = 1

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mPositionFloat = position + positionOffset
        pageScrolledCallback(position, positionOffset, positionOffsetPixels)
    }

    /**
     * 开启循环后，两边会多出4块，且是重复的
     * **WARNING：** [mItemCount] 在以下图中为 7                        mItemCount - 3  mItemCount - 2  mItemCount - 1
     * ------------    ------------    ------------    ------------    ------------    ------------    ------------
     * |          |    |          |    |          |    |          |    |          |    |          |    |          |
     * |          |    |          |    |     0    |    |     1    |    |     2    |    |          |    |          |
     * |     b    |    |     c    |    |     a    |    |     b    |    |     c    |    |     a    |    |     b    |
     * |     0    |    |     1    |    |     2    |    |     3    |    |     4    |    |     5    |    |     6    |
     * |          |    |          |    |          |    |          |    |          |    |          |    |          |
     * ------------    ------------    ------------    ------------    ------------    ------------    ------------
     *                                 |------------------------------------------|
     *                                                 FalsePosition
     * |----------------------------------------------------------------------------------------------------------|
     *                                                  RealPosition
     */
    private fun pageScrolledCallback(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mIsCirculate) {
            if (mPositionFloat in 0.95F..1.05F) {
                viewPager2.setCurrentItem(
                    getAnotherRealPosition(mIsCirculate, 1, mItemCount),false
                )
            }else if (mPositionFloat in mItemCount - 3 + 0.95F..mItemCount - 2 + 0.05F) {
                viewPager2.setCurrentItem(
                    getAnotherRealPosition(mIsCirculate, mItemCount - 2, mItemCount), false
                )
            }else if (mPositionFloat in 0.001F..0.1F) {
                viewPager2.setCurrentItem(
                    getAnotherRealPosition(mIsCirculate, 0, mItemCount),false
                )
            }else if (mPositionFloat in mItemCount - 2 + 0.9F..mItemCount - 5 + 0.999F) {
                viewPager2.setCurrentItem(
                    getAnotherRealPosition(mIsCirculate, mItemCount - 1, mItemCount), false
                )
            }
            if (mPositionFloat < 2 || mPositionFloat > mItemCount - 3) {
                if (mPositionFloat < 2) {
                    mIndicators?.onPageScrolled(
                        getFalsePosition(mIsCirculate, 2, mItemCount),
                        0F, 0)
                    mCallBack?.onPageScrolled(
                        getFalsePosition(mIsCirculate, 2, mItemCount),
                        0F, 0)
                }
                if (mPositionFloat > mItemCount - 3) {
                    mIndicators?.onPageScrolled(
                        getFalsePosition(mIsCirculate, mItemCount - 3, mItemCount),
                        0F, 0)
                    mCallBack?.onPageScrolled(
                        getFalsePosition(mIsCirculate, mItemCount - 3, mItemCount),
                        0F, 0)
                }
                return
            }
        }
        val falsePosition = getFalsePosition(mIsCirculate, position, mItemCount)
        mIndicators?.onPageScrolled(
            falsePosition,
            positionOffset,
            positionOffsetPixels)
        mCallBack?.onPageScrolled(
            falsePosition,
            positionOffset,
            positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        pageSelected(position)
    }

    private fun pageSelected(position: Int) {
        val falsePosition = getFalsePosition(mIsCirculate, position, mItemCount)
        mIndicators?.onPageSelected(falsePosition)
        mCallBack?.onPageSelected(falsePosition)
    }

    override fun onPageScrollStateChanged(state: Int) {
        mIndicators?.onPageScrollStateChanged(state)
        mCallBack?.onPageScrollStateChanged(state)
    }
}