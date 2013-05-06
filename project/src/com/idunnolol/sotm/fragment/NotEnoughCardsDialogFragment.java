package com.idunnolol.sotm.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.activity.CardConfigActivity;
import com.idunnolol.sotm.data.Db;
import com.idunnolol.sotm.data.Card.Type;

public class NotEnoughCardsDialogFragment extends DialogFragment {

	public static final String TAG = NotEnoughCardsDialogFragment.class.getName();

	private static final String ARG_TYPE = "ARG_TYPE";

	public static NotEnoughCardsDialogFragment newInstance(Type type) {
		NotEnoughCardsDialogFragment fragment = new NotEnoughCardsDialogFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TYPE, type.ordinal());
		fragment.setArguments(args);
		return fragment;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Type type = Type.values()[getArguments().getInt(ARG_TYPE)];

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		switch (type) {
		case HERO:
			if (Db.getCards(Type.HERO).size() == 0) {
				builder.setMessage(R.string.message_no_heroes);
			}
			else {
				builder.setMessage(R.string.message_not_enough_heroes);
			}
			break;
		case VILLAIN:
			builder.setMessage(R.string.message_no_villains);
			break;
		case ENVIRONMENT:
			builder.setMessage(R.string.message_no_environments);
			break;
		}

		builder.setPositiveButton(R.string.button_configure, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dismissAllowingStateLoss();
				startActivity(new Intent(getActivity(), CardConfigActivity.class));
			}
		});

		builder.setNegativeButton(R.string.cancel, null);

		return builder.create();
	}
}
