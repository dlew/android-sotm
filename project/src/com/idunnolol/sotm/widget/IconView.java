package com.idunnolol.sotm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.utils.Ui;

public class IconView extends LinearLayout {

	private ImageView mIconView;
	private TextView mRandomView;

	public IconView(Context context) {
		this(context, null);
	}

	public IconView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IconView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mIconView = Ui.findView(this, R.id.icon_image_view);
		mRandomView = Ui.findView(this, R.id.icon_random_view);
	}

	public void bind(Card card) {
		boolean isRandom = card == Card.RANDOM;
		mIconView.setVisibility(isRandom ? View.GONE : View.VISIBLE);
		mRandomView.setVisibility(isRandom ? View.VISIBLE : View.GONE);

		if (!isRandom) {
			mIconView.setImageResource(card.getIconResId());
		}
	}
}
