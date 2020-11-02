package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WaitingActivity extends AppCompatActivity {
    CardView ask,repord,cuttlery;
    Button pay,order;
    ImageButton cuti,waiti,repi;
    String table="waiting";
    String city;
    String resturant_id="";
    String resturant_name="";
    DatabaseReference ref;
    NotiHelper notiHelper;
    String user_name;
    String user_email;
    String user_phone_no;
    String order_id="";
    ImageView order_status_image,table_no_image;
    TextView order_status_textview,table_no_textview;
    RecyclerView recyclerView;
    OrderItemSingleOrderAdapter adapter;
    ArrayList<Order> list=new ArrayList<>();
    Intent floating_view_service;
    Order orderx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        notiHelper=new NotiHelper(getApplicationContext());
        user_name= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uname","123");
        user_phone_no= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uphone","123");
        user_email= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uemail","123");
        order_id=getIntent().getStringExtra("order_id");
        table=(String)getIntent().getStringExtra("table");
        resturant_id=(String)getIntent().getStringExtra("resturant_id");
        resturant_name=(String)getIntent().getStringExtra("name");
        orderx=(Order)getIntent().getSerializableExtra("order") ;
        ask=(CardView) findViewById(R.id.call_waiter);
        order=(Button) findViewById(R.id.place_more);
        repord=(CardView) findViewById(R.id.report_problem) ;
        cuttlery=(CardView) findViewById(R.id.ask_cutlery) ;
        cuti=(ImageButton)findViewById(R.id.cutimage);
        waiti=(ImageButton)findViewById(R.id.waiterimg);
        repi=(ImageButton)findViewById(R.id.reportimg);
        pay=(Button)findViewById(R.id.pay_bill);
        table_no_image=(ImageView)findViewById(R.id.img_table_no) ;
        table_no_textview=(TextView) findViewById(R.id.txt_table_no);
        order_status_image=(ImageView)findViewById(R.id.img_order_status);
        order_status_textview=(TextView)findViewById(R.id.txt_order_status) ;
        recyclerView=(RecyclerView)findViewById(R.id.rcy_items_in_order);

        TextView titler=(TextView) findViewById(R.id.resturant_title);
        titler.setText(resturant_name);
        getSupportActionBar().hide();
        ref= FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications");
        Glide.with(this).load(R.drawable.cuttlery).into(cuti);
        Glide.with(this).load(R.drawable.callwaiter).into(waiti);
        Glide.with(this).load(R.drawable.report).into(repi);
        floating_view_service=new Intent(WaitingActivity.this, FloatingViewService.class);

        Notification n=new Notification();
        n.setTable_no(table);
        n.setUser_id(FirebaseAuth.getInstance().getUid());
        adapter=new OrderItemSingleOrderAdapter(orderx.getCuisines(),orderx.getCount());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String s=dataSnapshot.getValue(String.class);
                    //   Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                    table=s;
                    if(s.equals("not_available"))
                    {
                        table_no_textview.setText("You are not assigned a table by the Resturant");
                        pay.setEnabled(false);
                        pay.setBackgroundColor(Color.GRAY);
                        table_no_image.setImageResource(R.drawable.rejected_icon);

                    }
                    else if(s.equals("waiting"))
                    {
                        table_no_textview.setText("You will be assigned a table soon");
                        table_no_image.setImageResource(R.drawable.waiting_icon);
                        pay.setEnabled(false);
                        pay.setBackgroundColor(Color.GRAY);
                    }
                    else if(Integer.parseInt(s)<1000)
                    {

                        table=s;
                        pay.setEnabled(true);
                        pay.setBackgroundColor(Color.GREEN);
                        table_no_textview.setText("You have been assigned table no "+Integer.parseInt(s));
                        table_no_image.setImageResource(R.drawable.accepted_icon);
                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("my_orders").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    if(!isMyServiceRunning(FloatingViewService.class))
                                    {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(WaitingActivity.this)) {
                                            askPermission();
                                        }
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                            startService(floating_view_service);
                                            // finish();
                                        } else if (Settings.canDrawOverlays(WaitingActivity.this
                                        )) {
                                            startService(floating_view_service);

                                        } else {
                                            askPermission();
                                            Toast.makeText(WaitingActivity.this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                else
                                {
                                    stopService(floating_view_service);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("pending").child(order_id).child("status").addValueEventListener(new ValueEventListener() {
                            @Override

                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    String s=dataSnapshot.getValue(String.class);
                                    if(s.equals("waiting"))
                                    {
                                        order_status_textview.setText("Your order will be accepted soon");
                                        order_status_image.setImageResource(R.drawable.waiting_icon);
                                    }
                                    else if(s.equals("Accepted"))
                                    {
                                        order_status_textview.setText("Your order is Accepted by the resturant");
                                        order_status_image.setImageResource(R.drawable.accepted_icon);
                                    }
                                    else if(s.equals("Cooking"))
                                    {
                                        order_status_textview.setText("Your order is being prepared");
                                        order_status_image.setImageResource(R.drawable.cooking_50);
                                    }
                                    else if(s.equals("Served"))
                                    {
                                        order_status_textview.setText("Your order is served");
                                        order_status_image.setImageResource(R.drawable.served_n);

                                    }
                                    else if(s.equals("Rejeceted"))
                                    {
                                        order_status_textview.setText("Your order is Rejected by the resturant");
                                        order_status_image.setImageResource(R.drawable.rejected_icon);
                                    }


                                }else
                                {
                                    order_status_textview.setText("Your order is not submitted yet");
                                    order_status_image.setImageResource(R.drawable.waiting_icon);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }
                else
                {
                    Intent i=new Intent(getApplicationContext(),ArrivingBillActivity.class);
                    i.putExtra("resturant_id",resturant_id);
                    SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    city=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("city","patiala");
                    i.putExtra("city",city);
                    getSharedPreferences("global",MODE_PRIVATE).edit().clear().commit();


                    startActivity(i);
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        pay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                n.setTable_no(table);
                n.setTime(date.toString());
                n.setUser_id(FirebaseAuth.getInstance().getUid());
                n.setUser_id("123");
                n.setResturant_id(resturant_id);
                n.setMessage("Want to settle bill \n"+"Time : "+dateFormat.format(date)+" by "+user_name);
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                dr.setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Request sent",Toast.LENGTH_LONG).show();
                    }
                });
                try {
                    notiHelper.SendNotification(resturant_id,"Notification","Payment Settle request from table no "+table+"\n Time "+dateFormat.format(date)+" by "+user_name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dr=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("notification").child(n.getId());
                dr.setValue(n);


                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).removeValue();
                Intent i=new Intent(getApplicationContext(),ArrivingBillActivity.class);
                i.putExtra("resturant_id",resturant_id);
                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                city=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("city","patiala");
                i.putExtra("city",city);
                getSharedPreferences("global",MODE_PRIVATE).edit().clear().commit();


                startActivity(i);
               finish();

            }
        });
        waiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                n.setTable_no(table);
                n.setTime(date.toString());
                n.setUser_id(FirebaseAuth.getInstance().getUid());
                n.setTable_no(table);
                n.setUser_id("123");
                n.setResturant_id(resturant_id);
                n.setMessage("Calling for waiter \n"+"Time : "+dateFormat.format(date)+" by "+user_name);
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                try {
                    notiHelper.SendNotification(resturant_id,"Notification","Waiter call from table no "+table+" \nTime "+dateFormat.format(date)+" by "+user_name);
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
        cuti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                n.setTable_no(table);
                n.setTime(date.toString());
                n.setTable_no(table);
                n.setUser_id("123");
                n.setUser_id(FirebaseAuth.getInstance().getUid());
                n.setResturant_id(resturant_id);
                n.setMessage("Asking for Cutlery\n"+"Time : "+dateFormat.format(date)+" by "+user_name);
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                try {
                    notiHelper.SendNotification(resturant_id,"Notification","Cutlery request from table no "+table+"\nTime "+dateFormat.format(date)+" by "+user_name);
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
        repi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification n=new Notification();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                n.setTable_no(table);
                n.setTime(date.toString());
                n.setTable_no(table);
                n.setUser_id("123");
                n.setUser_id(FirebaseAuth.getInstance().getUid());
                n.setResturant_id(resturant_id);
                n.setMessage("Reported a problem \n"+"Time : "+date.toString()+" by "+user_name);
                DatabaseReference dr=ref.push();
                n.setId(dr.getKey());
                try {
                    notiHelper.SendNotification(resturant_id,"Notification","Reporting Issue request from table no "+table+"\n "+dateFormat.format(date)+" by "+user_name);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        super.onBackPressed();

    }
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}