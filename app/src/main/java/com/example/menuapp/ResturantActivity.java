package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResturantActivity extends AppCompatActivity {
    ArrayList<Resturant> data=new ArrayList<>();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resturant);
        getSupportActionBar().setTitle("Select Resturant");
        FirebaseMessaging.getInstance().subscribeToTopic("all");


        progressDialog=new ProgressDialog(ResturantActivity.this);
        progressDialog.setTitle("T9 App");
        progressDialog.setMessage("Please wait...");
         progressDialog.show();
        FirebaseDatabase.getInstance().getReference().child("resturants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    data.add(d.getValue(Resturant.class));
                    Toast.makeText(getApplicationContext(),   data.get(0).getName(),Toast.LENGTH_SHORT).show();
                }
                RecyclerView recyclerView=(RecyclerView)findViewById(R.id.resturant_list);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(ResturantActivity.this));
                recyclerView.setAdapter(new ResturantAdapter());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                     progressDialog.dismiss();
            }
        });
    }
    class ResturantAdapter extends RecyclerView.Adapter<ResturantAdapter.holder>
    {
        class  holder extends RecyclerView.ViewHolder
        {
            TextView name,address,mobile,category;
            Button check_in,locate;
            ImageView picture;

            public holder(@NonNull View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.resturant_name);
                address=itemView.findViewById(R.id.resturant_addres);
                mobile=itemView.findViewById(R.id.resturant_contact);
                category=itemView.findViewById(R.id.resturant_category);
                check_in=itemView.findViewById(R.id.resturant_checkin);
                locate=itemView.findViewById(R.id.resturant_locate);
                picture=itemView.findViewById(R.id.picture);
            }
        }
        @NonNull
        @Override
        public ResturantAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.resturant_card, parent, false);
            ResturantAdapter.holder myHolder=new holder(listItem);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ResturantAdapter.holder holder, int position) {
                final Resturant resturant=data.get(position);
                holder.name.setText(resturant.name);
                holder.address.setText(resturant.address);
                holder.mobile.setText(resturant.contact);
                holder.category.setText(resturant.category);
          //  Glide.with(getApplicationContext())
           //         .load(holder.picture)

              //      .into(holder.picture);
//            Glide.with(getApplicationContext()).load(resturant.image).into(holder.picture);
                holder.check_in.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        i.putExtra("resturant_id",resturant.data_id);
                        i.putExtra("table","");
                        i.putExtra("name",resturant.name);
                        startActivity(i);
                    }
                });
                holder.locate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}