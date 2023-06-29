package com.tseopela.destination;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    //creating the controls
    private EditText edUsername, edPassword;
    private Button btnRegister;
    FirebaseAuth mAuth = FirebaseAuth.getInstance(); //firebase connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //assigning the controls to the correct id
        edUsername = findViewById(R.id.edUserName);
        edPassword = findViewById(R.id.edPassword);
        btnRegister = findViewById(R.id.btnRegister);

        //onclick-listener: register function
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(); //function
            }
        });

    }

    private void register() {
        //creating variables that will hold the credentials data
        //enabling the user to enter the credentials
        String userEmail = edUsername.getText().toString();
        String userPassword = edPassword.getText().toString();

        //if-statement to validate if the strings are entered
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            //toast will be displayed if the edit boxes are blank
            Toast.makeText(RegisterActivity.this, "Email,password or " +
                    "User Type cannot be blank", Toast.LENGTH_SHORT).show();
        } else {
            //validating and authenticating the credentials
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //if the credentials are correct proceed to the Login Activity
                            if (task.isSuccessful()) {
                                //display toast + navigate to Login activity
                                Toast.makeText(RegisterActivity.this, "User profile created",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this,
                                        LoginActivity.class));
                            } else {
                                //if credentials are incorrect display a toast with error message
                                Toast.makeText(RegisterActivity.this, "Registration failure"
                                        + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}