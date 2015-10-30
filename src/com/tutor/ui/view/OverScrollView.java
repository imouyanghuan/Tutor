package com.tutor.ui.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ScrollView;

/**
 * 弹性ScrollView
 * 
 * @author bruce.chen
 * 
 *         2015-10-26
 */
public class OverScrollView extends ScrollView {

	private Context context;

	public OverScrollView(Context context) {
		super(context);
		this.context = context;
	}

	public OverScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public OverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	private float pressY;
	private float downDis;
	private float upDis;
	private float pressX;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			pressY = ev.getRawY();
			pressX = ev.getRawX();
		}
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			float nowX = ev.getRawX();
			float nowY = ev.getRawY();
			float disX = Math.abs(nowX - pressX);
			float disY = Math.abs(nowY - pressY);
			if (disY >= ViewConfiguration.get(context).getScaledTouchSlop() && disY > disX) {
				return true;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				pressY = ev.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				int sy = getScrollY();
				if (sy == 0 && ev.getRawY() > pressY) {
					downDis = (float) ((ev.getRawY() - pressY) / 3);
					getChildAt(0).setTranslationY(downDis);
					break;
				}
				if (sy + getHeight() == getChildAt(0).getHeight() && ev.getRawY() < pressY) {
					upDis = (float) ((ev.getRawY() - pressY) / 3);
					getChildAt(0).setTranslationY(upDis);
					break;
				}
				if (getHeight() >= getChildAt(0).getHeight() && ev.getRawY() < pressY) {
					upDis = (float) ((ev.getRawY() - pressY) / 3);
					getChildAt(0).setTranslationY(upDis);
					break;
				}
				pressY = ev.getRawY();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				final float start = getChildAt(0).getTranslationY();
				ObjectAnimator animator = ObjectAnimator.ofFloat(getChildAt(0), "translationY", start, 0);
				animator.setDuration(350);
				animator.setInterpolator(new AccelerateDecelerateInterpolator());
				animator.start();
				break;
		}
		return super.onTouchEvent(ev);
	}
}
