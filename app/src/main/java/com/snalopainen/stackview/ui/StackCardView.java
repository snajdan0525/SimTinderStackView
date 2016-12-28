package com.snalopainen.stackview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.snalopainen.stackview.R;
import com.snalopainen.stackview.utilites.DisplayUtility;

/**
 * Created by snajdan on 2016/12/27.
 */

public class StackCardView extends FrameLayout implements View.OnTouchListener{

    private static final float CARD_ROTATION_DEGREES = 40.0f;
    private static final float BADGE_ROTATION_DEGREES = 15.0f;
    private static final int DURATION = 300;


    private ImageView imageView;
    private TextView displayNameTextView;
    private TextView usernameTextView;
    private TextView likeTextView;
    private TextView nopeTextView;


    private float leftBoundary;
    private float rightBoundary;
    private int screenWidth;
    private int padding;

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

        imageView = (ImageView) findViewById(R.id.iv);
        displayNameTextView = (TextView) findViewById(R.id.display_name_tv);
        usernameTextView = (TextView) findViewById(R.id.username_tv);
        likeTextView = (TextView) findViewById(R.id.like_tv);
        nopeTextView = (TextView) findViewById(R.id.nope_tv);


        likeTextView.setRotation(-(BADGE_ROTATION_DEGREES));
        nopeTextView.setRotation(BADGE_ROTATION_DEGREES);


        screenWidth = DisplayUtility.getScreenWidth(context);
        leftBoundary = screenWidth * (1.0f / 6.0f);
        rightBoundary = screenWidth * (5.0f / 6.0f);
        padding = DisplayUtility.dp2px(context, 16);

        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
