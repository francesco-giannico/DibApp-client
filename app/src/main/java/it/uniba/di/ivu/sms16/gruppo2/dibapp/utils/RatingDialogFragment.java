package it.uniba.di.ivu.sms16.gruppo2.dibapp.utils;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;

/**
 * Created by antoniolategano on 04/07/16.
 */
public class RatingDialogFragment extends DialogFragment {

    public RatingBar mRatingBar;
    public Button mRateButton;
    RatingDialogListener mRatingDialogListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rating_bar_popup, null);

        mRatingBar = (RatingBar) view.findViewById(R.id.noteRatingBar);
        mRateButton = (Button) view.findViewById(R.id.rateButton);

        mRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatingDialogListener.onRateButtonClick(RatingDialogFragment.this);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mRatingDialogListener = (RatingDialogListener) context;
    }

    public interface RatingDialogListener {
        void onRateButtonClick(DialogFragment dialog);

    }
}
