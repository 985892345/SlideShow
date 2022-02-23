package com.ndhzs.slideshow2.base

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.StyleableRes

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/22 10:51
 */
internal interface BaseViewAttrs {

    companion object {
        inline fun <T> newAttrs(
            context: Context,
            attrs: AttributeSet,
            @StyleableRes
            styleableId: IntArray,
            defStyleAttr: Int,
            defStyleRes: Int,
            func: Typedef.() -> T
        ): T {
            val ty = context.obtainStyledAttributes(attrs, styleableId, defStyleAttr, defStyleRes)
            try {
                return Typedef(ty).func()
            } finally {
                ty.recycle()
            }
        }
    }

    class Typedef(val ty: TypedArray) {
        fun Int.int(defValue: Int): Int = this.int(ty, defValue)
        fun Int.color(defValue: Int): Int = this.color(ty, defValue)
        fun Int.dimens(defValue: Int): Int = this.dimens(ty, defValue)
        fun Int.dimens(defValue: Float): Float = this.dimens(ty, defValue)
        fun Int.string(defValue: String? = null): String = this.string(ty, defValue)
        fun Int.boolean(defValue: Boolean): Boolean = this.boolean(ty, defValue)
        fun Int.float(defValue: Float): Float = this.float(ty, defValue)
        inline fun <reified E: RuntimeException> Int.intOrThrow(
            attrsName: String): Int = this.intOrThrow<E>(ty, attrsName)
        inline fun <reified E: RuntimeException> Int.stringOrThrow(
            attrsName: String): String = this.stringOrThrow<E>(ty, attrsName)
    }
}

internal inline fun <T> Context.getAttrs(
    attrs: AttributeSet,
    @StyleableRes
    styleableId: IntArray,
    defStyleAttr: Int,
    defStyleRes: Int,
    func: BaseViewAttrs.Typedef.() -> T
) = BaseViewAttrs.newAttrs(this, attrs, styleableId, defStyleAttr, defStyleRes, func)

internal fun Int.int(ty: TypedArray, defValue: Int): Int {
    return ty.getInt(this, defValue)
}

internal fun Int.color(ty: TypedArray, defValue: Int): Int {
    return ty.getColor(this, defValue)
}

internal fun Int.dimens(ty: TypedArray, defValue: Int): Int {
    return ty.getDimensionPixelSize(this, defValue)
}

internal fun Int.dimens(ty: TypedArray, defValue: Float): Float {
    return ty.getDimension(this, defValue)
}

internal fun Int.string(ty: TypedArray, defValue: String? = null): String {
    return ty.getString(this) ?: defValue ?: ""
}

internal fun Int.boolean(ty: TypedArray, defValue: Boolean): Boolean {
    return ty.getBoolean(this, defValue)
}

internal fun Int.float(ty: TypedArray, defValue: Float): Float {
    return ty.getFloat(this, defValue)
}

internal inline fun <reified E: RuntimeException> Int.intOrThrow(
    ty: TypedArray, attrsName: String
): Int {
    if (!ty.hasValue(this)) {
        throw E::class.java.getConstructor(String::class.java)
            .newInstance("属性 $attrsName 没有被定义！")
    }
    return this.int(ty, 0)
}

internal inline fun <reified E: java.lang.RuntimeException> Int.stringOrThrow(
    ty: TypedArray, attrsName: String
): String {
    if (!ty.hasValue(this)) {
        throw E::class.java.getConstructor(String::class.java)
            .newInstance("属性 $attrsName 没有被定义！")
    }
    return this.string(ty)
}