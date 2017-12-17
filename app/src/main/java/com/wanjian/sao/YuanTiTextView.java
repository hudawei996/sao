package com.wanjian.sao;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by wanjian on 2017/12/17.
 */

public class YuanTiTextView extends android.support.v7.widget.AppCompatTextView {
    public YuanTiTextView(Context context) {
        this(context, null);
    }

    public YuanTiTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "yuanti.ttf");
        setTypeface(typeFace);
    }
}
