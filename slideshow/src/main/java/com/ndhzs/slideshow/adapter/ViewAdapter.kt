package com.ndhzs.slideshow.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ndhzs.slideshow.SlideShow
import com.ndhzs.slideshow.utils.forEachInline

/**
 * 单一 View 的 Adapter
 *
 * - 不支持多 ViewHolder
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/16 10:55
 */
open class ViewAdapter<T, V: View> protected constructor(
  private val builder: Builder<T, V>
): RecyclerView.Adapter<ViewAdapter<T, V>.Holder>() {
  
  inner class Holder(val view: V) : RecyclerView.ViewHolder(view) {
    val wrapper = Wrapper(this)
  }
  
  inner class Wrapper(val holder: Holder) {
    val view: V
      get() = holder.view
    val realPosition: Int
      get() = holder.layoutPosition % itemCount
    val data: T
      get() = builder.data[realPosition]
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    val view = builder.newInstance(parent)
    val holder = Holder(view)
    builder.onCreate.forEachInline { it.invoke(holder.wrapper) }
    return holder
  }
  
  override fun onBindViewHolder(holder: Holder, position: Int) {
    builder.onBind.forEachInline {
      it.invoke(holder.wrapper)
    }
  }
  
  override fun getItemCount(): Int {
    return builder.data.size
  }
  
  open class Builder<T, V : View>(
    internal val data: List<T>,
    internal val newInstance: ViewGroup.() -> V
  ) {
    internal val onCreate = ArrayList<ViewAdapter<T, V>.Wrapper.() -> Unit>(2)
    internal val onBind = ArrayList<ViewAdapter<T, V>.Wrapper.() -> Unit>(2)
  
    open fun onCreate(call: ViewAdapter<T, V>.Wrapper.() -> Unit): Builder<T, V> {
      onCreate.add(call)
      return this
    }
  
    open fun onBind(call: ViewAdapter<T, V>.Wrapper.() -> Unit): Builder<T, V> {
      onBind.add(call)
      return this
    }
    
    open fun build(): ViewAdapter<T, V> {
      return ViewAdapter(this)
    }
  }
}

fun <T, V: View> SlideShow.setViewAdapter(viewAdapterBuilder: ViewAdapter.Builder<T, V>) {
  setAdapter(viewAdapterBuilder.build())
}