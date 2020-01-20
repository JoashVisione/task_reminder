package com.visione.taskreminder.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.visione.taskreminder.R;
import com.visione.taskreminder.session.ResetPin;
import com.visione.taskreminder.session.SessionManager;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {
    private EditText fieldPin, fieldUsername;
    public TextView txtCreateAccount;
    SessionManager session;
    String mUsername = "";

    public static android.content.SharedPreferences sharedPreferences = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());


        initializeComponents();

        boolean isRegistered = sharedPreferences.getBoolean("IS_REGISTERED", false);
        if (isRegistered){
            txtCreateAccount.setVisibility(View.GONE);
        } else {
            txtCreateAccount.setVisibility(View.VISIBLE);
        }

        //if(session.isLoggedIn()){
            fieldUsername.setText(mUsername);
            //fieldUsername.setEnabled(false);
            fieldPin.requestFocus();
        //}


    }



    private void initializeComponents() {
        fieldPin = findViewById(R.id.login_pin);
        fieldUsername = findViewById(R.id.login_username);
        TextView txtForgotPin = findViewById(R.id.forgot_password);
        txtCreateAccount = findViewById(R.id.txt_register);
        Button btnLogin = findViewById(R.id.btn_login);
        sharedPreferences = getSharedPreferences("Register", Context.MODE_PRIVATE);

        if(sharedPreferences.contains("username")){
            mUsername = (sharedPreferences).getString("username", "");
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = fieldUsername.getText().toString();
                String pin = fieldPin.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toasty.info(Login.this, " Username cannot be empty", Toasty.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(pin)) {
                    Toasty.info(Login.this, " PIN cannot be empty", Toasty.LENGTH_SHORT).show();

                } else if (pin.trim().length() < 4 || pin.trim().length() > 4) {
                    Toasty.info(Login.this, " PIN must be 4 digits", Toasty.LENGTH_SHORT).show();
                } else {
                    String prefUsername = "", prefPin = "";
                    if(sharedPreferences.contains("username")){
                        prefUsername = (sharedPreferences).getString("username", "");
                    }
                    if(sharedPreferences.contains("pin")){
                        prefPin = (sharedPreferences).getString("pin", "");
                    }
                    if(username.equals(prefUsername) && pin.equals(prefPin)){
                        session.createLoginSession(username, pin);
                        //if(session.)
                        sendToMainActivity();
                        finish();
                    } else {
                        //Toasty.info(Login.this, "Username or password incorrect", Toasty.LENGTH_SHORT).show();
                        showAlertDiaog("Login Failed", "Username or password incorrect!", false );
                    }
                }
            }
        });

        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegisterActicity();
            }
        });

        txtForgotPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPin();
            }
        });
    }
    private void sendToRegisterActicity(){
        Intent regIntent = new Intent(Login.this, Register.class);
        startActivity(regIntent);
        finish();

    }

    private void resetPin(){
        Intent resetIntent = new Intent(Login.this, ResetPin.class);
        startActivity(resetIntent);
    }
    private void sendToIntroActivity() {
        Intent introIntent = new Intent(Login.this, IntroActivity.class);
        introIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        introIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(introIntent);
        finish();
    }

    private void sendToMainActivity() {
        Intent introIntent = new Intent(Login.this, MainActivity.class);
        introIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        introIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(introIntent);
        finish();
    }

    public void showAlertDiaog(String title, String message, Boolean status){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);

        if(status != null)
            alertDialog.setIcon((status) ? R.drawable.success : R.drawable.failed);


        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(session.isLoggedIn()){
            fieldUsername.setText(mUsername);
            //fieldUsername.setEnabled(false);
            fieldPin.requestFocus();
        //}
    }
}
