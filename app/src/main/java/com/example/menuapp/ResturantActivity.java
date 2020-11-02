package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.menuapp.DataBase.RoomDatabase;
import com.example.menuapp.Others.AppExecuter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResturantActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final String DEFAULT_CITY="patiala";
    ArrayList<Resturant> data = new ArrayList<>();
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    ResturantAdapter adapter;
    SearchView searchView;
    String cityx;
    ArrayList<Resturant> alldata = new ArrayList<>();
    String city = "";
    Intent floating_view_service;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resturant);
        final SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
         cityx=sharedPreferences.getString("city","Patiala");
        adapter=new ResturantAdapter();
        floating_view_service=new Intent(ResturantActivity.this, FloatingViewService.class);
        alldata.addAll(data);
        recyclerView=(RecyclerView)findViewById(R.id.resturant_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ResturantActivity.this));
        recyclerView.setAdapter(adapter);
        AppExecuter.getInstance().getDiskio().execute(new Runnable() {
            @Override
            public void run() {
                data=(ArrayList<Resturant>)RoomDatabase.getInstance(ResturantActivity.this).resturantDAO().getAll();
                adapter.notifyDataSetChanged();
                alldata.addAll(data);

            }
        });






        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("my_orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(!isMyServiceRunning(FloatingViewService.class))
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(ResturantActivity.this)) {
                            askPermission();
                        }
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            startService(floating_view_service);
                            // finish();
                        } else if (Settings.canDrawOverlays(ResturantActivity.this
                        )) {
                            startService(floating_view_service);

                        } else {
                            askPermission();
                            Toast.makeText(ResturantActivity.this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
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
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        String resturant_id = getSharedPreferences("global", MODE_PRIVATE).getString("resturant_id", "123");
        if (!resturant_id.equals("123")) {
           // startActivity(new Intent(getApplicationContext(), MainActivity.class));

            //finish();
          //  return;
        }
        searchView = (SearchView) findViewById(R.id.restsearch);
        searchView.setOnQueryTextListener(this);
        progressDialog = new ProgressDialog(ResturantActivity.this);
        progressDialog.setTitle("T9 App");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
      //  progressDialog.show();


                  //  progressDialog.dismiss();
                final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

               if (ActivityCompat.checkSelfPermission(ResturantActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ResturantActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            Geocoder geocoder=new Geocoder(ResturantActivity.this,Locale.getDefault());
                            try {
                                final List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                city=addresses.get(0).getLocality();
                                final ArrayList<Resturant> temp=new ArrayList<>();
                                Toast.makeText(getApplicationContext(), city, Toast.LENGTH_SHORT).show();
                                locationManager.removeUpdates(this);
                            FirebaseDatabase.getInstance().getReference().child("resturants").child(city.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        Resturant r=d.getValue(Resturant.class);
                                        if(r.verified.equals("no"))
                                        {
                                            continue;
                                        }
                                        else
                                        {
                                            temp.add(r);
                                        }

                                    }
                                    data=temp;
                                    SharedPreferences sharedPreferences1=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    sharedPreferences.edit().putString("city",city.toLowerCase());
                                    sharedPreferences.edit().apply();
                                    adapter.notifyDataSetChanged();
                                    AppExecuter.getInstance().getDiskio().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            RoomDatabase.getInstance(getApplicationContext()).resturantDAO().deleAll();
                                            for (Resturant r:data)
                                            {
                                                RoomDatabase.getInstance(getApplicationContext()).resturantDAO().insert(r);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                   // return;

                }
                else

                {

                    final ArrayList<Resturant> temp=new ArrayList<>();

                    FirebaseDatabase.getInstance().getReference().child("resturants").child(DEFAULT_CITY).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                Resturant r=d.getValue(Resturant.class);
                                if(r.verified.equals("no"))
                                {
                                    continue;
                                }
                                else
                                {
                                    temp.add(r);
                                }
                                //   Toast.makeText(getApplicationContext(), data.get(0).getName(), Toast.LENGTH_SHORT).show();
                            }
                            data=temp;
                            adapter.notifyDataSetChanged();
                            SharedPreferences sharedPreferences1=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            sharedPreferences.edit().putString("city","patiala".toLowerCase());
                            sharedPreferences.edit().apply();
                            AppExecuter.getInstance().getDiskio().execute(new Runnable() {
                                @Override
                                public void run() {
                                    RoomDatabase.getInstance(getApplicationContext()).resturantDAO().deleAll();
                                    for (Resturant r:data)
                                    {
                                        RoomDatabase.getInstance(getApplicationContext()).resturantDAO().insert(r);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });                  //  progressDialog.dismiss();
                }







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
            TextView name,address,mobile,category,time,rating;
            CardView cardView;

            ImageView picture;

            public holder(@NonNull View itemView) {
                super(itemView);
                cardView=itemView.findViewById(R.id.cardshowres);
                name=itemView.findViewById(R.id.resturant_name);
                address=itemView.findViewById(R.id.resturant_addres);
                rating=itemView.findViewById(R.id.ratingBar);
                category=itemView.findViewById(R.id.resturant_category);
                time=itemView.findViewById(R.id.time);
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
                String s=String.format("%.1f", resturant.rating);;
                holder.rating.setText(s+"/5");
                holder.category.setText(resturant.category);
                holder.time.setText(resturant.timing);
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        SharedPreferences.Editor editor=getSharedPreferences("global",MODE_PRIVATE).edit();
                        editor.putString("resturant_id",resturant.data_id);
                        editor.putString("name",resturant.name);
                        editor.putString("table","waiting");
                        editor.putString("city",resturant.city.toLowerCase());

                        editor.commit();
                        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        sharedPreferences.edit().putString("city",resturant.city.toLowerCase());
                        sharedPreferences.edit().apply();
                        Toast.makeText(getApplicationContext(),city,Toast.LENGTH_LONG).show();
                        i.putExtra("resturant_id",resturant.data_id);
                        i.putExtra("table","");
                        i.putExtra("name",resturant.name);
                        startActivity(i);
                    }
                });
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getUid());
          //  Glide.with(getApplicationContext())
           //         .load(holder.picture)

              //      .into(holder.picture);
//            Glide.with(getApplicationContext()).load(resturant.image).into(holder.picture);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        if(item.getItemId()==R.id.profile)
        {
            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
        }
        if(item.getItemId()==R.id.myorders)
        {
            startActivity(new Intent(getApplicationContext(),My_Active_Orders_Activity.class));

        }
        return super.onOptionsItemSelected(item);
    }
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
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