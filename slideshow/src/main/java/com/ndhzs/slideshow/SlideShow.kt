package com.ndhzs.slideshow

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.cardview.widget.CardView
import androidx.core.animation.addListener
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.imageview.ShapeableImageView
import com.ndhzs.slideshow.indicators.AbstractIndicatorsView
import com.ndhzs.slideshow.indicators.utils.Indicators
import com.ndhzs.slideshow.indicators.utils.Refresh
import com.ndhzs.slideshow.indicators.view.MoveIndicators
import com.ndhzs.slideshow.indicators.view.FlashIndicators
import com.ndhzs.slideshow.indicators.view.WaterDropIndicators
import com.ndhzs.slideshow.indicators.view.ZoomIndicators
import com.ndhzs.slideshow.myinterface.IIndicator
import com.ndhzs.slideshow.myinterface.OnImgRefreshListener
import com.ndhzs.slideshow.myinterface.OnRefreshListener
import com.ndhzs.slideshow.utils.*
import com.ndhzs.slideshow.viewpager2.adapter.BaseFragmentStateAdapter
import com.ndhzs.slideshow.viewpager2.adapter.BaseImgAdapter
import com.ndhzs.slideshow.viewpager2.adapter.BaseRecyclerAdapter
import com.ndhzs.slideshow.viewpager2.adapter.BaseViewAdapter
import com.ndhzs.slideshow.viewpager2.pagecallback.BasePageChangeCallBack
import com.ndhzs.slideshow.viewpager2.transformer.BaseMultipleTransformer
import java.lang.ClassCastException
import kotlin.math.abs

/**
 * **NOTE:** 由于继承于 CardView, 如果你想修改背景颜色, 请使用属性 android:backgroundTine=..., 这是 CardView 的原因
 *
 * 该控件参考了第三方库：Banner (https://github.com/youth5201314/banner)，在此表示感谢！
 *
 * 里面内置了 transformer（页面移动动画）的默认实现类，
 * 来自于 (https://github.com/youth5201314/banner)
 *
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/26
 */
class SlideShow : CardView, NestedScrollingParent2 {

