package com.example.menuapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WaitingActivity extends AppCompatActivity {
    CardView ask,repord,cuttlery;
    Button pay,order;
    ImageButton cuti,waiti,repi;
    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("123456789").child("notifications");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        ask=(CardView) findViewById(R.id.call_waiter);
        order=(Button) findViewById(R.id.place_more);
        repord=(CardView) findViewById(R.id.report_problem) ;
        cuttlery=(CardView) findViewById(R.id.ask_cutlery) ;
        cuti=(ImageButton)findViewById(R.id.cutimage);
        waiti=(ImageButton)findViewById(R.id.waiterimg);
        repi=(ImageButton)findViewById(R.id.reportimg);
        pay=(Button)findViewById(R.id.pay_bill);
        getSupportActionBar().hide();
        Glide.with(this).load(R.drawable.cuttlery).into(cuti);
        Glide.with(this).load(R.drawable.callwaiter).into(waiti);
        Glide.with(this).load(R.drawable.report).into(repi);
        ImageButton img=(ImageButton)findViewById(R.id.pic);
        Glide.with(this).load(R.drawable.preparing).into(img);
        Notification n=new Notification();
        n.setTable_no("1");
        n.setUser_id("123");
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                n.setTable_no("1");
                n.setUser_id("123");
                n.setMessage("Want to settle bill");
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                dr.setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Request sent",Toast.LENGTH_LONG).show();
                    }
                });
               startActivity(new Intent(getApplicationContext(),ArrivingBillActivity.class));

            }
        });
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                n.setTable_no("1");
                n.setUser_id("123");
                n.setMessage("Calling for waiter");
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                dr.setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Request sent",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        cuttlery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                n.setTable_no("1");
                n.setUser_id("123");
                n.setMessage("Asking for Cutlery");
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                dr.setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Request sent",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        repord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                n.setTable_no("1");
                n.setUser_id("123");
                n.setMessage("Reported a problem");
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                dr.setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Request sent",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

    }
}