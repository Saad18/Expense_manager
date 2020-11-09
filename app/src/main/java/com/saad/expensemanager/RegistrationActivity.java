package com.saad.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;



public class RegistrationActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;

    EditText editTextFullName;
    EditText editTextEmail;
    EditText editTextPassword;
    Button btnReg;
    TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrtion);

        registration();
        SignIn();



    }

    private void registration() {

        editTextFullName = findViewById(R.id.name_registration);
        editTextEmail = findViewById(R.id.email_registration);
        editTextPassword = findViewById(R.id.password_registration);
        btnReg = findViewById(R.id.btn_reg);
        signIn = findViewById(R.id.signIn_here);

        mAuth = FirebaseAuth.getInstance();

        btnReg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullName = editTextFullName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (fullName.isEmpty()) {
                    editTextFullName.setError("Required field");
                    editTextFullName.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    editTextEmail.setError("Required field");
                    editTextEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Please provide valid email!");
                    editTextEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    editTextPassword.setError("Required field");
                    editTextPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    editTextPassword.setError("Password must be at least 6 characters!");
                    editTextPassword.requestFocus();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            User user = new User(fullName, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegistrationActivity.this,
                                                "user has been registered successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegistrationActivity.this,
                                                "Failed to register! try again!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        } else {
                            Toast.makeText(RegistrationActivity.this,
                                    "Failed to register! try again!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

    }

    private void SignIn(){
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}