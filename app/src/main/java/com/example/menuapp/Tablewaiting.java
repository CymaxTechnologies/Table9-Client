package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.EventListener;

public class Tablewaiting extends AppCompatActivity {
    String resturant_id,name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablewaiting);
        getSupportActionBar().hide();
        resturant_id=(String)getIntent().getStringExtra("resturant_id");
        name=(String)getIntent().getStringExtra("name");
        Notification notification=new Notification();
        notification.setUser_id(FirebaseAuth.getInstance().getUid());
        notification.setResturant_id(resturant_id);
        notification.setMessage("New Client");
        DatabaseReference drf=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("new_arrivals").push();
        notification.setId(drf.getKey());
        drf.setValue(notification);
        try {
           new NotiHelper(Tablewaiting.this).SendNotification(resturant_id,"New Arrival","Customer is waiting for table \n"+"Customer : "+FirebaseAuth.getInstance().getUid());
            Toast.makeText(getApplicationContext(),"Your request is sent",Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.hasChildren())
               {
                   if(dataSnapshot.getValue(String.class).equals("not_available"))
                   {
                       Toast.makeText(getApplicationContext(),"No Any table assigned",Toast.LENGTH_LONG).show();
                       startActivity(new Intent(getApplicationContext(),MainActivity.class));
                   }else
                   {
                       String table=dataSnapshot.getValue(String.class);
                       Toast.makeText(getApplicationContext(),"You table no is "+table,Toast.LENGTH_LONG).show();
                       Intent i=new Intent(getApplicationContext(),MainActivity.class);
                       i.putExtra("resturant_id",resturant_id);
                       i.putExtra("name",name);
                       i.putExtra("table",table);
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}