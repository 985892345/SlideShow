package com.ndhzs.slideshow.utils

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/27
 */
class IndicatorsGravity(val gravity: Int) {

    companion object {
        const val TOP = 0x30
        const val BOTTOM = 0x50
        const val LEFT = 0x03
        const val RIGHT = 0x05
        const val CENTER = 0x11
        const val CENTER_VERTICAL = 0x10
        const val CENTER_HORIZONTAL = 0x01
        const val TOP_LEFT = TOP or LEFT
        const val TOP_CENTER = TOP or CENTER_HORIZONTAL
        const val TOP_RIGHT = TOP or RIGHT
        const val BOTTOM_LEFT = BOTTOM or LEFT
        const val BOTTOM_CENTER = BOTTOM or CENTER_HORIZONTAL
        const val BOTTOM_RIGHT = BOTTOM or RIGHT
        const val LEFT_CENTER = LEFT or CENTER_VERTICAL
        const val RIGHT_CENTER = RIGHT or CENTER_VERTICAL

        @JvmStatic
        fun isError(gravity: Int): Boolean {
            return gravity != TOP &&
                    gravity != BOTTOM &&
                    gravity != LEFT &&
                    gravity != RIGHT &&
                    gravity != CENTER &&
                    gravity != CENTER_VERTICAL &&
                    gravity != CENTER_HORIZONTAL &&
                    gravity != TOP_LEFT &&
                    gravity != TOP_CENTER &&
                    gravity != TOP_RIGHT &&
                    gravity != BOTTOM_LEFT &&
                    gravity != BOTTOM_CENTER &&
                    gravity != BOTTOM_RIGHT &&
                    gravity != LEFT_CENTER &&
                    gravity != RIGHT_CENTER
        }
    }
}
