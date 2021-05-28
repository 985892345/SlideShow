package com.ndhzs.slideshow

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.imageview.ShapeableImageView
import com.ndhzs.slideshow.utils.*
import com.ndhzs.slideshow.viewpager2.adapter.BaseFragmentStateAdapter
import com.ndhzs.slideshow.viewpager2.adapter.BaseImgAdapter
import com.ndhzs.slideshow.viewpager2.page.BasePageChangeCallBack
import com.ndhzs.slideshow.viewpager2.transformer.*

/**
 * 该控件参考了第三方库：Banner，在此表示感谢！
 *
 * 里面内置了 transformer（页面移动动画）的默认实现类，分别有：
 *
 * [AlphaPageTransformer]
 *
 * [DepthPageTransformer]
 *
 * [MZScaleInTransformer]
 *
 * [RotateDownPageTransformer]
 *
 * [RotateUpPageTransformer]
 *
 * [RotateYTransformer]
 *
 * [ScaleInTransformer]
 *
 * [ZoomOutPageTransformer]
 *
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/26
 */
class SlideShow(context: Context, attrs: AttributeSet?) : CardView(context, attrs) {

    /**
     * 该方法只能设置一个 transformer（页面移动动画）
     *
     * 已默认建了几个 transformer，名字分别为：
     *
     * [AlphaPageTransformer]
     *
     * [DepthPageTransformer]
     *
     * [MZScaleInTransformer]
     *
     * [RotateDownPageTransformer]
     *
     * [RotateUpPageTransformer]
     *
     * [RotateYTransformer]
     *
     * [ScaleInTransformer]
     *
     * [ZoomOutPageTransformer]
     * @see [addTransformer]
     */
    fun setTransformer(transformer: ViewPager2.PageTransformer): SlideShow {
        clearTransformer()
        mPageTransformers.addTransformer(transformer)
        return this
    }

    /**
     * 该方法可以设置多个 transformer（页面移动动画）
     * @see [setTransformer]
     */
    fun addTransformer(transformer: ViewPager2.PageTransformer): SlideShow {
        mPageTransformers.addTransformer(transformer)
        return this
    }

    /**
     * 该方法用于删除 transformer（页面移动动画）
     * @see [clearTransformer]
     */
    fun deleteTransformer(transformer: ViewPager2.PageTransformer) {
        mPageTransformers.removeTransformer(transformer)
    }

    /**
     * 该方法用于删除全部 transformer（页面移动动画）
     * @see [deleteTransformer]
     */
    fun clearTransformer() {
        mPageTransformers.mTransformers.clear()
    }

    /**
     * 虽然可以得到 ViewPager2，但建议最好不要在 ViewPager2 中设置一些东西
     */
    fun getViewPager2(): ViewPager2 {
        return mViewPager2
    }

    /**
     * 用于设置图片加载的 Adapter
     *
     * **NOTICE：** 如果你想使一个页面能看到相邻的图片边缘，
     *
     * 1、请设置 app:slide_imgWight 或者
     *
     * app:slide_imgMarginLeft 和 app:slide_imgMarginRight
     *
     * 2、再设置 app:slide_pageInterval 或者调用 [setPageInterval]
     */
    fun <T> setAdapter(datas: List<T>, imgAdapter: BaseImgAdapter<T>): SlideShow {
        imgAdapter.setData(datas)
        mViewPager2.adapter = imgAdapter
        return this
    }

    /**
     * 用于设置图片加载的 Adapter，使用 Lambda 填写
     *
     * **NOTICE：** 如果你想使一个页面能看到相邻的图片边缘，
     *
     * 1、请设置 app:slide_imgWight 或者
     *
     * app:slide_imgMarginLeft 和 app:slide_imgMarginRight
     *
     * 2、再设置 app:slide_pageInterval 或者调用 [setPageInterval]
     */
    fun <T> setAdapter(
        datas: List<T>, onBindImageView:
            (
            data: T,
            imageView: ShapeableImageView,
            holder: BaseImgAdapter.BaseImgViewHolder,
            position: Int
        ) -> Unit
    ): SlideShow {
        val adapter = object : BaseImgAdapter<T>() {
            override fun onBindImageView(
                data: T,
                imageView: ShapeableImageView,
                holder: BaseImgViewHolder,
                position: Int
            ) {
                onBindImageView.invoke(data, imageView, holder, position)
            }
        }
        return setAdapter(datas, adapter)
    }

