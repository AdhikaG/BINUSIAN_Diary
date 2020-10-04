package com.example.binusiandiary.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.binusiandiary.Help;
import com.example.binusiandiary.MainActivity;
import com.example.binusiandiary.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class Login extends AppCompatActivity {
    EditText loginEmail;
    EditText loginPasssword;
    Button loginButton;
    TextView forgetPassword;
    TextView createAccount;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebasestore;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login to BINUS Diary");

        progressBar = findViewById(R.id.progressBar3);

        loginEmail=findViewById(R.id.loginemail);
        loginPasssword=findViewById(R.id.loginPassword);
        loginButton=findViewById(R.id.loginBtn);
        forgetPassword=findViewById(R.id.forgotPasword);
        createAccount=findViewById(R.id.createAccount);

        firebaseAuth = FirebaseAuth.getInstance();
        firebasestore = FirebaseFirestore.getInstance();



        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lEmail =loginEmail.getText().toString();
                String lPassword = loginPasssword.getText().toString();

                if(lEmail.isEmpty()||lPassword.isEmpty()){
                    Toast.makeText(Login.this, "Fields are Required", Toast.LENGTH_SHORT).show();
                    return;
                }//apus notes
                progressBar.setVisibility(View.VISIBLE);

                if (firebaseAuth.getCurrentUser().isAnonymous()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    firebasestore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this, "All Temporary Notes are Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //apus temporary user
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this, "Temporary User Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                firebaseAuth.signInWithEmailAndPassword(lEmail,lPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Login Failed. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }


}
