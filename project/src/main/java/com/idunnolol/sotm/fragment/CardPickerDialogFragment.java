package com.idunnolol.sotm.fragment;

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
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.sotm.data.Prefs;
import com.idunnolol.sotm.widget.CardAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CardPickerDialogFragment extends DialogFragment {

    public static final String TAG = CardPickerDialogFragment.class.getName();

    private static final String ARG_TYPE = "ARG_TYPE";
    private static final String ARG_GAME_SETUP = "ARG_GAME_SETUP";
    private static final String ARG_CARD = "ARG_CARD";

    public static CardPickerDialogFragment newInstance(Type type, GameSetup gameSetup, Card card) {
        CardPickerDialogFragment fragment = new CardPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type.ordinal());
        args.putParcelable(ARG_GAME_SETUP, gameSetup);
        args.putParcelable(ARG_CARD, card);
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
        Type type = getType();
        GameSetup gameSetup = getArguments().getParcelable(ARG_GAME_SETUP);

        // We disable heroes that are already selected.  We don't bother with villains
        // or environments; I don't care if someone re-selects what they already had.
        //
        // We do not disable the clicked card (and alts) because the user may want to
        // select a different version of the current card.
        Set<Card> disabledCards = new HashSet<Card>();
        if (type == Type.HERO) {
            Card currCard = getArguments().getParcelable(ARG_CARD);
            for (Card hero : gameSetup.getHeroes()) {
                if (!hero.equals(currCard)) {
                    disabledCards.addAll(Db.getCardAndAlternates(hero));
                }
            }
        }

        Collection<Card> cards = Db.getCards(type);

        // Add in advanced cards (if allowed)
        if (type == Type.VILLAIN && Prefs.isAdvancedAllowed()) {
            Collection<Card> baseCards = cards;
            cards = new ArrayList<Card>(baseCards.size() * 2);
            for (Card villain : baseCards) {
                cards.add(villain);

                if (villain.canBeAdvanced()) {
                    Card advancedVillain = new Card(villain);
                    advancedVillain.makeAdvanced();
                    cards.add(advancedVillain);
                }
            }
        }

        mAdapter = new CardAdapter(getActivity(), cards, disabledCards);

        int titleResId;
        final Card randomCard;
        switch (type) {
            case HERO:
                titleResId = R.string.title_hero;
                randomCard = Card.RANDOM_HERO;
                break;
            case VILLAIN:
                titleResId = R.string.title_villain;
                randomCard = Card.RANDOM_VILLAIN;
                break;
            case ENVIRONMENT:
            default:
                titleResId = R.string.title_environment;
                randomCard = Card.RANDOM_ENVIRONMENT;
                break;
        }

        return new AlertDialog.Builder(getActivity())
            .setTitle(titleResId)
            .setSingleChoiceItems(mAdapter, 0, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dismissAllowingStateLoss();
                    mListener.onCardSelected(mAdapter.getItem(which));
                }
            })
            .setNeutralButton(R.string.card_random, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onCardSelected(randomCard);
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .create();
    }

    //////////////////////////////////////////////////////////////////////////
    // Listener interface

    public interface CardPickerDialogFragmentListener {

        public void onCardSelected(Card card);
    }
}
