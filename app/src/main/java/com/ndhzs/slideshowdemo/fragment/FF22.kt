package com.ndhzs.slideshowdemo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ndhzs.slideshowdemo.R
import com.ndhzs.slideshowdemo.adapter.BaseSimplifyRecyclerAdapter
import com.ndhzs.slideshow.SlideShow
import com.ndhzs.slideshow.viewpager2.transformer.AlphaPageTransformer
import com.ndhzs.slideshow.viewpager2.transformer.ScaleInTransformer

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/3
 */
class FF22 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fg_2_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.fg_2_2_recycler)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler
            .adapter = BaseSimplifyRecyclerAdapter(10)
            .onBindView(R.layout.item_recycler_slideshow, SlideShowViewHolder::class.java,
                { position -> position == 0 },
                { holder, _ ->
                    val slideShow = holder.slideView
                    val colorList = listOf(0xFFFF9800.toInt(), 0xFF616161.toInt(), 0xFFC8C8C8.toInt())
                    slideShow.addTransformer(ScaleInTransformer()) // 设置移动动画
                        .addTransformer(AlphaPageTransformer())
                        .setStartItem(1) // 设置起始位置
                        .setDelayTime(5000) // 设置自动滚动时间，但目前还没有实现自动滚动
                        .setTimeInterpolator(AccelerateDecelerateInterpolator())
                        .setAdapter(colorList) { data, imageView, _, _ ->
                            imageView.setBackgroundColor(data)
                        }
                })
            .onBindView(R.layout.item_recycler_other, OtherViewHolder::class.java,
                { position -> position > 0 },
                { holder, position ->

                })
    }

    class SlideShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slideView: SlideShow = itemView.findViewById(R.id.recycler_slideShow)
    }

    class OtherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}