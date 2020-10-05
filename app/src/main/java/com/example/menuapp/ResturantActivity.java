package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResturantActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ArrayList<Resturant> data=new ArrayList<>();
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    ResturantAdapter adapter;
    SearchView searchView;
    ArrayList<Resturant> alldata=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resturant);
        getSupportActionBar().setTitle("Select Resturant");
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        String resturant_id=getSharedPreferences("global",MODE_PRIVATE).getString("resturant_id","123");
        if(!resturant_id.equals("123"))
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));

            finish();
            return;
        }
        searchView=(SearchView)findViewById(R.id.restsearch) ;
        searchView.setOnQueryTextListener(this);
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
                adapter=new ResturantAdapter();
                alldata.addAll(data);
                 recyclerView=(RecyclerView)findViewById(R.id.resturant_list);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(ResturantActivity.this));
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                     progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        adapter.getFilter().filter(newText);
        return false;
    }

    class ResturantAdapter extends RecyclerView.Adapter<ResturantAdapter.holder> implements Filterable
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
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getUid());
          //  Glide.with(getApplicationContext())
           //         .load(holder.picture)

              //      .into(holder.picture);
//            Glide.with(getApplicationContext()).load(resturant.image).into(holder.picture);
                holder.check_in.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getApplicationContext(),Tablewaiting.class);
                        i.putExtra("resturant_id",resturant.data_id);
                        i.putExtra("table","");
                        i.putExtra("name",resturant.name);
                        startActivity(i);
                        finish();
                    }
                });
                holder.locate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holder.locate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Float.parseFloat(resturant.latitude), Float.parseFloat(resturant.longitude));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }
                });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        private Filter exampleFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Resturant> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0||constraint=="") {
                    filteredList.addAll(alldata);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Resturant  item : alldata) {
                        if (item.name.toLowerCase().contains(filterPattern) ||item.category.toLowerCase().contains(filterPattern)||item.city.toLowerCase().contains(filterPattern)||item.address.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data.clear();
                data.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };

        @Override
        public Filter getFilter() {
            return exampleFilter;
        }
    }
}