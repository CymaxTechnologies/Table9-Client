package com.example.menuapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Order implements Serializable {
    String order_id,customer_id,resturant_id,check_in_time,check_in_date,value,payment_method,rating,user_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    Order()
    {
        order_id="";
        customer_id="";
        resturant_id="";
        check_in_date="";
        check_in_time="";
        value="0";
        payment_method="";
        rating="";
        user_id="";

    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getResturant_id() {
        return resturant_id;
    }

    public void setResturant_id(String resturant_id) {
        this.resturant_id = resturant_id;
    }

    public String getCheck_in_time() {
        return check_in_time;
    }

    public void setCheck_in_time(String check_in_time) {
        this.check_in_time = check_in_time;
    }

    public String getCheck_in_date() {
        return check_in_date;
    }

    public void setCheck_in_date(String check_in_date) {
        this.check_in_date = check_in_date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getRting() {
        return rating;
    }

    public void setRating(String rasting) {
        this.rating = rasting;
    }

    public String getRating() {
        return rating;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    String table;
    ArrayList<Cuisine> cuisines=new ArrayList<>();
    ArrayList<Integer> count=new ArrayList<>();

    public ArrayList<Cuisine> getCuisines() {
        return cuisines;
    }

    public void setCuisines(ArrayList<Cuisine> cuisines) {
        this.cuisines = cuisines;
    }

    public ArrayList<Integer> getCount() {
        return count;
    }

    public void setCount(ArrayList<Integer> count) {
        this.count = count;
    }
}
