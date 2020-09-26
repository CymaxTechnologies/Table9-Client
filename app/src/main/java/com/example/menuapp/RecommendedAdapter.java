package com.example.menuapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.holder> implements Filterable {
    ArrayList<Cuisine> data ;
    Context c;
    public RecommendedAdapter(ArrayList<Cuisine> data, Context c)
    {
        this.data=data;
        this.c=c;
    }
    @NonNull
    @Override
    public RecommendedAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.menuitem, parent, false);
        holder myHolder=new holder(listItem);
        return myHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedAdapter.holder holder, int position) {
                   Cuisine cuisine=data.get(position);
                   holder.name.setText(cuisine.getCousine_name());
                   holder.description.setText(cuisine.getAbout());
                   holder.availability.setText(cuisine.getTimming());
                   holder.ratingBar.setRating(Integer.parseInt(cuisine.getRating()));
    }

    @Override
    public int getItemCount(){

        return data.size();
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Cuisine> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(data);
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

    class holder extends RecyclerView.ViewHolder{
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
}
