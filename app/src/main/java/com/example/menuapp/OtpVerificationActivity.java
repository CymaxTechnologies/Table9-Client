package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {
    String email;
    String password;
    String verificationCode="Abcdek2k3h4j2";
    String phone;
    EditText otp;
    TextView otpno;
    Button button;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        otp=(EditText)findViewById(R.id.otp);
        otpno=(TextView)findViewById(R.id.otpno);
        button=(Button)findViewById(R.id.otpbutton);

        email=(String)getIntent().getStringExtra("email");
        password=(String)getIntent().getStringExtra("password");
        phone=(String)getIntent().getStringExtra("phone");
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(OtpVerificationActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
                verifyCode();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(OtpVerificationActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationCode = s;
                Toast.makeText(OtpVerificationActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phone,                     // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                OtpVerificationActivity.this,        // Activity (for callback binding)
                mCallback);
        otpno.setText("Otp send to +91 "+phone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp.getText().toString().length()<6||otp.getText().toString().equals(""))
                {
                    Toast.makeText(OtpVerificationActivity.this,"Invalid Codet",Toast.LENGTH_SHORT).show();



                }
                else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp.getText().toString());
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseAuth.getInstance().signOut();
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(OtpVerificationActivity.this,"Validation Succesfull",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),ResturantActivity.class));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OtpVerificationActivity.this,"Invalid Code",Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });
    }
    void verifyCode()
    {
        Toast.makeText(OtpVerificationActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(OtpVerificationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //checking if success
                if(task.isSuccessful()){
                    //display some message here
                    Toast.makeText(OtpVerificationActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(OtpVerificationActivity.this, ResturantActivity.class);
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("mobile").setValue(phone);
                    startActivity(intent);
                    finish();
                }else{
                    //display some message here
                    Toast.makeText(OtpVerificationActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                }


            }
        });

    }

}