package it.uniba.di.ivu.sms16.gruppo2.dibapp.authentication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;


public class AuthSignInFragment extends Fragment {

    private static final String EXTRA_EMAIL_STRING = "it.uniba.di.ivu.sms16.gruppo2.dibapp.EMAIL_STRING";
    private AuthFragmentsInteraction mAuthFragmentsInteraction = null;
    private EditText mEmailEditTxt;
    private EditText mPasswordEditTxt;
    private TextView mForgetPswTxtView;
    private Button mDoneSignInBtn;

    public AuthSignInFragment() {
    }

    public static AuthSignInFragment newInstance(String email) {
        AuthSignInFragment fragment = new AuthSignInFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_EMAIL_STRING, email);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AuthFragmentsInteraction) {
            mAuthFragmentsInteraction = (AuthFragmentsInteraction) context;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth_signin, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        final String email = getArguments().getString(EXTRA_EMAIL_STRING);

        mEmailEditTxt = (EditText) getActivity().findViewById(R.id.email_signin_edit);
        mPasswordEditTxt = (EditText) getActivity().findViewById(R.id.password_signin_edit);
        mForgetPswTxtView = (TextView) getActivity().findViewById(R.id.forget_psw_signin);
        mDoneSignInBtn = (Button) getActivity().findViewById(R.id.done_signin_button);

        mEmailEditTxt.setText(email);
        mEmailEditTxt.setKeyListener(null);

        mForgetPswTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuthFragmentsInteraction != null) {
                    mAuthFragmentsInteraction.onPasswordReset(email);
                }
            }
        });

        mPasswordEditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mDoneSignInBtn.performClick();
                    return true;
                }
                return false;
            }
        });


        mDoneSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAuthFragmentsInteraction != null) {

                    if (mPasswordEditTxt.getText().toString().isEmpty()) {
                        mPasswordEditTxt.setError(getString(R.string.empty_field_error));
                        return;
                    }

                    mAuthFragmentsInteraction.onSignIn(email, mPasswordEditTxt.getText().toString());
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