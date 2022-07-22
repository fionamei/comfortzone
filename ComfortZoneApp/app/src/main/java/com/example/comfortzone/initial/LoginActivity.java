package com.example.comfortzone.initial;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comfortzone.R;
import com.example.comfortzone.ui.HostActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.facebook.ParseFacebookUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FIELDS = "fields";
    public static final String PUBLIC_PROF = "public_profile";
    public static final List<String> mPermissions = new ArrayList<String>() {{
        add(PUBLIC_PROF);
        add(KEY_EMAIL);
    }};

    private EditText etUsername;
    private EditText etPassword;
    private Button btnSignin;
    private TextView tvSignup;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            goHostActivity();
        }

        getSupportActionBar().hide();

        initViews();
        setupListeners();
    }

    private void goHostActivity() {
        Intent i = new Intent(this, HostActivity.class);
        startActivity(i);
        finish();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignin = findViewById(R.id.btnSignin);
        tvSignup = findViewById(R.id.tvSignup);
        loginButton = findViewById(R.id.login_button);
    }

    private void setupListeners() {
        loginListener();
        signupListener();
        facebookListener();
    }

    private void loginListener() {
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Toast.makeText(LoginActivity.this, "Issue with login!", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    goHostActivity();
                }
            }
        });
    }

    private void signupListener() {
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }

    private void facebookListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user == null) {
                        } else if (user.isNew()) {
                            Toast.makeText(LoginActivity.this, "Logging in, please wait", Toast.LENGTH_SHORT).show();
                            getUserDetailFromFB();
                        } else {
                            goHostActivity();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserDetailFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
            ParseUser user = ParseUser.getCurrentUser();
            try {
                if (object.has(KEY_NAME))
                    user.setUsername(object.getString(KEY_NAME));
                if (object.has(KEY_EMAIL))
                    user.setEmail(object.getString(KEY_EMAIL));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    goInitialSetup();
                }
            });
        });

        Bundle parameters = new Bundle();
        parameters.putString(KEY_FIELDS, String.format("%s,%s", KEY_NAME, KEY_EMAIL));
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void goInitialSetup() {
        Intent i = new Intent(this, InitialComfortActivity.class);
        startActivity(i);
        finish();
    }
}