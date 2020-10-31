package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ArrivingBillActivity extends AppCompatActivity {
    ImageButton img;
    Button btn;
    RatingBar ratingBar;
    String resturant_id,city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arriving_bill);
        img=(ImageButton)findViewById(R.id.wait);
        resturant_id=(String)getIntent().getStringExtra("resturant_id") ;
        city=(String)getIntent().getStringExtra("city");
        Toast.makeText(getApplicationContext(),resturant_id+" "+city,Toast.LENGTH_LONG).show();
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);;
        ratingBar.setRating(5);
        ratingBar.setNumStars(5);
        getSupportActionBar().hide();
        Button logout=(Button)findViewById(R.id.btnlogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog=showCustomDialog();
                final float  rat=ratingBar.getRating();
                final int[] c = {0};
                final float[] r = {0};
                FirebaseDatabase.getInstance().getReference().child("resturants").child(city).child(resturant_id).child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        c[0] =dataSnapshot.getValue(Integer.class);
                        FirebaseDatabase.getInstance().getReference().child("resturants").child(city).child(resturant_id).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                r[0] =dataSnapshot.getValue(Float.class);
                                //c[0]+=1;
                                r[0]=((r[0]*c[0])+rat)/(c[0]+1);
                                c[0]+=1;
                                FirebaseDatabase.getInstance().getReference().child("resturants").child(city).child(resturant_id).child("count").setValue(c[0]);
                                FirebaseDatabase.getInstance().getReference().child("resturants").child(city).child(resturant_id).child("rating").setValue(r[0]);

                                try {
                                    Thread.sleep(3000);
                                    alertDialog.dismiss();
                                    finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }finally {
                                    alertDialog.dismiss();
                                    finish();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                  alertDialog.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ResturantActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();

    }
    private AlertDialog showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.thankyoudialogue, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(ArrivingBillActivity.this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }

}