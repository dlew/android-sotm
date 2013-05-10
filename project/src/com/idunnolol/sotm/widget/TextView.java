package com.idunnolol.sotm.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.idunnolol.sotm.R;
import com.idunnolol.utils.FontCache;

public class TextView extends android.widget.TextView {

	public TextView(Context context) {
		this(context, null);
	}

	public TextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextView, defStyle, 0);
		if (ta.hasValue(R.styleable.TextView_font)) {
			String fontPath = ta.getString(R.styleable.TextView_font);
			setTypeface(FontCache.getTypeface(getContext(), fontPath));
		}
		ta.recycle();
	}
}
