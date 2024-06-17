package com.heaven.moviereview;

import static android.accounts.AccountManager.KEY_PASSWORD;
import static android.accounts.AccountManager.KEY_USERDATA;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton;
    TextView password;
    SharedPreferences sharedPreferences;

    private static final String PREFS_NAME= "MyPrefs";
    private static final String PREFS_USERNAME= "username";
    private static final String PREFS_PASSWORD= "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText= findViewById(R.id.usernameEditText);
        passwordEditText= findViewById(R.id.passwordEditText);
        loginButton= findViewById(R.id.loginButton);
        password=findViewById(R.id.password);


        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String savedUsername = sharedPreferences.getString(KEY_USERDATA, "");
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");

        if (!TextUtils.isEmpty(savedUsername) && !TextUtils.isEmpty(savedPassword)) {
            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
        }

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(loginActivity.this,"UserName:admin , Password:1234",Toast.LENGTH_LONG)
                        .show();
            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username= usernameEditText.getText().toString();
                String password=passwordEditText.getText().toString();

                if (validateCredentials(username, password)){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_USERDATA, username);
                    editor.putString(KEY_PASSWORD, password);
                    editor.apply();

                    Intent intent = new Intent(loginActivity.this, SearchMovieActivity.class );
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(loginActivity.this, "Invilid User Name Or Password",Toast.LENGTH_SHORT ).show();

                }

            }
        });
    }
    private boolean validateCredentials(String username, String password) {
        // Perform the necessary validation logic here
        // For example, check if the username and password match your requirements
        return username.equals("admin") && password.equals("1234");
    }
}