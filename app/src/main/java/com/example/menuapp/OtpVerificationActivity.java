package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    TextView time;
    ProgressBar progressBar;
    TextView resendOTP;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        otp=(EditText)findViewById(R.id.otp);
        otpno=(TextView)findViewById(R.id.otpno);
        button=(Button)findViewById(R.id.otpbutton);
        button.setEnabled(false);
        button.setBackgroundColor(Color.GRAY);
        time=(TextView)findViewById(R.id.time);
        progressBar=(ProgressBar)findViewById(R.id.progressbar) ;
        resendOTP=(TextView)findViewById(R.id.resendotp) ;
        resendOTP.setEnabled(false);
        progressBar.setProgress(0);

        email=(String)getIntent().getStringExtra("email");
        password=(String)getIntent().getStringExtra("password");
        phone=(String)getIntent().getStringExtra("phone");
        getSupportActionBar().hide();
       Thread my=new myTThread();
       my.start();
      otp.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
                     if(s.toString().length()==6)
                     {
                         button.setEnabled(true);
                         button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                     }
                     else
                     {
                         button.setEnabled(false);
                         button.setBackgroundColor(Color.GRAY);
                     }
          }

          @Override
          public void afterTextChanged(Editable s) {

          }
      });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(OtpVerificationActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
                verifyCode(phoneAuthCredential);
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
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP.setEnabled(false);
                new myTThread().start();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp.getText().toString().length()<6||otp.getText().toString().equals(""))
                {
                    Toast.makeText(OtpVerificationActivity.this,"Invalid Codet",Toast.LENGTH_SHORT).show();



                }
                else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp.getText().toString());
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile");
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            UserProfile userProfile = new UserProfile();
                                            userProfile.setPhone(phone);
                                            ref.setValue(userProfile);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                startActivity(new Intent(getApplicationContext(),ResturantActivity.class));
                            } else {
                                Toast.makeText(OtpVerificationActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
    }});}
    void verifyCode(PhoneAuthCredential phoneAuthCredential)
    {
        Toast.makeText(OtpVerificationActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                UserProfile userProfile = new UserProfile();
                                userProfile.setPhone(phone);
                                ref.setValue(userProfile);
                                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("uname",userProfile.name);
                                editor.putString("uphone",userProfile.phone);
                                editor.putString("uemail",userProfile.email);
                                editor.apply();
                                startActivity(new Intent(getApplicationContext(),ResturantActivity.class));
                            }
                            else {
                                startActivity(new Intent(getApplicationContext(),ResturantActivity.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(OtpVerificationActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==6)
                {
                    button.setEnabled(true);
                    button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                else
                {
                    button.setEnabled(false);
                    button.setBackgroundColor(Color.GRAY);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
class myTThread extends Thread
{

    @Override
    public void run() {
        for(int i=0;i<60;i++)
        {
            try {
                Thread.sleep(i*100);
                final int finalI = i;
                OtpVerificationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time.setText("You  can click resend after "+(60- finalI)+" seconds");
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        OtpVerificationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                time.setText("You can resend otp now");
            }
        });
        resendOTP.setEnabled(true);
    }
}

}