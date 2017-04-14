package it.uniba.di.ivu.sms16.gruppo2.dibapp.authentication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;


public class AuthEmailFragment extends Fragment {

    private EditText mEmailEditTxt;
    private Button mContinuetBtn;
    private AuthFragmentsInteraction mAuthFragmentsInteraction = null;

    public AuthEmailFragment() {
    }

    public static AuthEmailFragment newInstance() {
        return new AuthEmailFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AuthFragmentsInteraction) {
            mAuthFragmentsInteraction = (AuthFragmentsInteraction) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth_email, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        mEmailEditTxt = (EditText) getActivity().findViewById(R.id.auth_email_edit);
        mContinuetBtn = (Button) getActivity().findViewById(R.id.auth_continue_button);
        mContinuetBtn.setEnabled(false);

        mEmailEditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mContinuetBtn.performClick();
                    return true;
                }
                return false;
            }
        });

        mEmailEditTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //DO NOTHING
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mContinuetBtn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEmailEditTxt.getText().toString().isEmpty()) {
                    mContinuetBtn.setEnabled(false);
                }
            }
        });

        mContinuetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditTxt.getText().toString().trim();

                if (email.isEmpty()) {
                    mEmailEditTxt.setError(getText(R.string.empty_field_error));
                    return;
                }

                if (!email.contains(getString(R.string.email_uniba_regex))) {

                    mEmailEditTxt.setError(getString(R.string.email_uniba_regex_error));
                    return;
                }

                if (mAuthFragmentsInteraction != null) {
                    mAuthFragmentsInteraction.onCheckEmail(email);
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAuthFragmentsInteraction = null;
    }
}
