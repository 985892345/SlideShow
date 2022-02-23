package com.ndhzs.slideshow2.utils

import com.ndhzs.slideshow2.SlideShow2
import com.ndhzs.slideshow2.base.ImageViewAdapter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/23 19:46
 */

fun <T> SlideShow2.setImgAdapter(imgAdapter: ImageViewAdapter<T>) {
    setAdapter(imgAdapter)
}