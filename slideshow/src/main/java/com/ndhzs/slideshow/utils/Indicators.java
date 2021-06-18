package com.ndhzs.slideshow.utils;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import java.lang.annotation.Retention;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;
import static com.ndhzs.slideshow.utils.Indicators.Style.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * .....
 *
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/29
 */
public class Indicators {

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(SOURCE)
    @IntDef({
            NO_SHOW,
            SELF_VIEW,
            SELF_VIEW_ELSEWHERE,
            NORMAL,
    })
    public @interface Style {
        int NO_SHOW = -1;
        int SELF_VIEW = 0;
        int SELF_VIEW_ELSEWHERE = 1;
        int NORMAL = 2;
    }

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(SOURCE)
    @IntDef({
            OuterGravity.TOP,
            OuterGravity.BOTTOM,
            OuterGravity.LEFT,
            OuterGravity.RIGHT,
            OuterGravity.VERTICAL_CENTER,
            OuterGravity.HORIZONTAL_CENTER
    })
    public @interface OuterGravity {
        int TOP = 00001;
        int BOTTOM = 00010;
        int LEFT = 00100;
        int RIGHT = 01000;
        int VERTICAL_CENTER = 01100;
        int HORIZONTAL_CENTER = 00011;
    }

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(SOURCE)
    @IntDef({
            InnerGravity.FRONT,
            InnerGravity.BACK,
            InnerGravity.CENTER,
    })
    public @interface InnerGravity {
        int FRONT = 001;
        int BACK = 010;
        int CENTER = 011;
    }
}
