package com.ndhzs.slideshow.utils

import android.view.View
import androidx.collection.ArrayMap

/**
 * 用于收集 post 和 postDelay 来解决内存泄漏问题
 *
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/29
 */
class RunnableManger(private val view: View) {

    private val map = ArrayMap<Runnable, Runnable>()

    fun post(runnable: Runnable) {
        val run = Runnable {
            runnable.run()
            map.remove(runnable)
        }
        map[runnable] = run
        view.post(run)
    }

    fun postDelay(delayMillis: Long, runnable: Runnable) {
        val run = Runnable {
            runnable.run()
            map.remove(runnable)
        }
        map[runnable] = run
        view.postDelayed(run, delayMillis)
    }

    fun remove(runnable: Runnable): Boolean {
        val run = map.remove(runnable)
        view.removeCallbacks(run)
        return run != null
    }

    fun destroy() {
        map.forEach {
            view.removeCallbacks(it.value)
        }
        map.clear()
    }
}