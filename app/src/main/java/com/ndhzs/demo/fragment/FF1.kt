package com.ndhzs.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ndhzs.demo.R
import com.ndhzs.demo.adapter.BaseSimplifyRecyclerAdapter
import com.ndhzs.slideshow.SlideShow

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/30
 */
class FF1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fg_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recycler = view.findViewById<RecyclerView>(R.id.fg_1_recycler)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler
            .adapter = BaseSimplifyRecyclerAdapter(10)
            .onBindView(R.layout.item_recycler_other, OtherViewHolder::class.java,
                { true },
                { holder, position ->

                })
        super.onViewCreated(view, savedInstanceState)
    }

    class OtherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}