    private val mAttrs: SlideShowAttrs
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mAttrs = SlideShowAttrs.Builder().build()
        mAttrs.initialize(context, attrs)
        init()
    }
    constructor(context: Context, attrs: SlideShowAttrs) : super(context) {
        mAttrs = attrs
        attrs.setAttrs()
        init()
    }

    /**
     * 该方法只能设置一个 transformer（页面移动动画）
     *
     * 已内置了几个 transformer，请点击 [SlideShow] 查看注释
     *
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
    fun removeTransformer(transformer: ViewPager2.PageTransformer) {
        mPageTransformers.removeTransformer(transformer)
    }

    /**
     * 该方法用于删除全部 transformer（页面移动动画）
     * @see [removeTransformer]
     */
    fun clearTransformer() {
        mPageTransformers.mTransformers.clear()
    }

    /**
     * 虽然可以得到 ViewPager2，但建议最好不要在 ViewPager2 中设置一些东西
     *
     * **WARNING：** 传入 [setAutoSlideEnabled] 为 true 或调用 [openCirculateEnabled] 后，
     * ViewPager2 的内部 item 位置会发生改变。如果此时你与 Toolbar 等进行联合将会出现位置问题。
     *
     * **NOTICE：** 可以使用 [setPageChangeCallback] 来进行联合，这个回调会返回你想要的 item 位置
     *
     * @see [setPageChangeCallback]
     */
    fun getViewPager2(): ViewPager2 {
        return mViewPager2
    }

    /**
     * 虽然可以设置 ViewPager2，但建议最好不要在 ViewPager2 中设置一些东西
     *
     * **WARNING：** 传入 [setAutoSlideEnabled] 为 true 或调用 [openCirculateEnabled] 后，
     * ViewPager2 的内部 item 位置会发生改变。如果此时你与 Toolbar 等进行联合将会出现位置问题。
     *
     * **NOTICE：** 可以使用 [setPageChangeCallback] 来进行联合，这个回调会返回你想要的 item 位置
     *
     * @see [setPageChangeCallback]
     */
    inline fun setViewPager2(vp: ViewPager2.() -> Unit): SlideShow {
        vp.invoke(getViewPager2())
        return this
    }

    /**
     * 得到正确的 item 位置
     *
     * 如果你开启了循环或者自动滑动, 那么内部 ViewPager2 的 item 数量会发生变化, 该方法就是让你通过当前 item
     * 的位置而得到你想得到的虚假位置
     *
     * **NOTE:** 在 [setPageChangeCallback] 和 refactor 里的 position 是自动处理过的， 所以可以不使用该方法
     */
    fun getRealPosition(nowPosition: Int): Int {
        return BasePageChangeCallBack.getFalsePosition(mIsCirculateEnabled, nowPosition, mImgDataSize)
    }

    /**
     * 得到是否设置了 Adapter
     */
    fun hasBeenSetAdapter(): Boolean {
        return mViewPager2.adapter != null
    }

    fun setOffscreenPageLimit(@ViewPager2.OffscreenPageLimit limit: Int): SlideShow {
        mViewPager2.offscreenPageLimit = limit
        return this
    }

    fun getIsLeft(): Boolean {
        return !getChildAt(0).canScrollHorizontally(-1)
    }

    fun getIsRight(): Boolean {
        return !getChildAt(0).canScrollHorizontally(1)
    }

    fun getIsTop(): Boolean {
        return !getChildAt(0).canScrollVertically(-1)
    }

    fun getIsBottom(): Boolean {
        return !getChildAt(0).canScrollVertically(1)
    }

    /**
     * 用于设置图片加载的 Adapter
     *
     * **NOTE：** 如果你想使一个页面能看到相邻的图片边缘，请设置 app:slide_adjacentPageInterval
     *
     * **NOTE：** 使用该方法可能意为着你需要自动滑动，请使用 [setAutoSlideEnabled]
     *
     * **NOTE：** 使用该方法可能意为着你需要循环滑动，请使用 [openCirculateEnabled]
     */
    fun <T> setImgAdapter(datas: List<T>, imgAdapter: BaseImgAdapter<T>) {
        imgAdapter.initialize(datas, mViewPager2, mAttrs)
        mViewPager2.adapter = imgAdapter
        mImgDataSize = datas.size
        if (mIsCirculateEnabled && mImgDataSize > 1) {
            imgAdapter.openCirculateEnabled()
            mPageChangeCallback.openCirculateEnabled(mImgDataSize)
            if (finalItem == -1) finalItem = 0
            finalItem = BasePageChangeCallBack.getBackPosition(finalItem, imgAdapter.itemCount, mImgDataSize)
            if (mIsAutoSlideEnabled) { // 在延迟加载 adapter 后，ViewPager2 仍然会显示，但 onAttachedToWindow 已经被调用，所以要在这里调用开始
                start()
            }
        }
        afterSetAdapter()
    }

    /**
     * 用于设置图片加载的 Adapter（使用 Lambda 填写）
     *
     * **NOTE：** 如果你想使一个页面能看到相邻的图片边缘，请设置 app:slide_adjacentPageInterval
     *
     * **NOTE：** 使用该方法可能意为着你需要自动滑动，请使用 [setAutoSlideEnabled]
     *
     * **NOTE：** 使用该方法可能意为着你需要循环滑动，请使用 [openCirculateEnabled]
     */
    fun <T> setImgAdapter(
        datas: List<T>,
        create: (
            holder: BaseViewAdapter<ShapeableImageView>.BaseViewHolder
        ) -> Unit,
        refactor:
            (data: T,
             imageView: ShapeableImageView,
             holder: BaseViewAdapter<ShapeableImageView>.BaseViewHolder,
             position: Int
        ) -> Unit
    ) {
        val adapter = object : BaseImgAdapter<T>() {
            override fun refactor(
                data: T,
                imageView: ShapeableImageView,
                holder: BaseViewHolder,
                position: Int
            ) {
                refactor.invoke(data, imageView, holder, position)
            }

            override fun create(holder: BaseViewHolder) {
                create.invoke(holder)
            }
        }
        setImgAdapter(datas, adapter)
    }

    /**
     * 用于设置自己的 View 的 Adapter
     */
    fun <V: View> setViewAdapter(viewAdapter: BaseViewAdapter<V>) {
        throwAdapterLoadError()
        viewAdapter.initializeAttrs(mAttrs)
        mViewPager2.adapter = viewAdapter
        afterSetAdapter()
    }

    /**
     * 用于设置自己的 View 的 Adapter（使用 Lambda 填写）
     *
     * @param getNewView 该回调用于在 onCreateViewHolder 调用时生成新的 view 对象, 你可以在此时对 view 进行一些初始化.
     */
    fun <V: View> setViewAdapter(
        getNewView: (context: Context) -> V,
        getItemCount: () -> Int,
        create: (holder: BaseViewAdapter<V>.BaseViewHolder) -> Unit,
        onBindViewHolder: (
            view: V,
            holder: BaseViewAdapter<V>.BaseViewHolder,
            position: Int,
            payloads: MutableList<Any>
        ) -> Unit
    ) {
        val adapter = object : BaseViewAdapter<V>() {
            override fun getItemCount(): Int {
                return getItemCount.invoke()
            }

            override fun onBindViewHolder(
                view: V,
                holder: BaseViewHolder,
                position: Int,
                payloads: MutableList<Any>
            ) {
                onBindViewHolder.invoke(view, holder, position, payloads)
            }

            override fun getNewView(context: Context): V {
                return getNewView(context)
            }

            override fun create(holder: BaseViewHolder) {
                create.invoke(holder)
            }
        }
        setViewAdapter(adapter)
    }

    /**
     * 用于设置 FragmentStateAdapter
     *
     * 该方法简写了创建 FragmentStateAdapter 的过程，传入数据后会自动帮你设置 FragmentStateAdapter
     */
    fun setFragmentAdapter(fragmentActivity: FragmentActivity, fragments: List<Fragment>) {
        throwAdapterLoadError()
        val adapter = object : BaseFragmentStateAdapter(fragmentActivity, fragments) {}
        mViewPager2.adapter = adapter
        afterSetAdapter()
    }

    /**
     * 用于设置 FragmentStateAdapter
     */
    fun setFragmentAdapter(fragmentAdapter: FragmentStateAdapter) {
        throwAdapterLoadError()
        mViewPager2.adapter = fragmentAdapter
        afterSetAdapter()
    }

    /**
     * 用于设置通用 RecyclerView.Adapter
     *
     * 不推荐使用自己的 adapter，建议使用 BaseRecyclerAdapter，用法一样，但可以调用 [notifyItemRefresh] 方法进行特殊刷新
     */
    @Deprecated("不建议使用自己的 adapter", ReplaceWith("使用 BaseRecyclerAdapter 代替"))
    fun setRecyclerAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
        throwAdapterLoadError()
        mViewPager2.adapter = adapter
        afterSetAdapter()
    }

    /**
     * 用于设置 BaseRecyclerAdapter
     *
     * 使用该 adapter 后可以调用 [notifyItemRefresh] 方法进行特殊刷新
     */
    fun setRecyclerAdapter(adapter: BaseRecyclerAdapter<out RecyclerView.ViewHolder>) {
        throwAdapterLoadError()
        mViewPager2.adapter = adapter
        afterSetAdapter()
    }

    private fun throwAdapterLoadError() {
        if (mIsAutoSlideEnabled) {
            throw RuntimeException(
                "Your ${SlideShowAttrs.Lib_name}#setAdapter()、setAutoSlideEnabled(): " +
                        "The adapter does not support automatic sliding!")
        }
        if (mIsCirculateEnabled) {
            throw RuntimeException(
                "Your ${SlideShowAttrs.Lib_name}#setAdapter()、 openCirculateEnabled(): " +
                        "The adapter does not support circular presentation!")
        }
    }

    private fun afterSetAdapter() {
        if (this::mIndicators.isInitialized) {
            val adapter = mViewPager2.adapter
            if (adapter != null) {
                mIndicators.setAmount(if (adapter !is BaseImgAdapter<*>) adapter.itemCount else mImgDataSize)
            }
        }
    }

    /**
     * 通知位置为 position 的 imageView 刷新
     *
     * **NOTICE：** 该方法支持永久的更新，但实现原理是打上标记，在滑动回来时会重新调用你传入的 [l]，所以会出现重复调用，请注意该点
     *
     * **WARNING：** 不建议进行延时操作
     *
     * **WARNING：** 使用该方法的前提是 [setViewAdapter] 是 [BaseImgAdapter] 的实现类，否则将报错
     */
    fun notifyImageViewRefresh(position: Int, @Refresh.Condition condition: Int, l: OnImgRefreshListener) {
        val adapter = mViewPager2.adapter
        if (adapter is BaseImgAdapter<*>) {
            adapter.setImgRefreshListener(position, condition, l)
        }else {
            throw RuntimeException(
                    "Your ${SlideShowAttrs.Lib_name}#notifyImageViewRefresh(): " +
                            "The adapter is not BaseImgAdapter, so you can't use function of notifyImageViewRefresh!")
        }
    }

    /**
     * 通知位置为 position 的 holder 刷新
     *
     * **NOTICE：** 该方法支持永久的更新，但实现原理是打上标记，在滑动回来时会重新调用你传入的 [l]，所以会出现重复调用，请注意该点
     *
     * **WARNING：** 不建议进行延时操作
     *
     * **WARNING：** 使用该方法的前提是 [setViewAdapter] 是 [BaseRecyclerAdapter] 的实现类，否则将报错
     */
    fun notifyItemRefresh(position: Int, @Refresh.Condition condition: Int, l: OnRefreshListener) {
        val adapter = mViewPager2.adapter
        if (adapter is BaseRecyclerAdapter) {
            adapter.setRefreshListener(position, condition, l)
        }else {
            throw RuntimeException(
                    "Your ${SlideShowAttrs.Lib_name}#notifyRefresh(): " +
                            "The adapter is not BaseRecyclerAdapter, so you can't use function of notifyRefresh!")
        }
    }

    /**
     * 用于刷新全部
     *
     * **NOTE：** 如果使用的 [BaseImgAdapter]，可以在**修改了外部数据**的情况下调用该方法进行刷新,
     * 但推荐使用 [notifyImgDataChange] 方法
     */
    fun notifyDataSetChanged() {
        val adapter = mViewPager2.adapter
        if (adapter is BaseImgAdapter<*>) {
            adapter.myNotifyDataSetChanged()
        }else {
            adapter?.notifyDataSetChanged()
        }
    }

    /**
     * 用于给设置了 [BaseImgAdapter] 的情况下传入新数据刷新
     *
     * (由于泛型擦除原因, 无法检查你的泛型是否与 Adapter 中数据的泛型一致)
     *
     * **WARNING：** 使用该方法的前提是 [setImgAdapter] 是 [BaseImgAdapter] 的实现类，否则将报错
     * @exception ClassCastException 传入的泛型类型不匹配
     */
    fun <T> notifyImgDataChange(data: List<T>) {
        val adapter = mViewPager2.adapter
        if (adapter is BaseImgAdapter<*>) {
            try {
                adapter.refreshData(data)
            }catch (e: ClassCastException) {
                throw ClassCastException("${SlideShowAttrs.Lib_name}#notifyImgDataChange(): " +
                        "请检查你传入的泛型是否是刚调用 setImgAdapter() 中传入的泛型\n具体原因 -->> ${e.message}")
            }
        }else {
            throw RuntimeException(
                "Your ${SlideShowAttrs.Lib_name}#notifyImgDataChange(): " +
                        "The adapter is not BaseImgAdapter, so you can't use function of notifyImgDataChange!")
        }
    }

    /**
     * 用于清除之前设置的刷新
     *
     * 只会在 [Refresh.Condition.COEXIST]、[Refresh.Condition.COVERED] 时调用才有用
     */
    fun removeImgRefreshListener(position: Int) {
        val adapter = mViewPager2.adapter
        if (adapter is BaseImgAdapter<*>) {
            adapter.removeImgRefreshListener(position)
        }else if (adapter is BaseRecyclerAdapter) {
            adapter.removeRefreshListener(position)
        }
    }

    /**
     * 用于清除之前设置的刷新
     *
     * 只会在 [Refresh.Condition.COEXIST]、[Refresh.Condition.COVERED] 时调用才有用
     */
    fun clearRefreshListener() {
        val adapter = mViewPager2.adapter
        if (adapter is BaseImgAdapter<*>) {
            adapter.clearImgRefreshListener()
        }else if (adapter is BaseRecyclerAdapter) {
            adapter.clearRefreshListener()
        }
    }

    /**
     * 设置是否自动滑动
     *
     * **WARNING：** 传入 true 后，为了能够循环，将会使 ViewPager2 的 item 位置发生变化，该变化即使在之后传入 false 也不能取消
     */
    fun setAutoSlideEnabled(enabled: Boolean): SlideShow {
        if (enabled) {
            openCirculateEnabled()
        }else {
            stop()
        }
        mIsAutoSlideEnabled = enabled
        start() // 里面会判断
        return this
    }

    fun getAutoSlideEnabled(): Boolean {
        return mIsAutoSlideEnabled
    }

    /**
     * 开启循环轮播图
     *
     * 开启自动滑动后该方法自动会被调用
     *
     * 调用该方法将会使 ViewPager2 的 item 位置发生变化，该变化不能取消
     *
     * **WARNING：** 只有在未加载视图时设置才有效
     *
     * @see [setAutoSlideEnabled]
     */
    fun openCirculateEnabled(): SlideShow {
        if (mIsCirculateEnabled) {
            return this
        }
        mIsCirculateEnabled = true
        return this
    }

    /**
     * 得到是否开启循环
     */
    fun getCirculateEnabled(): Boolean {
        return mIsCirculateEnabled
    }

    /**
     * 设置自动滑动的延迟时间，默认时间是 4 秒，最低只能设置成 2 秒
     */
    fun setDelayTime(delayTime: Long): SlideShow {
        mDelayTime = delayTime
        if (delayTime < 2000) {
            mDelayTime = 2000
        }
        return this
    }

    /**
     * 设置自动滑动动画的时间，默认时间是 1 秒
     */
    fun setAutoSlideTime(slideTime: Long): SlideShow {
        mAutoSlideTime = slideTime
        return this
    }

    /**
     * 设置自动滑动动画的插值器
     */
    fun setTimeInterpolator(interpolator: TimeInterpolator): SlideShow {
        mInterpolator = interpolator
        return this
    }

    /**
     * 设置自己的指示器
     */
    fun setYourIndicators(yourIndicators: IIndicator): SlideShow {
        if (mAttrs.mIndicatorsAttrs.indicatorStyle != Indicators.Style.SELF_VIEW) {
            throw RuntimeException(
                "Your ${SlideShowAttrs.Lib_name}#setYourIndicators(): " +
                        "You must set the style to \"self_view\" or \"self_view_elsewhere\" before using your own indicators!")
        }
        if (isAttachedToWindow) {
            throw RuntimeException(
                "Your ${SlideShowAttrs.Lib_name}#setYourIndicators(): " +
                        "You cannot set the indicators after the SlideShow has been attached to window!")
        }
        if (this::mIndicators.isInitialized) {
            throw RuntimeException(
                "Your ${SlideShowAttrs.Lib_name}#setYourIndicators(): " +
                        "You have set the indicators!")
        }
        mIndicators = yourIndicators
        loadIndicators()
        return this
    }

    fun setYourIndicators(yourIndicators: AbstractIndicatorsView): SlideShow {
        if (mAttrs.mIndicatorsAttrs.indicatorStyle != Indicators.Style.EXTEND_ABSTRACT_INDICATORS) {
            throw RuntimeException(
                "Your ${SlideShowAttrs.Lib_name}#setYourIndicators(): " +
                        "You must set the style to \"extend_abstract_indicators\" before using your own indicators!")
        }
        if (isAttachedToWindow) {
            throw RuntimeException(
                "Your ${SlideShowAttrs.Lib_name}#setYourIndicators(): " +
                        "You cannot set the indicators after the SlideShow has been attached to window!")
        }
        if (this::mIndicators.isInitialized) {
            throw RuntimeException(
                "Your ${SlideShowAttrs.Lib_name}#setYourIndicators(): " +
                        "You have set the indicators!")
        }
        yourIndicators.setIndicatorsAttrs(mAttrs.mIndicatorsAttrs)
        mIndicators = yourIndicators
        loadIndicators()
        return this
    }

    /**
     * 得到指示器外部的位置（整个横幅）
     */
    @Indicators.OuterGravity
    fun getIndicatorsOuterGravity(): Int {
        return mAttrs.mIndicatorsAttrs.indicatorOuterGravity
    }

    /**
     * 得到指示器内部的位置（小圆点）
     */
    @Indicators.InnerGravity
    fun getIndicatorsInnerGravity(): Int {
        return mAttrs.mIndicatorsAttrs.indicatorInnerGravity
    }

    /**
     * 得到指示器的样式
     */
    @Indicators.Style
    fun getIndicatorsStyle(): Int {
        return mAttrs.mIndicatorsAttrs.indicatorStyle
    }

    /**
     * 改变指示器的圆点数量
     */
    fun changeIndicatorsAmount(amount: Int) {
        mIndicators.changeAmount(amount)
    }

    /**
     * 设置是否显示指示器
     */
    fun setShowIndicators(boolean: Boolean): SlideShow {
        mAttrs.mIndicatorsAttrs.isShowIndicators = boolean
        if (this::mIndicators.isInitialized) {
            mIndicators.setShowIndicators(boolean)
        }
        return this
    }

    /**
     * 得到内部 ViewPager2 的 orientation
     */
    @ViewPager2.Orientation
    fun getOrientation(): Int {
        return mAttrs.orientation
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
     *
     * **NOTICE：** 在传入 [setAutoSlideEnabled] 为 true 或调用了 [openCirculateEnabled] 的情况下，
     * ViewPager2 的内部 item 位置会发生改变。
     *
     * **WARNING：** 请使用该方法与 Toolbar 等进行联合
     */
    fun setPageChangeCallback(callback: ViewPager2.OnPageChangeCallback): SlideShow {
        mPageChangeCallback.setPageChangeCallBack(callback)
        return this
    }

    fun removePageChangeCallback() {
        mPageChangeCallback.removePageChangeCallback()
    }

    /**
     * 设置 [SlideShow] 刚被加载时的起始页面
     * @see [setCurrentItem]
     */
    fun setStartItem(item: Int): SlideShow {
        finalItem = item
        return this
    }
    private var finalItem = -1

    /**
     * 与 ViewPager2#setCurrentItem 相同，默认有动画
     *
     * **WARNING：** 只有在设置 adapter 后才有效
     *
     * @see [setStartItem]
     */
    fun setCurrentItem(item: Int, smoothScroll: Boolean = true): SlideShow {
        if (mViewPager2.isFakeDragging) {
            mViewPager2.endFakeDrag()
        }
        mViewPager2.setCurrentItem(item, smoothScroll)
        return this
    }

    /**
     * 进行可控制时间的滑动
     */
    fun slowlySlide(startPosition: Int, endPosition: Int, duration: Long, interpolator: TimeInterpolator = LinearInterpolator()) {
        if (mViewPager2.isAttachedToWindow) {
            checkIsInItemCount(startPosition, "startPosition")
            checkIsInItemCount(endPosition, "endPosition")
            fakeDrag(endPosition - startPosition, duration, interpolator)
        }else {
            mRunnableManger.post{
                checkIsInItemCount(startPosition, "startPosition")
                checkIsInItemCount(endPosition, "endPosition")
                fakeDrag(endPosition - startPosition, duration, interpolator)
            }
        }
    }

    /**
     * 设置是否允许用户滑动
     *
     * 设置了 false 后你将会在 SlideShow 的父 View 的 onTouchEvent 收到事件
     */
    fun setUserInputEnabled(userInputEnabled: Boolean): SlideShow {
        mViewPager2.isUserInputEnabled = userInputEnabled
        mIsUserInputEnabled = userInputEnabled
        return this
    }

    fun getUserInputEnabled(): Boolean {
        return mIsUserInputEnabled
    }

    /**
     * 设置是否开启同方向的嵌套滑动处理
     */
    fun setOpenNestedScroll(isOpenNestedScroll: Boolean): SlideShow {
        mIsOpenNestedScroll = isOpenNestedScroll
        return this
    }

    /**
     * 用于开启自动滑动
     *
     * **NOTE：** 使用时可以不用手动调用，在视图被添加到窗口时会自动滑动
     */
    fun start() {
        if (mIsAutoSlideEnabled) {
            if (!mIsSliding && mImgDataSize != 1) {
                mIsSliding = true
                mRunnableManger.postDelayed(mDelayTime, mAutoSlideRunnable)
                if (!this::mInterpolator.isInitialized) {
                    mInterpolator = LinearInterpolator()
                }
            }
        }
    }

    /**
     * 用于关闭自动滑动
     */
    fun stop() {
        if (mIsAutoSlideEnabled) {
            mIsSliding = false
            mRunnableManger.remove(mAutoSlideRunnable)
            if (mFakeDragAnimator != null) {
                mFakeDragAnimator!!.cancel()
            }
        }
    }

    private val mParentHelper by lazy {
        NestedScrollingParentHelper(this)
    }
    private val mRunnableManger = RunnableManger(this)
    private val mViewPager2 = ViewPager2(context)
    private var mIsUserInputEnabled = true
    private val mPageChangeCallback = BasePageChangeCallBack(mViewPager2)
    private val mPageTransformers by lazy {
        val transformer = BaseMultipleTransformer()
        mViewPager2.setPageTransformer(transformer)
        transformer
    }
    private var mImgDataSize = 1
    private var mIsAutoSlideEnabled = false
    private var mIsSliding = false
    private var mDelayTime = 4000L
    private var mAutoSlideTime = 1000L
    private lateinit var mInterpolator: TimeInterpolator
    private val mAutoSlideRunnable by lazy {
        object : Runnable {
            override fun run() {
                fakeDrag(1, mAutoSlideTime, mInterpolator)
                postDelayed(this, mDelayTime)
            }
        }
    }

    private fun init() {
        cardElevation = 0F
        initViewPager2()
        initIndicators()
        setCardBackgroundColor(0x00000000)
    }

    private fun initViewPager2() {
        val lp = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
        mViewPager2.registerOnPageChangeCallback(mPageChangeCallback)
        mViewPager2.orientation = mAttrs.orientation
        mViewPager2.setBackgroundColor(0x00000000)
        mViewPager2.getChildAt(0).setBackgroundColor(0x00000000)
        attachViewToParent(mViewPager2, -1, lp)
        mRunnableManger.post {
            if (finalItem == -1) finalItem = 0
            setCurrentItem(finalItem, false)
        }
    }

    private lateinit var mIndicators: IIndicator

    /**
     * 初始化指示器
     */
    private fun initIndicators() {
        val style = mAttrs.mIndicatorsAttrs.indicatorStyle
        if (style != Indicators.Style.NO_SHOW &&
            style != Indicators.Style.SELF_VIEW &&
            style != Indicators.Style.EXTEND_ABSTRACT_INDICATORS) {
            val indicators = when (style) {
                Indicators.Style.MOVE -> MoveIndicators(context)
                Indicators.Style.ZOOM -> ZoomIndicators(context)
                Indicators.Style.WATER_DROP -> WaterDropIndicators(context)
                Indicators.Style.FLASH -> FlashIndicators(context)
                else -> WaterDropIndicators(context)
            }
            indicators.setIndicatorsAttrs(mAttrs.mIndicatorsAttrs)
            mIndicators = indicators
            loadIndicators()
        }
    }

    private fun loadIndicators() {
        val view = mIndicators.getIndicatorView()
        val lp = mIndicators.setIndicatorsOuterGravity(mAttrs.mIndicatorsAttrs.indicatorOuterGravity)
        attachViewToParent(view, -1, lp)
        mIndicators.setIndicatorsInnerGravity(mAttrs.mIndicatorsAttrs.indicatorInnerGravity)
        mIndicators.setIndicatorsBackgroundColor(mAttrs.mIndicatorsAttrs.indicatorBackgroundColor)
        mIndicators.setShowIndicators(mAttrs.mIndicatorsAttrs.isShowIndicators)
        val adapter = mViewPager2.adapter
        if (adapter != null) {
            mIndicators.setAmount(adapter.itemCount)
        }

        mPageChangeCallback.setIndicators(mIndicators)
    }

    /**
     * 设置 ViewPager2 内部页面的边距，Orientation 为水平时设置左右的间隔，垂直时设置上下的间隔
     *
     * **NOTE：**
     *
     * 1、slide_viewWight 为 match_parent 时，设置 pageInterval，图片宽度 **会** 改变，
     *   两图片的间距 adjacentPageInterval 为 slide_viewMarginHorizontal 的值的两倍，
     *   显示的图片到外部页面的间距 outPageInterval 为 slide_viewMarginHorizontal + pageInterval
     *
     * 2、slide_viewWight 为 具体值 时，设置 pageInterval，图片宽度不会改变，且设置的
     *   slide_imgMarginHorizontal 无效，此时两图片的间距 adjacentPageInterval 为 (初始的 View.left - pageInterval) / 2，
     *   初始的 View.left = (width - mAttrs.viewWidth) / 2
     *
     * 3、pageInterval 和 distance 值用于转换
     *
     * ---------------------------------------------------------------------------------
     * |      ViewPager2
     * |                      ----------------------------------------------------------
     * | <---- distance ----> |    RecyclerView 的 ViewGroup
     * |                      |                               --------------------------
     * |                      | <--- imgMarginHorizontal ---> |        你的图片在这区域
     * |                      |                               |
     * |                      |                               |
     * |                                                      |
     * | <------------- outPageInterval --------------------> |
     * |                      |                               |
     * |                      |                               -------------------------
     * |                      ---------------------------------------------------------
     * --------------------------------------------------------------------------------
     */
    private fun setPageInterval(width: Int, height: Int) {
        if (mViewPager2.adapter is BaseImgAdapter<*>) {
            val childView = mViewPager2.getChildAt(0) as RecyclerView
            when (getOrientation()) {
                ViewPager2.ORIENTATION_HORIZONTAL -> {
                    val distance = if (mAttrs.viewWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                        mAttrs.pageInterval
                    } else {
                        (width - mAttrs.viewWidth) / 2 - mAttrs.pageInterval / 2
                    }
                    childView.setPadding(distance, 0, distance, 0)
                }
                else -> {
                    val distance = if (mAttrs.viewHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                        mAttrs.pageInterval
                    } else {
                        (height - mAttrs.viewHeight) / 2 - mAttrs.pageInterval / 2
                    }
                    childView.setPadding(0, distance, 0, distance)
                }
            }
            childView.clipToPadding = false
        }
    }

    private fun checkIsInItemCount(position: Int, positionMessage: String = "position") {
        var isError = false
        val adapter = mViewPager2.adapter
        if (adapter != null) {
            val itemCount = adapter.itemCount
            if (mIsCirculateEnabled) {
                if (position !in 0 until mImgDataSize) {
                    isError = true
                }
            } else {
                if (position !in 0 until itemCount) {
                    isError = true
                }
            }
            if (isError) {
                throw IndexOutOfBoundsException(
                    "Your ${SlideShowAttrs.Lib_name}#slowlySlide(): " +
                            "The $positionMessage is < 0 or >= ViewPager2#itemCount")
            }
        }
    }

    private var mPreFakeDrag = 0F
    private var mFakeDragAnimator:  ValueAnimator? = null
    private fun fakeDrag(diffPosition: Int, duration: Long, interpolator: TimeInterpolator = LinearInterpolator()) {
        mPreFakeDrag = 0F
        val childView = mViewPager2.getChildAt(0)
        val pixelDistance = if (getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
            mViewPager2.width - childView.paddingLeft - childView.paddingRight
        }else {
            mViewPager2.height - childView.paddingTop - childView.paddingBottom
        }
        mFakeDragAnimator = ValueAnimator.ofFloat(0F, -diffPosition.toFloat())
        mFakeDragAnimator!!.addUpdateListener {
            val nowFakeDrag = it.animatedValue as Float
            val differentOffsetPixel = (nowFakeDrag - mPreFakeDrag) * pixelDistance
            mViewPager2.fakeDragBy(differentOffsetPixel)
            mPreFakeDrag = nowFakeDrag
        }
        mFakeDragAnimator!!.addListener(
                onStart = {
                    mViewPager2.beginFakeDrag()
                    mViewPager2.isUserInputEnabled = false
                },
                onEnd = {
                    mViewPager2.endFakeDrag()
                    mViewPager2.isUserInputEnabled = true
                    mFakeDragAnimator = null
                },
                onCancel = {
                    mViewPager2.endFakeDrag()
                    mViewPager2.isUserInputEnabled = true
                    mFakeDragAnimator = null
                }
        )
        mFakeDragAnimator!!.interpolator = interpolator
        mFakeDragAnimator!!.duration = duration
        mFakeDragAnimator!!.start()
    }

    private var mInitialX = 0
    private var mInitialY = 0
    private var mIsCirculateEnabled = false
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mIsUserInputEnabled) {
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (mViewPager2.adapter is BaseImgAdapter<*>) {
                when (ev.action) {
                    MotionEvent.ACTION_DOWN -> {
                        stop()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        start()
                    }
                }
            }
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    mInitialX = x
                    mInitialY = y
                }
                MotionEvent.ACTION_MOVE -> {
                    if (getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                        if (abs(y - mInitialY) < abs(x - mInitialX)) {
                            requestDisallowInterceptTouchEvent(true)
                        }
                    }else {
                        if (abs(y - mInitialY) > abs(x - mInitialX)) {
                            requestDisallowInterceptTouchEvent(true)
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private var mWidth = 0
    private var mHeight = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val newWidthMS = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> widthMeasureSpec
            else -> MeasureSpec.makeMeasureSpec(
                if (width != 0) width else 900,
                MeasureSpec.EXACTLY
            )
        }
        val newHeightMS = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> heightMeasureSpec
            else -> MeasureSpec.makeMeasureSpec(
                if (height != 0) height else 600,
                MeasureSpec.EXACTLY
            )
        }
        if (mWidth != width || mHeight != height) {
            mWidth = width
            mHeight = height
            measureOver(width, height)
        }
        super.onMeasure(newWidthMS, newHeightMS)
    }

    private fun measureOver(width: Int, height: Int) {
        setPageInterval(width, height)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
        mRunnableManger.destroy()
    }

    private var mIsParentOpenNestedScroll = false
    private var mIsOpenNestedScroll = false
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        if (!mIsOpenNestedScroll)
            return false
        if (target == mViewPager2.getChildAt(0))
            return false
        if (type == ViewCompat.TYPE_TOUCH) {
            if (getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL && axes == ViewCompat.SCROLL_AXIS_HORIZONTAL
                || getOrientation() == ViewPager2.ORIENTATION_VERTICAL && axes == ViewCompat.SCROLL_AXIS_VERTICAL
            ) {
                // 以下代码用于三层嵌套 SlideShow 时，底层 SlideShow（ss1） 只能唤起上一层的 SlideShow（ss2），
                // 而存在这种情况：ss2 已经滑到底了，ss1 需要使 ss3 滑动，在这种情况下就需要手动开启
                mIsParentOpenNestedScroll = mViewPager2.getChildAt(0).startNestedScroll(axes)
                return true
            }
        }
        return false
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
        mViewPager2.isUserInputEnabled = false
        mViewPager2.beginFakeDrag()
    }

    /**
     * 官方是在 onInterceptTouchEvent 中 Down 时调用的 onStartNestedScroll，
     * 但是经测试 ViewPager2 在滑动时调用了 requestDisallowInterceptTouchEvent
     * 用来禁止父 View 拦截滑动，于是就会出现父 View 在调用了 onStartNestedScroll后，却因为
     * onInterceptTouchEvent 被取消而无法在 Up 中调用 onStopNestedScroll
     * 去取消嵌套滑动。
     *
     * 所以在三层及以上的 SlideShow 嵌套，而顶部两层需要开启嵌套滑动时，需要手动调用
     * mViewPager2.getChildAt(0).stopNestedScroll() 去关闭被 requestDisallowInterceptTouchEvent
     * 的父 View 的 NestedScroll。
     *
     * 比如：从外到内共三层，分别为 s1、s2、s3，  s1 和 s2 需要开启嵌套滑动，我在滑动 s3 时，s3 的
     * onInterceptTouchEvent 的 Down 事件就开启了 s1 和 s2 的 onStartNestedScroll（s1 是由 s2
     * 的 Down 开启的，s2 是由 s3 的 Down 开启的），然后 s1 和 s2 的 onInterceptTouchEvent 就被
     * s3 取消，在 s3 的 onInterceptTouchEvent 调用 Up 时，只有 s2 的 onStopNestedScroll 被调用，
     * s1 的 onStopNestedScroll 必须由 s2 才能调用，但 s2 的 onInterceptTouchEvent 被取消，所以
     * s1 的 onStopNestedScroll 不会被调用，于是就需要手动取消
     */
    override fun onStopNestedScroll(target: View, type: Int) {
        mParentHelper.onStopNestedScroll(target, type)
        mViewPager2.endFakeDrag()
        if (mIsParentOpenNestedScroll) {
            mViewPager2.getChildAt(0).stopNestedScroll()
        }
        mViewPager2.isUserInputEnabled = true
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        if (type == ViewCompat.TYPE_TOUCH) {
            if (getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                mViewPager2.fakeDragBy(-dxUnconsumed.toFloat())
            }else {
                mViewPager2.fakeDragBy(-dyUnconsumed.toFloat())
            }
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    }
}