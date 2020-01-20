package com.visione.taskreminder.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.visione.taskreminder.activities.Login;

import java.util.HashMap;

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private static final String PREF_NAME = "Login";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String USERNAME = "username";
    private static final String PIN = "pin";

    public SessionManager(Context context) {
        this.context = context;
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String username, String pin){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(USERNAME, username);
        editor.putString(PIN, pin);
        editor.commit();
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            sendToLoginActivity();
        }
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(USERNAME, pref.getString(USERNAME, null));
        user.put(PIN, pref.getString(PIN, null));

        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        sendToLoginActivity();
    }

    public boolean isLoggedIn(){
        return  pref.getBoolean(IS_LOGIN, false);
    }

    private void sendToLoginActivity(){
        Intent logIntent = new Intent(context, Login.class);
        logIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        logIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(logIntent);
    }
}
