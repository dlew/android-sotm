package com.idunnolol.sotm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.idunnolol.sotm.R;

public class CheckableRow extends LinearLayout implements Checkable {

	private CheckBox mCheckBox;

	public CheckableRow(Context context) {
		super(context);
	}

	public CheckableRow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mCheckBox = (CheckBox) findViewById(R.id.checkbox);
	}

	//////////////////////////////////////////////////////////////////////////
	// android.widget.Checkable

	@Override
	public boolean isChecked() {
		return mCheckBox.isChecked();
	}

	@Override
	public void setChecked(boolean checked) {
		mCheckBox.setChecked(checked);
	}

	@Override
	public void toggle() {
		mCheckBox.toggle();
	}
}
