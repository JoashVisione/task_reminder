package com.visione.taskreminder.session;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import es.dmoral.toasty.Toasty;


import com.visione.taskreminder.R;

public class Register extends AppCompatActivity {
    private EditText fieldPin, fieldPinConfirm, fieldUsername, fieldSecuQsn;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pref = getApplicationContext().getSharedPreferences("Register", 0);
        editor = pref.edit();

        initializeComponents();
    }

    private void initializeComponents() {
        fieldUsername = findViewById(R.id.username);
        fieldPin = findViewById(R.id.register_pin);
        fieldPinConfirm = findViewById(R.id.register_pin_confirm);
        fieldSecuQsn = findViewById(R.id.sec_qsn);
        TextView txtLogin = findViewById(R.id.txt_login);
        Button btnCreateAccount = findViewById(R.id.btn_create_account);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLoginActivity();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = fieldUsername.getText().toString();
                String pin = fieldPin.getText().toString();
                String pinConfirm = fieldPinConfirm.getText().toString();
                String security = fieldSecuQsn.getText().toString();

                if(TextUtils.isEmpty(username)){
                    Toasty.info(Register.this, " Username cannot be empty", Toasty.LENGTH_SHORT).show();

                } else  if(TextUtils.isEmpty(pin)){
                    Toasty.info(Register.this, " PIN cannot be empty", Toasty.LENGTH_SHORT).show();

                } else  if(TextUtils.isEmpty(pinConfirm)){
                    Toasty.info(Register.this, " PIN cannot be empty", Toasty.LENGTH_SHORT).show();

                } else  if(TextUtils.isEmpty(security)){
                    Toasty.info(Register.this, " Secret word cannot be empty", Toasty.LENGTH_SHORT).show();

                } else  if(!pin.equals(pinConfirm)){
                    Toasty.info(Register.this, " PIN does not match", Toasty.LENGTH_SHORT).show();

                } else if (pin.trim().length() < 4 || pin.trim().length() > 4) {
                    Toasty.info(Register.this, " PIN must be 4 digits", Toasty.LENGTH_SHORT).show();
                } else {
                    editor.putString("username", username);
                    editor.putString("pin", pin);
                    editor.putString("security", security);
                    editor.putBoolean("IS_REGISTERED", true);
                    editor.apply();
                    sendToLoginActivity();
                    finish();
                    Toasty.success(Register.this, "Login using information you provided", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendToLoginActivity(){
        Intent logIntent = new Intent(Register.this, Login.class);
        startActivity(logIntent);
        finish();
    }
}
