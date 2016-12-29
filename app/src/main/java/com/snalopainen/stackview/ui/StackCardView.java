package com.snalopainen.stackview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.snalopainen.stackview.R;
import com.snalopainen.stackview.bus.RxBus;
import com.snalopainen.stackview.bus.events.StackCardMovedEvent;
import com.snalopainen.stackview.utilites.DisplayUtility;

/**
 * Created by snajdan on 2016/12/27.
 */

public class StackCardView extends FrameLayout implements View.OnTouchListener {

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


    private float oldX;
    private float oldY;
    private float newX;
    private float newY;
    private float dx;
    private float dy;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = motionEvent.getX();
                oldY = motionEvent.getY();
                view.clearAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                newX = motionEvent.getX();
                newY = motionEvent.getY();
                dx = newX - oldX;
                dy = newY - oldY;

                float posX = view.getX() + dx;
                RxBus.getInstance().send(new StackCardMovedEvent(posX));
                view.setX(view.getX() + dx);
                view.setY(view.getY() + dy);
                setCardRotation(view, view.getX());

                updateBadgesAlpha(posX);
                return true;
            case MotionEvent.ACTION_UP:

                break;
            default:
                return super.onTouchEvent(motionEvent);

        }
        return false;
    }

    private void isBeyondLeftBoundary(View view) {
    }

    private void isBeyondRightBoundary(View view) {
    }

    private static final float CARD_ROTATION_DEGREES = 40.0f;

    private void setCardRotation(View view, float posX) {
        float rotation = (CARD_ROTATION_DEGREES * (posX)) / screenWidth;
        int halfCardHeight = (view.getHeight() / 2);
        if (oldY < halfCardHeight - (2 * padding)) {
            view.setRotation(rotation);
        } else {
            view.setRotation(-rotation);
        }

    }

    private void updateBadgesAlpha(float posX) {
        float alpha = (posX - padding) / (screenWidth * 0.5f);
        likeTextView.setAlpha(alpha);
        nopeTextView.setAlpha(alpha);
    }
}
