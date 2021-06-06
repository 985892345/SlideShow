package com.ndhzs.demo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.ndhzs.demo.R
import com.ndhzs.slideshow.SlideShow
import com.ndhzs.slideshow.viewpager2.transformer.AlphaPageTransformer
import com.ndhzs.slideshow.viewpager2.transformer.ScaleInTransformer

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/30
 */
class RecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val SLIDE_VIEW = 0
        const val OTHER_VIEW = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            SLIDE_VIEW -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_slideshow, parent, false)
                return SlideShowViewHolder(view)
            }
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_other, parent, false)
        return OtherViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("123","(RecyclerAdapter.kt:41)-->> position = $position")
        when (getItemViewType(position)) {
            SLIDE_VIEW -> {
                val slideHolder = holder as SlideShowViewHolder
                val slideShow = slideHolder.slideView
                val colorList = listOf(0xFFFF9800.toInt(), 0xFF616161.toInt(), 0xFFC8C8C8.toInt())
                slideShow.addTransformer(ScaleInTransformer()) // 设置移动动画
                    .addTransformer(AlphaPageTransformer())
                    .setAutoSlideEnabled(true)
                    .setStartItem(1) // 设置起始位置
                    .setDelayTime(5000) // 设置自动滚动时间，但目前还没有实现自动滚动
                    .setTimeInterpolator(AccelerateDecelerateInterpolator())
                    .setAdapter(colorList) { data, imageView, _, _ ->
                        imageView.setBackgroundColor(data)
                    }
            }
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            SLIDE_VIEW
        }else {
            OTHER_VIEW
        }
    }

    class SlideShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slideView: SlideShow = itemView.findViewById(R.id.recycler_slideShow)
    }

    class OtherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}