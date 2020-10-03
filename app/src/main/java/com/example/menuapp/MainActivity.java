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

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    ArrayList<Cuisine> data=new ArrayList<>();
    ArrayList<Cuisine> cartCuisine=new ArrayList<>();
    ArrayList<Integer> cartCount=new ArrayList<>();
    Button btncart;
    String table="";
    String resturant_id="";
    String resturant_name;
    androidx.appcompat.widget.SearchView searchView;
    int count=0;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resturant_id=(String)getIntent().getStringExtra("resturant_id");
        table=(String)getIntent().getStringExtra("table");
        resturant_name=(String)getIntent().getStringExtra("name") ;
        searchView=(androidx.appcompat.widget.SearchView )findViewById(R.id.search) ;
       if(table.equals(""))
       {
           AlertDialog.Builder alert = new AlertDialog.Builder(this);
           alert.setCancelable(false);
           alert.setTitle("T9 App");
           alert.setMessage("Please Enter Table no");

// Set an EditText view to get user input
           final EditText input = new EditText(this);

           alert.setView(input);

           alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int whichButton) {

                   table=input.getText().toString().trim();
               }
           });

           alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int whichButton) {
                   // Canceled.
                   onCreate(savedInstanceState);
               }
           });

           alert.show();

       }
          getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_24);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recommended);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(resturant_id).child("Cuisine");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    Cuisine co=d.getValue(Cuisine.class);
                    data.add(co);
                }
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
                Intent i=new Intent(getApplicationContext(),CartActivity.class);
                i.putExtra("table",table);
                i.putExtra("resturant_id",resturant_id);
                i.putExtra("name",resturant_name);
                i.putExtra("cartI",cartCuisine);
                i.putExtra("cartC",cartCount);
                try {
                    new NotiHelper(getApplicationContext()).SendNotification(resturant_id,"A new Order","A new Order from table no "+table);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(i);
                finish();

            }
        });

        if (cartCuisine.size() == 0) {
            btncart.setVisibility(View.GONE);
        }

        Toast.makeText(getApplicationContext(), Integer.toString(data.size()), Toast.LENGTH_LONG).show();
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
        ArrayList<Cuisine> all=new ArrayList<>();

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
            holder.description.setText(cuisine.getAbout());
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
                    for (Cuisine  item : data) {
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
        cartCuisine.clear();
        cartCount.clear();
        count=0;
        btncart.setVisibility(View.GONE);
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
        return super.onOptionsItemSelected(item);
    }
}