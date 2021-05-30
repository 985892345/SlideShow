package com.ndhzs.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ndhzs.demo.R
import com.ndhzs.demo.adapter.RecyclerAdapter

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/30
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fg_1, container, false)
    }
}