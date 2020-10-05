package com.example.menuapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

public class WaitingActivity extends AppCompatActivity {
    CardView ask,repord,cuttlery;
    Button pay,order;
    ImageButton cuti,waiti,repi;
    String table;
    String resturant_id;
    String resturant_name;
    DatabaseReference ref;
    NotiHelper notiHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        notiHelper=new NotiHelper(getApplicationContext());
        table=(String)getIntent().getStringExtra("table");
        resturant_id=(String)getIntent().getStringExtra("resturant_id");
        resturant_name=(String)getIntent().getStringExtra("name");
        ask=(CardView) findViewById(R.id.call_waiter);
        order=(Button) findViewById(R.id.place_more);
        repord=(CardView) findViewById(R.id.report_problem) ;
        cuttlery=(CardView) findViewById(R.id.ask_cutlery) ;
        cuti=(ImageButton)findViewById(R.id.cutimage);
        waiti=(ImageButton)findViewById(R.id.waiterimg);
        repi=(ImageButton)findViewById(R.id.reportimg);
        pay=(Button)findViewById(R.id.pay_bill);
        TextView titler=(TextView) findViewById(R.id.resturant_title);
        titler.setText(resturant_name);
        getSupportActionBar().hide();
        ref= FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications");
        Glide.with(this).load(R.drawable.cuttlery).into(cuti);
        Glide.with(this).load(R.drawable.callwaiter).into(waiti);
        Glide.with(this).load(R.drawable.report).into(repi);
        ImageButton img=(ImageButton)findViewById(R.id.pic);
        Glide.with(this).load(R.drawable.preparing).into(img);
        Notification n=new Notification();
        n.setTable_no(table);
        n.setUser_id(FirebaseAuth.getInstance().getUid());
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                n.setTable_no(table);
                n.setUser_id(FirebaseAuth.getInstance().getUid());
                n.setUser_id("123");
                n.setResturant_id(resturant_id);
                n.setMessage("Want to settle bill");
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                dr.setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Request sent",Toast.LENGTH_LONG).show();
                    }
                });
                try {
                    notiHelper.SendNotification(resturant_id,"Notification","Payment Settle request from table no "+table);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dr=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("notification").child(n.getId());
                dr.setValue(n);
                getSharedPreferences("global",MODE_PRIVATE).edit().clear().commit();
               startActivity(new Intent(getApplicationContext(),ArrivingBillActivity.class));

            }
        });
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                n.setUser_id(FirebaseAuth.getInstance().getUid());
                n.setTable_no(table);
                n.setUser_id("123");
                n.setResturant_id(resturant_id);
                n.setMessage("Calling for waiter");
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                try {
                    notiHelper.SendNotification(resturant_id,"Notification","Waiter call from table no "+table);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dr.setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Request sent",Toast.LENGTH_LONG).show();
                    }
                });
                dr=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("notification").child(n.getId());
                dr.setValue(n);

            }
        });
        cuttlery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                n.setTable_no(table);
                n.setUser_id("123");
                n.setUser_id(FirebaseAuth.getInstance().getUid());
                n.setResturant_id(resturant_id);
                n.setMessage("Asking for Cutlery");
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                try {
                    notiHelper.SendNotification(resturant_id,"Notification","Cutlery request from table no "+table);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dr.setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Request sent",Toast.LENGTH_LONG).show();
                    }
                });
                dr=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("notification").child(n.getId());
                dr.setValue(n);

            }
        });
        repord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                n.setTable_no(table);
                n.setUser_id("123");
                n.setUser_id(FirebaseAuth.getInstance().getUid());
                n.setResturant_id(resturant_id);
                n.setMessage("Reported a problem");
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                try {
                    notiHelper.SendNotification(resturant_id,"Notification","Reporting Issue request from table no "+table);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dr.setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Request sent",Toast.LENGTH_LONG).show();

                    }
                });
                dr=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("notification").child(n.getId());
                dr.setValue(n);
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("table",table);

                i.putExtra("name",resturant_name);
                i.putExtra("resturant_id",resturant_id);
                startActivity(i);
                finish();
            }
        });

    }
}