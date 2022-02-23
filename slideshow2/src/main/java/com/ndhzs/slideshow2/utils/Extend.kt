package com.ndhzs.slideshow2.utils

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/23 10:51
 */

internal fun <T> lazyUnlock(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)