    /**
     * 用于设置 FragmentStateAdapter，该方法简写了创建 FragmentStateAdapter 的过程，传入数据后会自动帮你设置 FragmentStateAdapter
     *
     * **WARNING：** 使用该方法可能意为着你不想自动滑动，请使用 [setAutoSlideEnabled]
     */
    fun setAdapter(fragments: List<Fragment>, fragmentActivity: FragmentActivity): SlideShow {
        val adapter = object : BaseFragmentStateAdapter(fragmentActivity) {}
        adapter.setFragments(fragments)
        mViewPager2.adapter = adapter
        return this
    }

    /**
     * 用于设置 FragmentStateAdapter
     *
     * **WARNING：** 使用该方法可能意为着你不想自动滑动，请使用 [setAutoSlideEnabled]
     */
    fun setAdapter(fragmentAdapter: FragmentStateAdapter): SlideShow {
        mViewPager2.adapter = fragmentAdapter
        return this
    }

    /**
     * 用于设置通用 RecyclerView.Adapter
     *
     * **WARNING：** 使用该方法可能意为着你不想自动滑动，请使用 [setAutoSlideEnabled]
     */
    fun setAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>): SlideShow {
        mViewPager2.adapter = adapter
        return this
    }

    /**
     * 通知位置为 position 的 imageView 刷新
     *
     * **NOTICE：** 使用该方法的前提是 [setAdapter] 是用的 [BaseImgAdapter]，否则将报错
     *
     * @param isAsyncRefresh 是否异步刷新
     */
    fun notifyImageViewRefresh(position: Int, isAsyncRefresh: Boolean = false, l: OnRefreshListener) {
        val adapter = mViewPager2.adapter
        if (adapter is BaseImgAdapter<*>) {
            if (isAsyncRefresh) {
                post {
                    adapter.notifyItemChanged(position, l)
                }
            }else {
                adapter.notifyItemChanged(position, l)
            }
        }else {
            throw IllegalAccessException(
                "Your ${Attrs.Library_name} of adapter is not BaseImgAdapter, you can't use function of notifyImageViewRefresh!")
        }
    }

    /**
     * 通知位置为 position 的 imageView 刷新，使用 Lambda 填写
     *
     * **NOTICE：** 使用该方法的前提是 [setAdapter] 是用的 [BaseImgAdapter]，否则将报错
     *
     * @param isAsyncRefresh 是否异步刷新
     */
    fun notifyImageViewRefresh(position: Int,
                               isAsyncRefresh: Boolean = false,
                               onRefresh: (
                                   imageView: ShapeableImageView,
                                   holder: BaseImgAdapter.BaseImgViewHolder,
                                   position: Int) -> Unit
                               ) {
        val l = object : OnRefreshListener {
            override fun onRefresh(
                imageView: ShapeableImageView,
                holder: BaseImgAdapter.BaseImgViewHolder,
                position: Int
            ) {
                onRefresh.invoke(imageView, holder, position)
            }
        }
        return notifyImageViewRefresh(position, isAsyncRefresh, l)
    }

    /**
     * 设置是否自动滑动
     */
    fun setAutoSlideEnabled(enabled: Boolean): SlideShow {
        mIsAutoSlideEnabled = enabled
        return this
    }

    /**
     * 与 ViewPager2#setCurrentItem 相同，默认有动画
     * @see [setStartItem]
     */
    fun setCurrentItem(item: Int, smoothScroll: Boolean = true): SlideShow {
        mViewPager2.setCurrentItem(item, smoothScroll)
        return this
    }

    /**
     * 设置自动滑动的延迟时间，默认时间是 4 秒
     */
    fun setDelayTime(delayTime: Long): SlideShow {
        mDelayTime = delayTime
        return this
    }

    /**
     * 使用自带的图片加载的 setAdapter 方法后可以调用，用于设置内部 ImageView 的圆角
     */
    fun setImgRadius(radius: Float): SlideShow {
        Attrs.imgLeftTopRadius = radius
        Attrs.imgRightTopRadius = radius
        Attrs.imgLeftBottomRadius = radius
        Attrs.imgRightBottomRadius = radius
        return this
    }

    /**
     * 使用自带的图片加载的 setAdapter 方法后可以调用，用于设置内部 ImageView 的圆角
     */
    fun setImgRadius(leftTop: Float, rightTop: Float, leftBottom: Float, rightBottom: Float): SlideShow {
        Attrs.imgLeftTopRadius = leftTop
        Attrs.imgRightTopRadius = rightTop
        Attrs.imgLeftBottomRadius = leftBottom
        Attrs.imgRightBottomRadius = rightBottom
        return this
    }

    /**
     * 设置指示器的横幅背景颜色
     */
    fun setIndicatorsBannerColor(color: Int): SlideShow {
        Attrs.indicatorBannerColor = color
        return this
    }

    /**
     * 设置指示器的圆点颜色
     */
    fun setIndicatorsColor(color: Int): SlideShow {
        Attrs.indicatorColor = color
        return this
    }

    /**
     * 设置指示器的圆点半径
     */
    fun setIndicatorsRadius(radius: Float): SlideShow {
        Attrs.indicatorRadius = radius
        return this
    }

    /**
     * 设置指示器的位置
     * @param gravity 数据来自 [IndicatorsGravity]
     */
    fun setIndicatorsGravity(@Indicators.Gravity gravity: Int): SlideShow {
        Attrs.indicatorGravity = gravity
        return this
    }

    /**
     * 得到指示器的位置
     */
    @Indicators.Gravity
    fun getIndicatorsGravity(): Int {
        return Attrs.indicatorGravity
    }

    /**
     * 设置指示器的样式
     * @see style 数据来自 [IndicatorsStyle]
     */
    fun setIndicatorsStyle(@Indicators.Style style: Int): SlideShow {
        Attrs.indicatorStyle = style
        return this
    }

    /**
     * 得到指示器的样式
     */
    @Indicators.Style
    fun getIndicatorsStyle(): Int {
        return Attrs.indicatorStyle
    }

    /**
     * 设置是否显示指示器
     */
    fun setShowIndicators(boolean: Boolean): SlideShow {
        Attrs.isShowIndicators = boolean
        return this
    }

    /**
     * 设置内部 ViewPager2 的 orientation
     * @param orientation 数据来自 [ViewPager2.ORIENTATION_HORIZONTAL]、[ViewPager2.ORIENTATION_VERTICAL]
     */
    fun setOrientation(@ViewPager2.Orientation orientation: Int): SlideShow {
        Attrs.orientation = orientation
        mViewPager2.orientation = orientation
        return this
    }

    /**
     * 得到内部 ViewPager2 的 orientation
     */
    @ViewPager2.Orientation
    fun getOrientation(): Int {
        return Attrs.orientation
    }

    /**
     * 设置 [SlideShow] 的圆角，其实 [SlideShow] 继承于 [CardView]，可以使用 [CardView] 的方法
     */
    fun setOutRadius(radius: Float): SlideShow {
        super.setRadius(radius)
        return this
    }

    /**
     * 监听 ViewPager2 的滑动
     */
    fun setPageChangeCallback(callback: ViewPager2.OnPageChangeCallback): SlideShow {
        mPageChangeCallback.setPageChangeCallBack(callback)
        return this
    }

    /**
     * 设置 ViewPager2 内部页面的边距，Orientation 为水平时设置左右的间隔，垂直时设置上下的间隔
     *
     * **NOTICE：**
     *
     * 1、app:slide_imgWight 为 match_parent 时，设置 [interval]，图片宽度 **会** 改变，
     *   两图片的间距为 app:slide_imgMarginHorizontal 的值的两倍
     *
     * 2、app:slide_imgWight 为 具体值 时，设置 [interval]，图片宽度不会改变，
     *   且设置的 app:slide_imgMarginHorizontal 无效，此时两图片的间距为 [interval]
     *
     * @param interval 内部页面到外部页面的间隔值
     */
    fun setPageInterval(interval: Int): SlideShow {
        if (interval > 0) {
            post {
                val distance= if (Attrs.imgWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                    interval
                }else {
                    /*
                    * 其实两图片的间距为 (初始的ImageView.left - interval) * 2，我使用了 distance 做中间值来转换
                    * */
                    (width - Attrs.imgWidth) / 2 - interval / 2
                }
                val childView = mViewPager2.getChildAt(0) as RecyclerView
                when (getOrientation()) {
                    ViewPager2.ORIENTATION_HORIZONTAL -> {
                        childView.setPadding(distance, 0, distance, 0)
                    }
                    ViewPager2.ORIENTATION_VERTICAL -> {
                        childView.setPadding(0, distance, 0, distance)
                    }
                }
                childView.clipToPadding = false
            }
        }
        return this
    }

    /**
     * 设置 [SlideShow] 刚被加载时的起始页面
     * @see [setCurrentItem]
     */
    fun setStartItem(item: Int): SlideShow {
        post {
            mViewPager2.setCurrentItem(item, false)
        }
        return this
    }

    /**
     * 设置是否允许用户滑动
     */
    fun setUserInputEnabled(enabled: Boolean): SlideShow {
        mViewPager2.isUserInputEnabled = enabled
        return this
    }

    /**
     * 用于开启自动滑动
     *
     * 在 [SlideShow] 刚被加载时可以不用调用该方法，因为已在内部调用
     */
    fun start() {
        if (mIsAutoSlideEnabled) {
            if (!mIsSliding) {
                mIsSliding = true
                postDelayed(mAutoSlideRunnable, mDelayTime)
            }
        }
    }

    /**
     * 用于关闭自动滑动
     *
     * 不用担心内存泄露问题，因为自动滑动使用的是 View#postDelayed
     */
    fun stop() {
        if (mIsAutoSlideEnabled) {
            mIsSliding = false
            removeCallbacks(mAutoSlideRunnable)
        }
    }

    private val mViewPager2: ViewPager2 = ViewPager2(context)
    private val mPageChangeCallback = BasePageChangeCallBack()
    private val mPageTransformers = BaseMultipleTransformer()

    init {
        if (attrs != null) {
            Attrs.initialize(context, attrs)
            setPageInterval(Attrs.pageInterval.toInt())
        }
        init()
    }

    private var mIsAutoSlideEnabled = true
    private var mIsSliding = false
    private var mDelayTime = 4000L
    private val mAutoSlideRunnable = object : Runnable {
        override fun run() {
            postDelayed(this, mDelayTime)
        }
    }

    private fun init() {
        cardElevation = 0F
        initViewPager2()
    }

    private fun initViewPager2() {
        mViewPager2.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mViewPager2.registerOnPageChangeCallback(mPageChangeCallback)
        mViewPager2.setPageTransformer(mPageTransformers)
        addView(mViewPager2)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mViewPager2.isUserInputEnabled) {
            requestDisallowInterceptTouchEvent(true)
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    stop()
                }
                MotionEvent.ACTION_UP -> {
                    start()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    interface OnRefreshListener {
        fun onRefresh(imageView: ShapeableImageView, holder: BaseImgAdapter.BaseImgViewHolder, position: Int)
    }
}