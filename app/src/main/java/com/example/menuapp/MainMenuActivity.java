package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menuapp.Models.FoodType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainMenuActivity extends AppCompatActivity {
    ExpandableListView expandableListView;
    HashMap<String,Cuisine> map=new HashMap<>();
    ArrayList<FoodType> foodTypes=new ArrayList<>();
    String resturant_id="8877009988";
    String resturant_name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
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
                CustomExpandableAdapter customExpandableAdapter=new CustomExpandableAdapter(getApplicationContext(),foodTypes);
                expandableListView.setAdapter(customExpandableAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
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

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Cuisine c=foodTypes.get(groupPosition).getCuisines().get(childPosition);
            View itemView;
//            Toast.makeText(context,c.cousine_name,Toast.LENGTH_LONG).show();
            if(convertView==null)
            {
                 itemView=LayoutInflater.from(context).inflate(R.layout.menuitem,null);
            }
            else
            {
                itemView=convertView;
            }
            TextView name,description,availability;
            ImageView picture;
            Button add;
            RatingBar ratingBar;

            name=(TextView)itemView.findViewById(R.id.cousine_name);
            description=(TextView)itemView.findViewById(R.id.description);
            availability=(TextView)itemView.findViewById((R.id.about));
            picture=(ImageView) itemView.findViewById(R.id.picture);
            add=(Button)itemView.findViewById(R.id.add);
            ratingBar=(RatingBar)itemView.findViewById(R.id.ratingBar);
            name.setText(c.cousine_name);
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

           availability.setText(c.getTimming());
           ratingBar.setRating(5);
            return itemView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}