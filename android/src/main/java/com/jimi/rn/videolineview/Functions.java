package com.jimi.rn.videolineview;

import android.content.Context;
import android.util.TypedValue;

/**
 * Description:
 * Author: zengweidie
 * CreateDate: 2021/9/15 15:45
 * UpdateUser: 更新者
 * UpdateDate: 2021/9/15 15:45
 * UpdateRemark: 更新说明
 */

public class Functions {
    /**
     * dip转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */

    public static int sp2px(Context context, float spVal) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,

                spVal, context.getResources().getDisplayMetrics());
    }
}
