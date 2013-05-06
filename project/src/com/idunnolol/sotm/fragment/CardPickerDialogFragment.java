package com.idunnolol.sotm.fragment;

import java.util.Collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.Card.Type;
import com.idunnolol.sotm.data.Db;
import com.idunnolol.sotm.widget.CardAdapter;

public class CardPickerDialogFragment extends DialogFragment {

	public static final String TAG = CardPickerDialogFragment.class.getName();

	private static final String ARG_TYPE = "ARG_TYPE";

	public static CardPickerDialogFragment newInstance(Type type) {
		CardPickerDialogFragment fragment = new CardPickerDialogFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TYPE, type.ordinal());
		fragment.setArguments(args);
		return fragment;
	}

	private CardPickerDialogFragmentListener mListener;

	private CardAdapter mAdapter;

	private Type getType() {
		return Type.values()[getArguments().getInt(ARG_TYPE)];
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mListener = (CardPickerDialogFragmentListener) activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Collection<Card> cards = Db.getCards(getType());
		mAdapter = new CardAdapter(getActivity(), cards);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		switch (getType()) {
		case HERO:
			builder.setTitle(R.string.title_hero);
			break;
		case VILLAIN:
			builder.setTitle(R.string.title_villain);
			break;
		case ENVIRONMENT:
			builder.setTitle(R.string.title_environment);
			break;
		}

		builder.setSingleChoiceItems(mAdapter, 0, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dismissAllowingStateLoss();
				mListener.onCardSelected(mAdapter.getItem(which));
			}
		});
		builder.setNeutralButton(R.string.card_random, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dismissAllowingStateLoss();
				mListener.onCardSelected(Card.RANDOM);
			}
		});
		builder.setNegativeButton(R.string.cancel, null);

		return builder.create();
	}

	//////////////////////////////////////////////////////////////////////////
	// Listener interface

	public interface CardPickerDialogFragmentListener {
		public void onCardSelected(Card card);
	}
}
