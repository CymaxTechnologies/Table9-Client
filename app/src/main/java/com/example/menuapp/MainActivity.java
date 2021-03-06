package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.renderscript.RenderScript;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.menuapp.Models.FoodType;
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
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION =1234 ;
    EditText search_edit_text;
    ExpandableListView expandableListView;
    HashMap<String,Cuisine> map=new HashMap<>();
    HashMap<String,Cuisine> allMap=new HashMap<>();
    ArrayList<FoodType> foodTypes=new ArrayList<>();
    ArrayList<FoodType> allFoodTYpes=new ArrayList<>();
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
    CustomExpandableAdapter customExpandableAdapter;
    String user_email;
    String user_phone_no;
    androidx.appcompat.widget.SearchView searchView;
    int count=0;
    ArrayList<Cuisine> all=new ArrayList<>();
    Intent floating_view_service;
    boolean buble_show;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        floating_view_service=new Intent(getApplicationContext(),FloatingViewService.class);
        user_name= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uname","123");
        user_phone_no= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uphone","123");
        user_email= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uemail","123");
        resturant_id= getSharedPreferences("global",MODE_PRIVATE).getString("resturant_id", "123");
        resturant_name=(getSharedPreferences("global",MODE_PRIVATE).getString("name", "123"));
        table=getSharedPreferences("global",MODE_PRIVATE).getString("table", "waiting");
        search_edit_text=(EditText)findViewById(R.id.edt_search) ;
        resturant_title=(TextView)findViewById(R.id.resturant_title);
        resturant_title.setText(resturant_name);
        cartCuisine.clear();
        cartCount.clear();
        tcart.clear();
        tcount.clear();
        buble_show=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("show",true);
        expandableListView=(ExpandableListView)findViewById(R.id.expandablelistview);
        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("cuisines").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot parent:dataSnapshot.getChildren())
                {
                    FoodType foodType=new FoodType();
                    foodType.setName(parent.getKey());

                    for(DataSnapshot child:parent.getChildren())
                    {
                        Cuisine c=child.getValue(Cuisine.class);
                        foodType.getCuisines().add(c);
                    }
                    foodTypes.add(foodType);

                }
                allFoodTYpes.addAll(foodTypes);
                 customExpandableAdapter=new CustomExpandableAdapter(getApplicationContext(),foodTypes);
                expandableListView.setAdapter(customExpandableAdapter);
                expandAll();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                       // Toast.makeText(getApplicationContext(),"You have assigned a table no "+table,Toast.LENGTH_LONG).show();
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
                    customExpandableAdapter.notifyDataSetChanged();

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
                  // finish();



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
        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    customExpandableAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

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

                    .into(holder.picture);
            holder.add.setOnClickListener(new View.OnClickListener() {
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
            ImageView picture;
            Button add;
            RatingBar ratingBar;
            public holder(@NonNull View itemView) {
                super(itemView);

                name=(TextView)itemView.findViewById(R.id.cousine_name);
                description=(TextView)itemView.findViewById(R.id.description);
                availability=(TextView)itemView.findViewById((R.id.about));
                picture=(ImageView) itemView.findViewById(R.id.picture);
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
        //    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("items").setValue(cartCuisine);
          //  FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("count").setValue(cartCount);





        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        menu.getItem(3).setChecked(buble_show);

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
        if(item.getItemId()==R.id.my_bubble)
        {
            if(item.isChecked())

            {
                item.setChecked(false);
                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putBoolean("show",false).apply();
                stopService(floating_view_service);

            }
            else
            {
                item.setChecked(true);
                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putBoolean("show",true).apply();
                startService(floating_view_service);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void startOurService() {
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("my_orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&&buble_show)
                {
                    if(!isMyServiceRunning(FloatingViewService.class))
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                            askPermission();
                        }
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            startService(floating_view_service);
                            // finish();
                        } else if (Settings.canDrawOverlays(MainActivity.this
                        )) {
                            startService(floating_view_service);

                        } else {
                            askPermission();
                            Toast.makeText(MainActivity.this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
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
    }
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
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
    // Before 2.0

    class CustomExpandableAdapter extends BaseExpandableListAdapter
    {
        Context context;
        ArrayList<FoodType> foodTypes;
        CustomExpandableAdapter(Context c,ArrayList<FoodType> l)
        {
            context=c;
            foodTypes=l;
        }
        @Override
        public int getGroupCount() {
            return foodTypes.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return foodTypes.get(groupPosition).getCuisines().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return foodTypes.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return foodTypes.get(groupPosition).getCuisines().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            FoodType foodType=foodTypes.get(groupPosition);
            if(convertView==null)
            {
                convertView= LayoutInflater.from(context).inflate(R.layout.expandablelistviewheader,null);
            }
            TextView textView=convertView.findViewById(R.id.foodtypename);
            textView.setText(foodType.getName());

            return convertView;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Cuisine c=foodTypes.get(groupPosition).getCuisines().get(childPosition);
            View itemView;
            if(groupPosition==foodTypes.size()-1&&childPosition==foodTypes.get(groupPosition).getCuisines().size()-1)
            {

            }
//            Toast.makeText(context,c.cousine_name,Toast.LENGTH_LONG).show();
            if(true)
            {
                itemView=LayoutInflater.from(context).inflate(R.layout.menuitem,null);
            }
            else
            {
                itemView=convertView;
            }
            if(groupPosition==foodTypes.size()-1&&childPosition==foodTypes.get(groupPosition).getCuisines().size()-1)
            {
              itemView.setPadding(0,0,0,100);
            }
            TextView name,description,availability;
            final ImageView picture,veg_non,veg_non_out;
            FrameLayout frameLayout;
            final Button add,remove,text;
            RatingBar ratingBar;
            LinearLayout linearLayout=itemView.findViewById(R.id.edittextlinearlayout);
            frameLayout=itemView.findViewById(R.id.frame);
            veg_non_out=itemView.findViewById(R.id.veg_vector);
            veg_non=itemView.findViewById(R.id.veg_pic);
            name=(TextView)itemView.findViewById(R.id.cousine_name);
            description=(TextView)itemView.findViewById(R.id.description);
            availability=(TextView)itemView.findViewById((R.id.about));
            picture=(ImageView) itemView.findViewById(R.id.picture);
            add=(Button)itemView.findViewById(R.id.add);
            ratingBar=(RatingBar)itemView.findViewById(R.id.ratingBar);
            name.setText(c.cousine_name);
            remove=itemView.findViewById(R.id.subtract);
            text=itemView.findViewById(R.id.textadd);
            if(c.price.equals(c.discount_price))
            {
                description.setText("Rs: "+c.price);
            }
            else
            {
                description.setText("Rs: "+c.price+"   "+c.discount_price,TextView.BufferType.SPANNABLE);

                Spannable spannable = (Spannable) description.getText();
                spannable.setSpan(new StrikethroughSpan(), 4, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            availability.setText(c.about);
            ratingBar.setRating(5);
            ratingBar.setVisibility(View.GONE);
           if(!c.getPicture().equals(""))
           {
               RequestOptions requestOptions = new RequestOptions();
               requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
               Glide.with(getApplicationContext())
                       .load(c.getPicture()).apply(requestOptions)
                       .apply(requestOptions)

                       .into(picture);
               picture.setPadding(0,0,5,0);
               final View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.enlarg_image_layout,null);

               final RequestOptions finalRequestOptions = requestOptions;
               final RequestOptions finalRequestOptions1 = requestOptions;
               picture.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       ImageView gifImageView = new ImageView(MainActivity.this);
                       //T
                       // oast.makeText(getApplicationContext(),c.getPicture(),Toast.LENGTH_LONG).show();
                        final View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.enlarg_image_layout,null);
                       Glide.with(MainActivity.this).load(c.getPicture()).into(gifImageView);

                       AlertDialog.Builder share_dialog = new AlertDialog.Builder(MainActivity.this);
                       share_dialog.setView(view);

                       Window window = share_dialog.create().getWindow();
                       WindowManager.LayoutParams wlp = window.getAttributes();

                       wlp.gravity = Gravity.BOTTOM;
                       wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                       window.setAttributes(wlp);
                       share_dialog.show();
                       Glide.with(MainActivity.this)
                               .asBitmap()
                               .load(c.getPicture())

                               .into(new SimpleTarget<Bitmap>() {
                                   @Override
                                   public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                       ImageView imageView=(ImageView)view.findViewById(R.id.imageView);

                                       imageView.setImageBitmap(resource);
                                   }});

                      // imageView.setImageDrawable(picture.getDrawable());



                   }
               });




               name.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
               veg_non_out.setVisibility(View.GONE);
               name.setWidth(220);
               if(c.veg_nonveg.equals("non_veg"))
               {

                   veg_non_out.setImageResource(R.drawable.nonveg_vector);
               }

               //linearLayout.setLayoutParams(layoutParams);
           }
           else
           {
               if(c.veg_nonveg.equals("non_veg"))
               {

                   veg_non_out.setImageResource(R.drawable.nonveg_vector);
               }
               frameLayout.setVisibility(View.GONE);
           }

            add.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    if(true)
                    {

                        if(cartCuisine.contains(c))
                        {
                            cartCount.set(cartCuisine.indexOf(c),cartCount.get(cartCuisine.indexOf(c))+1);
                            int count=cartCount.get(cartCuisine.indexOf(c));
                            text.setText(Integer.toString(count));
                        }
                        else
                        {
                            cartCuisine.add(c);
                            cartCount.add(1);
                            text.setText("1");
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
           remove.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(!cartCuisine.contains(c))
                   {
                       return;
                   }
                   else
                   {
                       count-=1;
                       int co=cartCount.get(cartCuisine.indexOf(c));
                       if(co==1)
                       {
                           cartCount.remove(cartCuisine.indexOf(c));
                           cartCuisine.remove(c);
                           if(cartCuisine.size()==0)
                           {
                               btncart.setVisibility(View.GONE);
                               text.setText("ADD");
                           }
                       }
                       else
                       {
                           cartCount.set(cartCuisine.indexOf(c),co-1);
                           text.setText(Integer.toString(co-1));
                       }
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

            return itemView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
        void filter(String s)
        {
            foodTypes.clear();
            if(s.equals(""))
            {
                foodTypes.addAll(allFoodTYpes);
            }
            else
            {
                for(FoodType f:allFoodTYpes)
                {
                    FoodType x = null;
                    if(f.getName().toLowerCase().contains(s.toLowerCase()))
                    {
                        foodTypes.add(f);
                        continue;
                    }
                    for(Cuisine c:f.getCuisines())
                    {
                        if(c.getCousine_name().toLowerCase().contains(s.toLowerCase()))
                        {
                            if(x==null)
                            {
                                x=new FoodType();
                                x.setName(c.getCousine_name());
                                x.getCuisines().add(c);

                            }
                            else
                            {
                                x.getCuisines().add(c);
                            }
                        }
                    }
                    if(x!=null)
                    {
                        foodTypes.add(x);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }
    private void expandAll() {
        int count = customExpandableAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            expandableListView.expandGroup(i);
        }
    }

}