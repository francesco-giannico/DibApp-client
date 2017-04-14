package it.uniba.di.ivu.sms16.gruppo2.dibapp.authentication;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;


public class AuthSignUpFragment extends Fragment {

    private static final String EXTRA_EMAIL_STRING = "it.uniba.di.ivu.sms16.gruppo2.dibapp.EMAIL_STRING";

    private EditText mEmailEditTxt;
    private EditText mPasswordEditTxt;
    private EditText mNameEditTxt;
    private AutoCompleteTextView mDegreeAutoComplete;
    private Button mDoneBtn;

    private AuthFragmentsInteraction mAuthFragmentsInteraction;

    public AuthSignUpFragment() {
    }

    public static AuthSignUpFragment newInstance(String email) {
        AuthSignUpFragment fragment = new AuthSignUpFragment();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth_signup, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        final String email = getArguments().getString(EXTRA_EMAIL_STRING);

        mEmailEditTxt = (EditText) getActivity().findViewById(R.id.email_signup_edit);
        mPasswordEditTxt = (EditText) getActivity().findViewById(R.id.password_signup_edit);
        mNameEditTxt = (EditText) getActivity().findViewById(R.id.name_edit_text);
        mDegreeAutoComplete = (AutoCompleteTextView) getActivity().findViewById(R.id.degree_autocomplete);
        mDoneBtn = (Button) getActivity().findViewById(R.id.done_signup_button);

        mEmailEditTxt.setText(email);
        mEmailEditTxt.setKeyListener(null);

        String[] degreeCourses = getResources().getStringArray(R.array.degree_courses_array);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, degreeCourses);
        mDegreeAutoComplete.setAdapter(adapter);

        mPasswordEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mPasswordEditTxt.getError() == null) {
                    return;
                }

                if (mPasswordEditTxt.getText().toString().isEmpty()) {
                    mPasswordEditTxt.setError(getString(R.string.empty_field_error));
                    return;
                }

                if (mPasswordEditTxt.getText().toString().length() <
                        getResources().getInteger(R.integer.password_min_length)) {

                    mPasswordEditTxt.setError(getString(R.string.password_length_error));
                }
            }
        });

        mNameEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mNameEditTxt.getError() == null) {
                    return;
                }

                if (mNameEditTxt.getText().toString().isEmpty()) {
                    mNameEditTxt.setError(getString(R.string.empty_field_error));
                }
            }
        });

        mDegreeAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mDegreeAutoComplete.getError() == null) {
                    return;
                }

                if (mDegreeAutoComplete.getText().toString().isEmpty()) {
                    mDegreeAutoComplete.setError(getString(R.string.empty_field_error));
                }
            }
        });


        mDegreeAutoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mDoneBtn.performClick();
                    return true;
                }
                return false;
            }
        });

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAuthFragmentsInteraction != null) {

                    if (fieldsNotEmpty() && fieldsChecked()) {

                        User user = new User(mNameEditTxt.getText().toString(), email, null);
                        user.degreeCourse = mDegreeAutoComplete.getText().toString();

                        mAuthFragmentsInteraction.onSignUp(user, mPasswordEditTxt.getText().toString());

                    } else {
                        Snackbar.make(v, R.string.generic_fields_error, Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean fieldsChecked() {
        return !(mPasswordEditTxt.getError() != null ||
                mNameEditTxt.getError() != null ||
                mDegreeAutoComplete.getError() != null);

    }

    private boolean fieldsNotEmpty() {
        return !(mPasswordEditTxt.getText().toString().isEmpty() ||
                mNameEditTxt.getText().toString().isEmpty() ||
                mDegreeAutoComplete.getText().toString().isEmpty());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAuthFragmentsInteraction = null;
    }
}
