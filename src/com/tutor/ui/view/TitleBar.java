package com.tutor.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.ui.activity.BaseActivity;
import com.tutor.util.ViewHelper;

/**
 * 自定義標題欄
 * 
 * @author bruce.chen
 * 
 */
public class TitleBar extends FrameLayout {

	private ImageButton left, right;
	private TextView title;
	private TextView lefTextView, rightTextView;

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		left = ViewHelper.get(this, R.id.title_bar_ib_left);
		lefTextView = ViewHelper.get(this, R.id.title_bar_tv_left);
		right = ViewHelper.get(this, R.id.title_bar_ib_right);
		rightTextView = ViewHelper.get(this, R.id.title_bar_tv_right);
		title = ViewHelper.get(this, R.id.title_bar_tv_title);
		try {
			left.setVisibility(View.INVISIBLE);
			right.setVisibility(View.INVISIBLE);
			lefTextView.setVisibility(View.INVISIBLE);
			rightTextView.setVisibility(View.INVISIBLE);
		} catch (Exception e) {}
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void setTitle(int title) {
		this.title.setText(title);
	}

	public void setTitleTextSize(float sp) {
		this.title.setTextSize(sp);
	}

	public void initBack(final BaseActivity activity) {
		if (activity == null) {
			return;
		}
		left.setVisibility(View.VISIBLE);
		left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				activity.finish();
			}
		});
	}

	public void setLeftButtonSrc(int resId) {
		if (left != null) {
			left.setImageResource(resId);
		}
	}

	public void setLeftButton(int resId, OnClickListener clickListener) {
		if (null != lefTextView) {
			lefTextView.setVisibility(View.INVISIBLE);
		}
		if (left != null) {
			left.setVisibility(View.VISIBLE);
			left.setImageResource(resId);
			left.setOnClickListener(clickListener);
		}
	}

	/**
	 * 返回键点击
	 * 
	 * @param clickListener
	 */
	public void setBackBtnClick(OnClickListener clickListener) {
		if (null != lefTextView) {
			lefTextView.setVisibility(View.INVISIBLE);
		}
		if (left != null) {
			left.setVisibility(View.VISIBLE);
			left.setOnClickListener(clickListener);
		}
	}

	public void setLeftText(int resId, OnClickListener clickListener) {
		if (null != left) {
			left.setVisibility(View.INVISIBLE);
		}
		if (lefTextView != null) {
			lefTextView.setVisibility(View.VISIBLE);
			lefTextView.setText(resId);
			lefTextView.setOnClickListener(clickListener);
		}
	}

	public void setLeftText(String text, OnClickListener clickListener) {
		if (null != left) {
			left.setVisibility(View.INVISIBLE);
		}
		if (lefTextView != null) {
			lefTextView.setVisibility(View.VISIBLE);
			lefTextView.setText(text);
			lefTextView.setOnClickListener(clickListener);
		}
	}

	public void setRightButton(int resId, OnClickListener clickListener) {
		if (null != rightTextView) {
			rightTextView.setVisibility(View.INVISIBLE);
		}
		if (right != null) {
			right.setVisibility(View.VISIBLE);
			right.setImageResource(resId);
			right.setOnClickListener(clickListener);
		}
	}

	public void setRightText(int resId, OnClickListener clickListener) {
		if (null != right) {
			right.setVisibility(View.INVISIBLE);
		}
		if (rightTextView != null) {
			rightTextView.setVisibility(View.VISIBLE);
			rightTextView.setText(resId);
			rightTextView.setOnClickListener(clickListener);
		}
	}

	public void setRightText(String text, OnClickListener clickListener) {
		if (null != right) {
			right.setVisibility(View.INVISIBLE);
		}
		if (rightTextView != null) {
			rightTextView.setVisibility(View.VISIBLE);
			rightTextView.setText(text);
			rightTextView.setOnClickListener(clickListener);
		}
	}

	public void setLeftTextVisibility(boolean b) {
		if (b) {
			lefTextView.setVisibility(View.VISIBLE);
		} else {
			lefTextView.setVisibility(View.INVISIBLE);
		}
	}

	public void setRightTextVisibility(boolean b) {
		if (b) {
			rightTextView.setVisibility(View.VISIBLE);
		} else {
			rightTextView.setVisibility(View.INVISIBLE);
		}
	}
}
