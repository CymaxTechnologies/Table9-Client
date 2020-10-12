package com.example.menuapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class OrderItemSingleOrderAdapter extends RecyclerView.Adapter<OrderItemSingleOrderAdapter.holder> {
    ArrayList<Cuisine> cuisines;
    ArrayList<Integer> count;
    OrderItemSingleOrderAdapter(ArrayList<Cuisine> cuisines,ArrayList<Integer> count)
    {
        this.cuisines=cuisines;
        this.count=count;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.singlelinetextview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
           String s=Integer.toString(position+1)+" : "+cuisines.get(position).getCousine_name()+" X "+Integer.toString(count.get(position));
           holder.textView.setText(s);
    }

    @Override
    public int getItemCount() {
        return cuisines.size();
    }

    public class holder extends RecyclerView.ViewHolder{
        TextView textView;
        public holder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.order_item);
        }
    }
}
