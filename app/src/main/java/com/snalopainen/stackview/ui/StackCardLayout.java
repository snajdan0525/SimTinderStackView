package com.snalopainen.stackview.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;

import com.snalopainen.stackview.bus.RxBus;
import com.snalopainen.stackview.bus.events.StackCardMovedEvent;
import com.snalopainen.stackview.utilites.DisplayUtility;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by snajdan on 2016/12/28.
 */

public class StackCardLayout extends FrameLayout {

    private static int ANIMATION_DURATION = 300;
    private int screenWidth;
    private int yCardoffset;//每一个层叠的stackView位移差
    private CompositeSubscription compositeSubscription;
    private PublishSubject<Integer> publishSubject = PublishSubject.create();
    public StackCardLayout(Context context) {
        super(context);
    }

    public StackCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StackCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init() {
        setClipChildren(false);//不限制子View在父视图范围内。。。估计看到的人也不懂这句话，只可意会不可言传
        screenWidth = DisplayUtility.getScreenWidth(getContext());
        yCardoffset = DisplayUtility.dp2px(getContext(), 8);
        compositeSubscription = new CompositeSubscription();
        setCompositeSubscription();
    }


    private void setCompositeSubscription() {
        Subscription rxSubscription = RxBus.getInstance().toObserverable()
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o == null) {
                            return;
                        }
                        if (o instanceof StackCardMovedEvent) {
                            float posX = ((StackCardMovedEvent) o).getPosX();
                            int childCount = getChildCount();
                            for (int i = childCount - 2; i >= 0; i--) {
                                StackCardView stackCardView = (StackCardView) getChildAt(i);
                                if (Math.abs(posX) == (float) screenWidth) {
                                    float scaleValue = 1 - ((childCount - 2 - i) / 50.0f);
                                    stackCardView.animate()
                                            .x(0)
                                            .y((childCount - 2 - i) * yCardoffset)
                                            .scaleX(scaleValue)
                                            .rotation(0)
                                            .setInterpolator(new AnticipateInterpolator())
                                            .setDuration(300);
                                }

                            }
                        }
                    }
                });
        compositeSubscription.add(rxSubscription);
    }

    public void addCard(StackCardView scv) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int childCount = getChildCount();
        addView(scv, 0, layoutParams);

        float scaleValue = 1 - (childCount / 50.0f);

        scv.animate()
                .x(0)
                .y(childCount * yCardoffset)
                .scaleX(scaleValue)
                .setInterpolator(new AnticipateOvershootInterpolator())
                .setDuration(ANIMATION_DURATION);
    }
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (publishSubject != null)
            publishSubject.onNext(getChildCount());
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        if (publishSubject != null)
            publishSubject.onNext(getChildCount());
    }
}