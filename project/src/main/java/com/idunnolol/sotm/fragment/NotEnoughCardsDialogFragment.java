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
import com.idunnolol.sotm.data.Card.Type;
import com.idunnolol.sotm.data.Db;

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Type type = Type.values()[getArguments().getInt(ARG_TYPE)];

        return new AlertDialog.Builder(getActivity())
            .setMessage(getErrorResId(type))
            .setPositiveButton(R.string.button_configure, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getActivity(), CardConfigActivity.class));
                }
            })
            .create();
    }

    public static int getErrorResId(Type type) {
        switch (type) {
            case HERO:
                if (Db.getCards(Type.HERO).size() == 0) {
                    return R.string.message_no_heroes;
                }
                else {
                    return R.string.message_not_enough_heroes;
                }
            case VILLAIN:
                return R.string.message_no_villains;
            case ENVIRONMENT:
                return R.string.message_no_environments;
        }

        return 0;
    }
}
