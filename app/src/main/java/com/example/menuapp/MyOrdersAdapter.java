package com.example.menuapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.holder> {
    ArrayList<Order> list;
    Context c;
       MyOrdersAdapter(ArrayList<Order> list, Context c)
       {
           this.c=c;
           this.list=list;
       }
    @NonNull
    @Override
    public MyOrdersAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new holder( LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrdersAdapter.holder holder, int position) {
               final Order order=list.get(position);
               holder.resturant.setText(order.getResturant_name());
               holder.total.setText(order.getValue());
               holder.status.setText(order.getStatus());
               holder.recyclerView.setLayoutManager(new LinearLayoutManager(c));
               holder.recyclerView.setHasFixedSize(true);
               holder.recyclerView.setAdapter(new OrderItemSingleOrderAdapter(order.getCuisines(),order.getCount()));
               if(order.getStatus().equals("waiting"))
               {
                   holder.img.setImageResource(R.drawable.waiting_icon);


               }
               else if(order.getStatus().equals("Accepted"))
               {
                 
                   holder.img.setImageResource(R.drawable.accepted_icon);
                   Toast.makeText(c,"Your order has been accepted ",Toast.LENGTH_LONG).show();

               }
               else if(order.getStatus().equals("Cooking"))
               {
                   holder.img.setImageResource(R.drawable.cooking_50);

               }
               else if(order.getStatus().equals("Served"))
               {
                   holder.img.setImageResource(R.drawable.finish_icon);

               }
               else if(order.getStatus().equals("Rejeceted"))
               {
                   holder.img.setImageResource(R.drawable.rejected_icon);
                   Toast.makeText(c,"Your order has been accepted ",Toast.LENGTH_LONG).show();

               }
           holder.cardView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent I=new Intent(c,WaitingActivity.class);
                   I.putExtra("resturant_id",order.getResturant_id());
                   I.putExtra("table",order.getTable());
                   I.putExtra("name",order.getResturant_name());
                   c.startActivity(I);
               }
           });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class holder  extends RecyclerView.ViewHolder{
        TextView resturant,status,total;
        ImageView img;
        RecyclerView recyclerView;
        CardView cardView;
        public holder(@NonNull View itemView) {
            super(itemView);
            resturant=itemView.findViewById(R.id.my_rest_name);
            status=itemView.findViewById(R.id.my_res_status);
            total=itemView.findViewById(R.id.my_res_price);
            img=itemView.findViewById(R.id.my_res_img);
            recyclerView=itemView.findViewById(R.id.my_res_recycler);
            cardView=itemView.findViewById(R.id.order_card);
        }
    }
}
