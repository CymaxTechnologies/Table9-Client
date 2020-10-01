package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menuapp.SendNotificationPack.APIService;
import com.example.menuapp.SendNotificationPack.Client;
import com.example.menuapp.SendNotificationPack.Data;
import com.example.menuapp.SendNotificationPack.MyResponse;
import com.example.menuapp.SendNotificationPack.NotificationSender;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    ArrayList<Cuisine> cart;
    ArrayList<Integer> count;
    int total=0;
    Button cartSend;
    private APIService apiService;
    ProgressDialog progressDialog;
    String table="";
    String resturant_id="";
    String resturant_name="";
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
       // progressDialog.show();
         cartSend=findViewById(R.id.cartSend);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        cart=(ArrayList<Cuisine>)getIntent().getSerializableExtra("cartI");
        count=(ArrayList<Integer>)getIntent().getSerializableExtra("cartC");
        updateButton();
       // getSupportActionBar().hide();
        Toast.makeText(getApplicationContext(),table,Toast.LENGTH_SHORT).show();
        TextView t=(TextView)findViewById(R.id.resturant_title);
        t.setText(resturant_name);
        cartSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = new Order();
                order.setResturant_id(resturant_id);
                order.setTable(table);
                HashMap<Cuisine, Integer> cu = new HashMap<>();
                for (int i = 0; i < cart.size(); i++) {
                    cu.put(cart.get(i), count.get(i));
                }

                order.setCuisines(cart);
                order.setValue(Integer.toString(total));
                order.setCount(count);
                 DatabaseReference key=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("pending").push();
                 order.setOrder_id(key.getKey());

                 key.setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         Toast.makeText(getApplicationContext(),"Order placed Succesfully",Toast.LENGTH_LONG).show();
                        // progressDialog.show();
                         Intent i=new Intent(getApplicationContext(),WaitingActivity.class);
                         i.putExtra("table",table);
                         i.putExtra("name",resturant_name);
                         i.putExtra("resturant_id",resturant_id);
                         startActivity(i);

                         sendNotifications();
                         finish();
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
        for (int i = 0; i <cart.size() ; i++) {
            total+=Integer.parseInt(cart.get(i).getPrice())*count.get(i);
            cartSend.setText("Total Rs "+Integer.toString(total));
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
           holder.price.setText(Integer.toString(co)+" *"+c.price);
           holder.total.setText("Total: " +Integer.toString(Integer.parseInt(c.getPrice())*co));
           holder.remove.setOnClickListener(new View.OnClickListener() {
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

        }

        @Override
        public int getItemCount() {
            return cart.size();
        }
        class holder extends RecyclerView.ViewHolder {
            TextView name,price,total;
            Button remove;
            public holder(@NonNull View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.cart_item_name);
                price=itemView.findViewById(R.id.cart_item_price);
                total=itemView.findViewById(R.id.cart_total);
                remove= itemView.findViewById(R.id.cart_remove);
            }
        }
    }
    public void sendNotifications() {
        final String[] usertoken = new String[1];
        DatabaseReference r=FirebaseDatabase.getInstance().getReference().child("123456789").child("token");
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usertoken[0] =dataSnapshot.getValue(String.class).toString();
               // Toast.makeText(getApplicationContext(),usertoken[0],Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                    Toast.makeText(getApplicationContext(),Integer.toString(response.code()),Toast.LENGTH_LONG).show();
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
}
