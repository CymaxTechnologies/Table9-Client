package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    EditText email,password,phone;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    String verificationCode;
    ProgressDialog bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        bar=new ProgressDialog(LoginActivity.this);
         bar.setMessage("Please wait while we verify your number");
         bar.setTitle("T9App");
         bar.setCancelable(false);
        email=(EditText) findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        phone=(EditText)findViewById(R.id.phone);
        ImageButton btn=(ImageButton) findViewById(R.id.login_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e=email.getText().toString();
                String p=password.getText().toString();
                String m=phone.getText().toString();
                if(e.isEmpty() ||p.isEmpty()||m.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"All feilds are required",Toast.LENGTH_LONG).show();
                }
                else
                {
                    p=phone.getText().toString();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91"+p,                     // Phone number to verify
                            60,                           // Timeout duration
                            TimeUnit.SECONDS,                // Unit of timeout
                            LoginActivity.this,        // Activity (for callback binding)
                            mCallback);
                }
                bar.show();

            }
        });
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(LoginActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("mobile").setValue(phone.getText());
                            startActivity(intent);
                            finish();
                        }else{
                            //display some message here
                            Toast.makeText(LoginActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        bar.dismiss();

                    }
                });
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this,"verification fialed",Toast.LENGTH_SHORT).show();
                bar.dismiss();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(LoginActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };
    }

}