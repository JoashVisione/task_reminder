package com.visione.taskreminder.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.visione.taskreminder.R;
import com.visione.taskreminder.activities.Login;

import es.dmoral.toasty.Toasty;

public class ResetPin extends AppCompatActivity {
    private EditText fieldNewPin, fieldNewPinConfirm, fieldUsername, fieldSecWord;
    public SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pin);

        sharedPreferences = getSharedPreferences("Register", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initializeComponents();
    }

    private void initializeComponents() {
        fieldUsername = findViewById(R.id.username);
        fieldNewPin = findViewById(R.id.new_pin);
        fieldNewPinConfirm = findViewById(R.id.new_pin_confirm);
        fieldSecWord = findViewById(R.id.sec_word);
        Button btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = fieldUsername.getText().toString();
                String pin = fieldNewPin.getText().toString();
                String pinConfirm = fieldNewPinConfirm.getText().toString();
                String security = fieldSecWord.getText().toString();

                if(TextUtils.isEmpty(username)){
                    Toasty.info(ResetPin.this, "Username cannot be empty", Toasty.LENGTH_SHORT).show();

                } else  if(TextUtils.isEmpty(pin) ||  TextUtils.isEmpty(pinConfirm)){
                    Toasty.info(ResetPin.this, " PIN cannot be empty", Toasty.LENGTH_SHORT).show();

                } else  if(TextUtils.isEmpty(security)){
                    Toasty.info(ResetPin.this, " Secret word cannot be empty", Toasty.LENGTH_SHORT).show();

                } else  if(!pin.equals(pinConfirm)){
                    Toasty.info(ResetPin.this, " PIN does not match", Toasty.LENGTH_SHORT).show();
                } else {
                    String prefUsername = "", prefSecurity = "";
                    if(sharedPreferences .contains("username")){
                        prefUsername = sharedPreferences.getString("username", "");
                    }
                    if(sharedPreferences.contains("security")){
                        prefSecurity = sharedPreferences.getString("security", "");
                    }
                    if(username.equals(prefUsername) && security.equals(prefSecurity)){
                        editor.putString("pin", pin);
                        editor.apply();
                        sendToLoginActivity();
                        finish();
                        Toasty.success(ResetPin.this, "Login with new PIN", Toasty.LENGTH_SHORT).show();

                    } else {
                        Toasty.error(ResetPin.this, "Username or secret word incorrect", Toasty.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void sendToLoginActivity(){
        Intent logIntent = new Intent(ResetPin.this, Login.class);
        startActivity(logIntent);
    }
}
