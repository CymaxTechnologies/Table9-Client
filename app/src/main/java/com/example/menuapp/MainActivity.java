package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.RenderScript;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    ArrayList<Cuisine> data=new ArrayList<>();
    ArrayList<Cuisine> cartCuisine=new ArrayList<>();
    ArrayList<Integer> cartCount=new ArrayList<>();
    ArrayList<Cuisine> tcart=new ArrayList<>();
    ArrayList<Integer> tcount=new ArrayList<>();
    TextView resturant_title;
    Button btncart;
    String table="";
    String resturant_id="";
    String resturant_name;
    String user_name;
    String user_email;
    String user_phone_no;
    androidx.appcompat.widget.SearchView searchView;
    int count=0;
    ArrayList<Cuisine> all=new ArrayList<>();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        cartCuisine.clear();
        cartCount.clear();
        tcart.clear();
        tcount.clear();
        user_name= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uname","123");
        user_phone_no= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uphone","123");
        user_email= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uemail","123");
        resturant_id= getSharedPreferences("global",MODE_PRIVATE).getString("resturant_id", "123");
        resturant_name=(getSharedPreferences("global",MODE_PRIVATE).getString("name", "123"));
        table=getSharedPreferences("global",MODE_PRIVATE).getString("table", "waiting");
        searchView=(androidx.appcompat.widget.SearchView )findViewById(R.id.search) ;
        resturant_title=(TextView)findViewById(R.id.resturant_title);
        resturant_title.setText(resturant_name);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();
            }
        });

          getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_24);
                  FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String s=dataSnapshot.getValue(String.class);
                 //   Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                    if(s.equals("not_available"))
                    {
                        Toast.makeText(getApplicationContext(),"No Any table assigned",Toast.LENGTH_LONG).show();
                        ;

                    }
                    else if(s.equals("waiting"))
                    {

                    }
                    else if(Integer.parseInt(s)<1000)
                    {
                        String table=dataSnapshot.getValue(String.class);
                        getSupportActionBar().setTitle("Table no "+table);
                        Toast.makeText(getApplicationContext(),"You have assigned a table no "+table,Toast.LENGTH_LONG).show();
                        //   startActivity(new Intent(getApplicationContext(),ResturantActivity.class));

                        return;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_menu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    tcart.clear();
                   for(DataSnapshot d:dataSnapshot.getChildren())
                   {
                       tcart.add(d.getValue(Cuisine.class));


                   }

                    //count+=cartCuisine.size();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    cartCount.clear();
                    cartCuisine.clear();
                    count=0;
                    tcount.clear();
                    for(DataSnapshot d:dataSnapshot.getChildren())
                    {
                        tcount.add(d.getValue(Integer.class));
                    }
                    for(int i:tcount)
                    {
                        count+=i;
                    }
                    cartCuisine.addAll(tcart);
                    cartCount.addAll(tcount);
                    btncart.setVisibility(View.VISIBLE);
                    btncart.setText(Integer.toString(count)+" Items in Cart");


                }
                else
                {
                    cartCount.clear();
                    cartCuisine.clear();
                    btncart.setVisibility(View.GONE);
                    count=0;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(resturant_id).child("Cuisine");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    Cuisine co=d.getValue(Cuisine.class);
                    data.add(co);

                }
                all.addAll(data);
                recyclerView.setAdapter(new RecommendedAdapter());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btncart = (Button) findViewById(R.id.cartbutton);
        btncart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(true)
               {
                   Intent i=new Intent(getApplicationContext(),CartActivity.class);
                   i.putExtra("table",table);
                   i.putExtra("resturant_id",resturant_id);
                   i.putExtra("name",resturant_name);
                   i.putExtra("cartI",cartCuisine);
                   i.putExtra("cartC",cartCount);
                  // FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("items").setValue(cartCuisine);
                   //FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("count").setValue(cartCount);
                   finish();



                   startActivity(i);
                  // finish();

               }
            }
        });

        if (cartCuisine.size() == 0) {
            btncart.setVisibility(View.GONE);
        }

     //
        //   Toast.makeText(getApplicationContext(), Integer.toString(data.size()), Toast.LENGTH_LONG).show();
        final RecommendedAdapter adapter = new RecommendedAdapter();

        RecyclerView mainmenu = (RecyclerView) findViewById(R.id.main_menu);
        final RecommendedAdapter adp = new RecommendedAdapter();
        mainmenu.setHasFixedSize(true);
        mainmenu.setLayoutManager(new LinearLayoutManager(this));
        mainmenu.setAdapter(adp);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView .OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                adp.getFilter().filter(newText);
                return true;
            }
        });

    }


    public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.holder> implements Filterable{

        Context c;


        @NonNull
        @Override
        public RecommendedAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.menuitem, parent, false);
            RecommendedAdapter.holder myHolder=new RecommendedAdapter.holder(listItem);
            return myHolder;

        }

        @Override
        public void onBindViewHolder(final RecommendedAdapter.holder holder, int position) {
            final Cuisine cuisine=data.get(position);
            holder.name.setText(cuisine.getCousine_name());
            if(cuisine.price.equals(cuisine.discount_price))
            {
                holder.description.setText("Rs: "+cuisine.price);
            }
            else
            {
                holder.description.setText("Rs: "+cuisine.price+"   "+cuisine.discount_price,TextView.BufferType.SPANNABLE);

                Spannable spannable = (Spannable) holder.description.getText();
                spannable.setSpan(new StrikethroughSpan(), 4, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            holder.availability.setText(cuisine.getTimming());
            holder.ratingBar.setRating(5);
            Glide.with(getApplicationContext())
                    .load(cuisine.getPicture())

                    .into(holder.picture);         holder.add.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    if(true)
                    {
                       // holder.add.setText("");
                        if(cartCuisine.contains(cuisine))
                        {
                            cartCount.set(cartCuisine.indexOf(cuisine),cartCount.get(cartCuisine.indexOf(cuisine))+1);
                        }
                        else
                        {
                            cartCuisine.add(cuisine);
                            cartCount.add(1);
                        }
                       // cart.put(cuisine,cart.getOrDefault(cuisine,0)+1);
                        //holder.add.setText(Integer.toString(cart.get(cuisine))+" selected");
                        count+=1;
                        if (cartCuisine.size() == 0) {
                            btncart.setVisibility(View.GONE);
                        }
                        else
                        {
                            btncart.setVisibility(View.VISIBLE);
                            btncart.setText(Integer.toString(count)+" Items in Cart");
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount(){

            return data.size();
        }
        class holder extends RecyclerView.ViewHolder  {
            TextView name,description,availability;
            ImageButton picture;
            Button add;
            RatingBar ratingBar;
            public holder(@NonNull View itemView) {
                super(itemView);

                name=(TextView)itemView.findViewById(R.id.cousine_name);
                description=(TextView)itemView.findViewById(R.id.description);
                availability=(TextView)itemView.findViewById((R.id.about));
                picture=(ImageButton)itemView.findViewById(R.id.picture);
                add=(Button)itemView.findViewById(R.id.add);
                ratingBar=(RatingBar)itemView.findViewById(R.id.ratingBar);


            }
                  }
        private Filter exampleFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Cuisine> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(all);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Cuisine  item : all) {
                        if (item.getCousine_name().toLowerCase().contains(filterPattern) ||item.getAbout().toLowerCase().contains(filterPattern)) {
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
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        if(count>0)
        {
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("items").setValue(cartCuisine);
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("count").setValue(cartCount);





        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }
        if(item.getItemId()==R.id.profile)
        {
            startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));

        }
        if(item.getItemId()==R.id.myorders)
        {
            startActivity(new Intent(getApplicationContext(),My_Active_Orders_Activity.class));

        }
        return super.onOptionsItemSelected(item);
    }


    // Before 2.0



}