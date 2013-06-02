package com.idunnolol.sotm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.idunnolol.sotm.BitmapCache;
import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.utils.Ui;

public class IconView extends FrameLayout {

	private ShadedImageView mIconView;
	private TextView mRandomView;
	private TextView mAdvancedView;

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
		mAdvancedView = Ui.findView(this, R.id.icon_advanced_view);
	}

	public void bind(Card card) {
		boolean isRandom = card.isRandom();
		boolean isAdvanced = card.isAdvanced();

		mIconView.setVisibility(isRandom ? View.GONE : View.VISIBLE);
		mRandomView.setVisibility(isRandom ? View.VISIBLE : View.GONE);
		mAdvancedView.setVisibility(isAdvanced ? View.VISIBLE : View.GONE);

		if (!isRandom) {
			mIconView.setImageBitmap(BitmapCache.getBitmap(card.getIconResId()));
		}

		if (isAdvanced) {
			mIconView.setShadeColor(getResources().getColor(R.color.advanced_shade));
		}
		else {
			mIconView.disableShade();
		}
	}
}
