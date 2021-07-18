package com.ndhzs.slideshow.utils

import android.content.res.Resources

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/17
 */
internal class SlideShowUtils {

    companion object {
        fun dpToPx(dp: Int): Float {
            val scale = Resources.getSystem().displayMetrics.density
            return dp * scale
        }

        fun dpToPx(dp: Float): Float {
            val scale = Resources.getSystem().displayMetrics.density
            return dp * scale
        }
    }
}