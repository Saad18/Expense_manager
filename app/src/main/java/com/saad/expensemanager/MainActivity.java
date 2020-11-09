package com.saad.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;
    private Button btn_login;
    private TextView textViewForgetPassword;
    private TextView textViewSignUpHere;
    private ProgressBar progressBar;

    //Firebase..
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){

            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

        }

        loginDetails();

    }

    private void loginDetails(){

        editTextLoginEmail = findViewById(R.id.email_login);
        editTextLoginPassword = findViewById(R.id.password_login);
        btn_login = findViewById(R.id.btn_login);
        textViewForgetPassword = findViewById(R.id.forget_password);
        textViewSignUpHere = findViewById(R.id.signUp_reg);
        progressBar = findViewById(R.id.indeterminateBar);


        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextLoginEmail.getText().toString().trim();
                String password = editTextLoginPassword.getText().toString().trim();

                if (email.isEmpty()){
                    editTextLoginEmail.setError("Email required..");
                    editTextLoginEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()){
                    editTextLoginPassword.setError("password required..");
                    editTextLoginPassword.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

               mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(),
                                    "Login Successful..", Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.VISIBLE);

                        }else {
                            Toast.makeText(getApplicationContext(),
                                    "Login Failed..", Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.VISIBLE);
                        }

                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });

            }

        });

        textViewSignUpHere.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));

            }
        });

        textViewForgetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });

    }

}