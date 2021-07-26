package com.ndhzs.slideshowdemo.weight

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/10
 */
class MyRecyclerView(context: Context, attrs: AttributeSet?)
    : RecyclerView(context, attrs) {
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
//                Log.d("123","(MyRecyclerView.kt:21)-->> TOUCH_SLOP = ${ViewConfiguration.get(context).scaledTouchSlop}")
//                Log.d("123","(MyRecyclerView.kt:21)-->> Recycler_ Down    state = $scrollState")
            }
            MotionEvent.ACTION_MOVE -> {
//                Log.d("123","(MyRecyclerView.kt:24)-->> Recycler_Move   x = ${ev.x.toInt()}   y = ${ev.y.toInt()}   state = $scrollState")
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
//                Log.d("123","(MyRecyclerView.kt:38)-->> I_Recycler_Down")
            }
            MotionEvent.ACTION_MOVE -> {
//                Log.d("123","(MyRecyclerView.kt:41)-->> I_Recycler_Move")
            }
        }

        return super.onInterceptTouchEvent(e)
    }
}