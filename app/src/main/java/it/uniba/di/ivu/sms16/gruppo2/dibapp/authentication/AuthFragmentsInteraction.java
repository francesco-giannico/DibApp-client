package it.uniba.di.ivu.sms16.gruppo2.dibapp.authentication;


import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;

public interface AuthFragmentsInteraction {

    void onCheckEmail(String email);


    void onSignIn(String email, String psw);


    void onSignUp(User user, String psw);


    void onPasswordReset(String email);
}
