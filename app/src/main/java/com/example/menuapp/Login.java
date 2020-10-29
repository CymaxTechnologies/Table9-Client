package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {
    EditText email,password,phone;
    TextView signup,forgot;
    Button btn;

    ProgressDialog bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        bar=new ProgressDialog(Login.this);
        bar.setCancelable(true);
        bar.setTitle("T9 App");
        bar.setMessage("Please wait....");
        getSupportActionBar().hide();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
          bar.show();
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserProfile userProfile=dataSnapshot.getValue(UserProfile.class);
                    SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("uname",userProfile.name);
                    editor.putString("uphone",userProfile.phone);
                    editor.putString("uemail",userProfile.email);
                    editor.apply();
                    bar.dismiss();
                    startActivity(new Intent(getApplicationContext(),ResturantActivity.class));
                   bar.dismiss();
                    finish();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //  bar.dismiss();
                }
            });

        }


        phone=(EditText)findViewById(R.id.lphone);
        btn=(Button) findViewById(R.id.btn_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phone.getText().toString().length()!=10)
                {
                    Toast.makeText(getApplicationContext(),"Enter your 10 digits mobile number",Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i=new Intent(Login.this,OtpVerificationActivity.class);
                    i.putExtra("phone",phone.getText().toString());
                    startActivity(i);
                }
            }
        });
    }
}