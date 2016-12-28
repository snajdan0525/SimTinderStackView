package com.snalopainen.stackview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.snalopainen.stackview.R;
/**
 * Created by snajdan on 2016/12/28.
 */

public class ConsistentAspectRatioImageView extends ImageView {

    private double heightRatio;

    public ConsistentAspectRatioImageView(Context context) {
        super(context);
        init(context, null);
    }

    public ConsistentAspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ConsistentAspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(attrs, R.styleable.ConsistentAspectRatioImageView);

        try {
            this.heightRatio = attributeArray.getFloat(R.styleable.ConsistentAspectRatioImageView_heightRatio, 1.0F);
        } finally {
            attributeArray.recycle();
        }
    }

    public void setHeightRatio(double ratio) {
        if (ratio != heightRatio) {
            heightRatio = ratio;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (heightRatio > 0.0) {
            // set the image views size
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * heightRatio);
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
