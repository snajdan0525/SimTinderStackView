package com.snalopainen.stackview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by snajdan on 2016/12/27.
 */

public class StackCardView extends FrameLayout {
    public StackCardView(Context context) {
        super(context);
        init(context, null);
    }

    public StackCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StackCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

    }
}
