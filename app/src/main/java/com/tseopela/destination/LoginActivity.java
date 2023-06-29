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

public class LoginActivity extends AppCompatActivity {

    //creating the controls
    private EditText edUserName,edPassword;
    private Button btnLogin,btnRegister;
    FirebaseAuth mAuth = FirebaseAuth.getInstance(); //firebase connection


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //linking the controls
        edUserName = findViewById(R.id.edUserName);
        edPassword = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        //onclick-listener: navigating to the register activity
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register activity                --current class    --navigating to class
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        //onclick-listener: login function
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //function
                login();
            }
        });

    }

    //method that will enable the user to login to the Destination system
    private void login()
    {
        //creating variables that will hold the credentials data
        //enabling the user to enter the credentials
        String userEmail = edUserName.getText().toString();
        String userPassword = edPassword.getText().toString();

        if(userEmail.isEmpty() || userPassword.isEmpty()) {
            //toast will be displayed if the edit boxes are blank
            Toast.makeText(LoginActivity.this, "Email or password cannot be blank",
                    Toast.LENGTH_SHORT).show();
        }else{
            //validating and authenticating the credentials
            mAuth.signInWithEmailAndPassword(userEmail,userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //if the credentials are correct proceed to the Login Activity
                            if(task.isSuccessful())
                            {
                                //display toast + navigate to Login activity
                                Toast.makeText(LoginActivity.this, "Sign in successful",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this,
                                        MainActivity.class));
                            }else{
                                //if credentials are incorrect display a toast with error message
                                Toast.makeText(LoginActivity.this, "Login failure"
                                        + task.getException().getMessage(), Toast.LENGTH_SHORT).show() ;
                            }
                        }
                    });
        }
    }
}