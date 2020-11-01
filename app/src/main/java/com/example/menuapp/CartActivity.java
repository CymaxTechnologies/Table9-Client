package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menuapp.SendNotificationPack.APIService;
import com.example.menuapp.SendNotificationPack.Client;
import com.example.menuapp.SendNotificationPack.Data;
import com.example.menuapp.SendNotificationPack.MyResponse;
import com.example.menuapp.SendNotificationPack.NotificationSender;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    ArrayList<Cuisine> cart;
    ArrayList<Integer> count;
    int total=0;
    Button text;
    Button cartSend;
    private APIService apiService;
    ProgressDialog progressDialog;
    String table="waiting";
    String resturant_id="";
    String resturant_name="";
    UserProfile profile=new UserProfile();
    String user_name;
    String user_email;
    String user_phone_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        table=(String)getIntent().getStringExtra("table");
        resturant_id=(String)getIntent().getStringExtra("resturant_id");
        resturant_name=(String)getIntent().getStringExtra("name") ;
        progressDialog=new ProgressDialog(CartActivity.this);
        progressDialog.setTitle("T9 App");
        progressDialog.setMessage("Please wait...");
        user_name= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uname","123");
        user_phone_no= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uphone","123");
        user_email= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uemail","123");

        // progressDialog.show();
        cartSend=(Button)findViewById(R.id.cartSend);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        cart=(ArrayList<Cuisine>)getIntent().getSerializableExtra("cartI");
        count=(ArrayList<Integer>)getIntent().getSerializableExtra("cartC");
        final Notification notification=new Notification();
        notification.setUser_id(FirebaseAuth.getInstance().getUid());
        notification.setResturant_id(resturant_id);
        text=(Button) findViewById(R.id.text);

        notification.setMessage("New Client request from user "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        if(!table.equals("")&&table!=null)
        {
            getSupportActionBar().setTitle("Table no "+table);
        }

        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                   /* final DatabaseReference drf=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("new_arrivals").push();
                    notification.setId(drf.getKey());

                    drf.setValue(notification);
                    try {
                        String s=user_name+" is waiting for table \n"+user_phone_no+"\n"+user_email;
                        new NotiHelper(CartActivity.this).SendNotification(resturant_id,"A new Custmer",s);
                        Toast.makeText(getApplicationContext(),"Your request is sent",Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
             FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(FirebaseAuth.getInstance().getUid()).setValue("waiting");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateButton();
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
                        Toast.makeText(getApplicationContext(),"No Any table assigned",Toast.LENGTH_LONG).show();
                        ;

                    }
                    else if(s.equals("waiting"))
                    {

                    }
                    else if(Integer.parseInt(s)<1000)
                    {
                        table=dataSnapshot.getValue(String.class);
                        getSupportActionBar().setTitle("Table no "+table);
                        Toast.makeText(getApplicationContext(),"You have assigned a table no "+table,Toast.LENGTH_LONG).show();
                        //   startActivity(new Intent(getApplicationContext(),ResturantActivity.class));

                        //return;
                        List<Order> pendings=new ArrayList<>();
                       FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("pending_orders").child(resturant_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot d:dataSnapshot.getChildren())
                                {
                                    Order o=dataSnapshot.getValue(Order.class);


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       // getSupportActionBar().hide();
        Toast.makeText(getApplicationContext(),table,Toast.LENGTH_SHORT).show();
        TextView t=(TextView)findViewById(R.id.resturant_title);
        t.setText(resturant_name);
        cartSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Notification notification=new Notification();
                notification.setUser_id(FirebaseAuth.getInstance().getUid());
                notification.setResturant_id(resturant_id);
                notification.setMessage("New Client");

                if(table.equals("waiting"))
                {
                    if(cart.size()==0)
                    {
                        Toast.makeText(getApplicationContext(),"0 orders in cart",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        finish();
                        return;
                    }
                    Order order = new Order();
                    order.setResturant_id(resturant_id);
                    order.setTable(table);
                    HashMap<Cuisine, Integer> cu = new HashMap<>();
                    for (int i = 0; i < cart.size(); i++) {
                        cu.put(cart.get(i), count.get(i));
                    }

                    String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                    order.setResturant_name(resturant_name);
                    order.setCheck_in_time(timeStamp);
                    order.setStatus("waiting");
                    order.setCustomer_id(FirebaseAuth.getInstance().getUid());
                    order.setUser_id(FirebaseAuth.getInstance().getUid());
                    order.setCuisines(cart);
                    order.setValue(Integer.toString(total));
                    order.setCount(count);
                     DatabaseReference dbr=        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("outside").child(FirebaseAuth.getInstance().getUid()).push();
                     order.setOrder_id(dbr.getKey());
                    Toast.makeText(getApplicationContext(),"Please wait you are not assigned a table",Toast.LENGTH_LONG).show();
                    DatabaseReference drf=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("new_arrivals").child(FirebaseAuth.getInstance().getUid());
                    String s=user_name+" is waiting for table \n"+user_phone_no+"\n"+user_email;
                    notification.setMessage(s);
                    drf.setValue(notification);
                    notification.setId(FirebaseAuth.getInstance().getUid());
                    try {
                         s=user_name+" is waiting for table \n"+user_phone_no+"\n"+user_email;
                        new NotiHelper(CartActivity.this).SendNotification(resturant_id,"A new Customer",s);                        Toast.makeText(getApplicationContext(),"Your request is sent",Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final boolean[] flag = {false};
                   // FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(FirebaseAuth.getInstance().getUid()).setValue("waiting");
                   DatabaseReference db= FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications").child(FirebaseAuth.getInstance().getUid());
                   notification.setId(db.getKey());
                    db.setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            flag[0] =true;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =true;

                        }
                    });
                    dbr.setValue(order);
                    Intent i=new Intent(getApplicationContext(),WaitingActivity.class);
                    i.putExtra("table",table);
                    i.putExtra("name",resturant_name);
                    i.putExtra("resturant_id",resturant_id);
                    i.putExtra("order_id",order.getOrder_id());
                    startActivity(i);
                    finish();
                   return ;
                }
                else if(table.equals("not_available"))
                {
                    Toast.makeText(getApplicationContext(),"No table Available",Toast.LENGTH_LONG).show();
                   return;
                }
                else  if(cart.size()==0)
                {
                    Toast.makeText(getApplicationContext(),"Please add Items into cart",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();

                    return;

                }

                final Order order = new Order();
                order.setResturant_id(resturant_id);
                order.setTable(table);
                HashMap<Cuisine, Integer> cu = new HashMap<>();
                for (int i = 0; i < cart.size(); i++) {
                    cu.put(cart.get(i), count.get(i));
                }

                String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                order.resturant_name=resturant_name;
                order.setCheck_in_time(timeStamp);
                order.setStatus("waiting");
                order.setCustomer_id(FirebaseAuth.getInstance().getUid());
                order.setUser_id(FirebaseAuth.getInstance().getUid());
                order.setCuisines(cart);
                order.setValue(Integer.toString(total));
                order.setCount(count);
                DatabaseReference key=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("pending").push();
                 order.setOrder_id(key.getKey());
                 final ProgressDialog progressDialog=new ProgressDialog(CartActivity.this);
                 progressDialog.setTitle("T9 App");
                 progressDialog.setMessage("Please wait.....");
                 progressDialog.show();
                 FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("my_orders").child(resturant_id).child(order.order_id).setValue(order);
                 key.setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         Toast.makeText(getApplicationContext(),"Order placed Succesfully",Toast.LENGTH_LONG).show();
                        // progressDialog.show();
                         FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("user").setValue(FirebaseAuth.getInstance().getUid());
                         FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).removeValue();
                         Intent i=new Intent(getApplicationContext(),WaitingActivity.class);
                         i.putExtra("table",table);
                         i.putExtra("name",resturant_name);
                         i.putExtra("resturant_id",resturant_id);
                         i.putExtra("order_id",order.getOrder_id());

                         Notification n=new Notification();
                         notification.setUser_id(FirebaseAuth.getInstance().getUid());
                         notification.setTime(new Date().toString());
                         Toast.makeText(getApplicationContext(),"Please wait while resturant accepts your order",Toast.LENGTH_LONG).show();
                         DatabaseReference dbr=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications").push();
                         notification.setId(dbr.getKey());
                         notification.setTable_no(table);
                        // Toast.makeText(getApplicationContext(),"Getting "+table,Toast.LENGTH_LONG).show();
                         notification.setMessage("\nNew Order at "+table+" by "+user_name+"\n"+user_phone_no);
                         dbr.setValue(notification);
                         FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).removeValue();

                         i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         startActivity(i);
                         String s="\nNew Order by "+user_name+"\n"+user_phone_no;
                         try {
                             new NotiHelper(CartActivity.this).SendNotification(resturant_id,"A new Order",s);
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                       //  sendNotifications();
                         finish();
                         progressDialog.dismiss();
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                         progressDialog.dismiss();
                     }
                 });

            }});
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.cart_items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter=new CartAdapter();
        recyclerView.setAdapter(adapter);

    }
    void  updateButton()
    {
        total=0;
        if(cart.size()==0)
        {
            text.setText("Rs "+Integer.toString(total));
            finish();
            return;
        }
        for (int i = 0; i <cart.size() ; i++) {
            total+=Integer.parseInt(cart.get(i).getPrice())*count.get(i);
            text.setText("Rs "+Integer.toString(total));
        }

    }
    class CartAdapter extends RecyclerView.Adapter<CartAdapter.holder> {

        @NonNull
        @Override
        public CartAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CartAdapter.holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cartitem,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull CartAdapter.holder holder, final int position) {
           Cuisine c=cart.get(position);
           final int co=count.get(position);
           holder.name.setText(c.cousine_name);
           holder.price.setText("Rs: " +c.price);
           holder.total.setText("Rs: " +Integer.toString(Integer.parseInt(c.getPrice())*co));
           holder.add.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(co==1)
                   {
                       cart.remove(position);
                       count.remove(position);
                   }
                   else
                   {
                       count.remove(position);
                       count.add(position,co-1);
                   }
                   updateButton();
                   notifyDataSetChanged();

               }
           });
         holder.remove.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 count.remove(position);
                 count.add(position,co+1);
                 notifyDataSetChanged();
             }
         });
        }

        @Override
        public int getItemCount() {
            return cart.size();
        }
        class holder extends RecyclerView.ViewHolder {
            TextView name,price,total,add;
            Button remove;
            public holder(@NonNull View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.cart_item_name);
                price=itemView.findViewById(R.id.cart_item_price);
                total=itemView.findViewById(R.id.cart_total);
                remove= itemView.findViewById(R.id.add);
                add=itemView.findViewById(R.id.subtract);

            }
        }
    }
    public void sendNotifications() {
        final String[] usertoken = new String[1];


        Data data = new Data("New Order", "New Order from table 1");
        NotificationSender sender = new NotificationSender(data, usertoken[0]);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(CartActivity.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
                else
                {
                  //  Toast.makeText(getApplicationContext(),Integer.toString(response.code()),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                          Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(cart.size()==0)
            {
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).removeValue();
                //FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("count").removeValue();

            }
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("items").setValue(cart);
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("cart").child(resturant_id).child("count").setValue(count);
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();




        }

        return super.onKeyDown(keyCode, event);
    }
}
