package com.ndhzs.slideshow.viewpager2.adapter

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.ndhzs.slideshow.utils.SlideShowAttrs

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/7/16
 */
abstract class BaseViewAdapter<V: View>(
    private val viewClass: Class<V>
) : RecyclerView.Adapter<BaseViewAdapter<V>.BaseViewHolder>() {

    protected lateinit var attrs: SlideShowAttrs

    /**
     * **WARNING：** 因为该方法有特殊实现，所以禁止重写
     */
    @Deprecated("禁止重写! ")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder {
        parent.setBackgroundColor(0x00000000)
        return BaseViewHolder(ViewLayout(parent.context, viewClass, viewType))
    }

    /**
     * **WARNING：** 因为该方法有特殊实现，所以禁止重写
     */
    @Deprecated(
        "禁止重写! ",
        ReplaceWith("onBindViewHolder(view, holder, position, payloads)"))
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        // nothing
        // 使用了带有 payloads 参数的 onBindViewHolder 方法
    }

    /**
     * **WARNING：** 因为该方法有特殊实现，所以禁止重写
     */
    @Deprecated(
        "禁止重写! ",
        ReplaceWith("onBindViewHolder(view, holder, position, payloads)"))
    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        onBindViewHolder(holder.view, holder, position, payloads)
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    internal fun initializeAttrs(attrs: SlideShowAttrs) {
        this.attrs = attrs
    }

    abstract fun onBindViewHolder(view: V, holder: BaseViewHolder, position: Int, payloads: MutableList<Any>)

    /**
     * 该方法用于在 onCreateViewHolder 调用时配置自己 view 的一些属性
     */
    open fun onConfigureView(view: V, viewType: Int) {}

    inner class BaseViewHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        val view = itemView.getChildAt(0) as V
    }

    inner class ViewLayout(context: Context, viewClass: Class<V>, viewType: Int) : FrameLayout(context) {
        init {
            val view = viewClass.getConstructor(Context::class.java).newInstance(context)
            val lp = LayoutParams(attrs.viewWidth, attrs.viewHeight)
            lp.gravity = Gravity.CENTER
            lp.leftMargin = attrs.viewMarginHorizontal
            lp.topMargin = attrs.viewMarginVertical
            lp.rightMargin = attrs.viewMarginHorizontal
            lp.bottomMargin = attrs.viewMarginVertical
            onConfigureView(view, viewType)
            val lpFl = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutParams = lpFl
            attachViewToParent(view, -1, lp)
            setBackgroundColor(0x00000000)
        }
    }
}