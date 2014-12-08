package com.idunnolol.sotm.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import com.idunnolol.sotm.R;

public class AboutDialogFragment extends DialogFragment {

    public static final String TAG = AboutDialogFragment.class.getName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.title_about);

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.about_app));
        sb.append("<br /><br />");
        sb.append(getString(R.string.about_explanation));
        sb.append("<br /><br />");
        sb.append(getString(R.string.about_tracker));
        sb.append("<br /><br />");
        sb.append(getString(R.string.credit_points));

        return new AlertDialog.Builder(getActivity())
            .setMessage(Html.fromHtml(sb.toString()))
            .setNeutralButton(R.string.ok, null)
            .create();
    }

    @Override
    public void onResume() {
        super.onResume();

        ((TextView) getDialog().findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

}
