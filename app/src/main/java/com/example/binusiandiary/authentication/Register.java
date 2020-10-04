package com.example.binusiandiary.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.binusiandiary.MainActivity;
import com.example.binusiandiary.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {
    EditText registerUsername;
    EditText registerUserEmail;
    EditText registerUserPass;
    EditText ConPass;
    TextView LoginAction;
    Button SyncAccount;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Sync Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerUserEmail = findViewById(R.id.userEmail);
        registerUsername = findViewById(R.id.userName);
        registerUserPass = findViewById(R.id.password);
        ConPass = findViewById(R.id.passwordConfirm);
        LoginAction=findViewById(R.id.login);
        SyncAccount =findViewById(R.id.createAccount);
        progressBar = findViewById(R.id.progressBar4);

        firebaseAuth = FirebaseAuth.getInstance();

        LoginAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        SyncAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUserEmail = registerUserEmail.getText().toString();
                final String newUsername = registerUsername.getText().toString();
                String newPassword = registerUserPass.getText().toString();
                String new2ndPassword = ConPass.getText().toString();

                if(newUserEmail.isEmpty()||newUsername.isEmpty()||newPassword.isEmpty()||new2ndPassword.isEmpty()){
                    Toast.makeText(Register.this, "Please Fill all the form", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!newPassword.equals(new2ndPassword)){
                    Toast.makeText(Register.this, "Password and Confirm Password does not match!", Toast.LENGTH_SHORT).show();
                    return;
               }
//                overridePendingTransition(R.anim.animation_down,R.anim.animation_up);overridePendingTransition(R.anim.animation_down,R.anim.animation_up);
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider.getCredential(newUserEmail,newPassword);
                firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Register.this, "Notes are Synced", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest request =  new UserProfileChangeRequest.Builder()
                                .setDisplayName(newUsername)
                                .build();
                        user.updateProfile(request);

                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                        overridePendingTransition(R.anim.animation_down,R.anim.animation_up);
                        finish();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Failed to Connect, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }


}


