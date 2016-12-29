package com.snalopainen.stackview.ui;

import android.animation.Animator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.snalopainen.stackview.R;
import com.snalopainen.stackview.bus.RxBus;
import com.snalopainen.stackview.bus.events.StackCardMovedEvent;
import com.snalopainen.stackview.models.User;
import com.snalopainen.stackview.utilites.DisplayUtility;
import com.squareup.picasso.Picasso;

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
        View view = inflate(context, R.layout.stack_cardview, this);
        imageView = (ImageView) view.findViewById(R.id.iv);
        displayNameTextView = (TextView) view.findViewById(R.id.display_name_tv);
        usernameTextView = (TextView) view.findViewById(R.id.username_tv);
        likeTextView = (TextView) view.findViewById(R.id.like_tv);
        nopeTextView = (TextView) view.findViewById(R.id.nope_tv);


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

                return true;
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
                if (isBeyondLeftBoundary(view)) {
                    //dismiss掉最顶层的card
                    dismissStackCard(view, -screenWidth / 2);
                } else if (isBeyondRightBoundary(view)) {
                    //dismiss掉最顶层的card
                    dismissStackCard(view, screenWidth / 2);
                } else {
                    //复原card的位置
                    resetStackCardPos(view);
                }
                return true;
            default:
                return super.onTouchEvent(motionEvent);
        }
    }

    private void resetStackCardPos(View view) {
        view.animate()
                .x(0)
                .y(0)
                .setDuration(0)
                .setInterpolator(new OvershootInterpolator())
                .rotation(0);
        likeTextView.setAlpha(0);
        nopeTextView.setAlpha(0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setOnTouchListener(null);
    }

    private void dismissStackCard(final View view, float posX) {
        view.animate().x(posX)
                .y(0)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(DURATION)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ViewGroup viewGroup = (ViewGroup) view.getParent();
                        if (viewGroup != null) {
                            viewGroup.removeView(view);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }

    private boolean isBeyondLeftBoundary(View view) {
        return (view.getX() + view.getWidth() / 2) < leftBoundary;
    }

    private boolean isBeyondRightBoundary(View view) {
        return (view.getX() + view.getWidth() / 2) > rightBoundary;
    }


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

    public void bind(User user) {
        if (user == null)
            return;

        setUpImage(imageView, user);
        setUpDisplayName(displayNameTextView, user);
        setUpUsername(usernameTextView, user);
    }

    private void setUpImage(ImageView iv, User user) {
        String avatarUrl = user.getAvatarUrl();
        if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(iv.getContext())
                    .load(avatarUrl)
                    .into(iv);
        }
    }

    private void setUpDisplayName(TextView tv, User user) {
        String displayName = user.getDisplayName();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }

    private void setUpUsername(TextView tv, User user) {
        String username = user.getUsername();
        if (!TextUtils.isEmpty(username)) {
            tv.setText(username);
        }
    }
}
