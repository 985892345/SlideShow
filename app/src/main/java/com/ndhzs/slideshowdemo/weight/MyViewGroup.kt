package com.ndhzs.slideshowdemo.weight

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/10
 */
class MyViewGroup(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
//                Log.d("123","(MyViewGroup.kt:20)-->> Down")
            }
            MotionEvent.ACTION_MOVE -> {
                //Log.d("123","(MyViewGroup.kt:23)-->> Move")
            }
            MotionEvent.ACTION_UP -> {
               // Log.d("123","(MyViewGroup.kt:26)-->> Up")
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                //Log.d("123","(MyViewGroup.kt:37)-->> Down")
            }
            MotionEvent.ACTION_MOVE -> {
                //Log.d("123","(MyViewGroup.kt:40)-->> Move")
            }
            MotionEvent.ACTION_UP ->{
                //Log.d("123","(MyViewGroup.kt:43)-->> Up")
            }
        }
        return true
//        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }
}