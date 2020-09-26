package com.example.menuapp;

import java.io.Serializable;

public class Cuisine implements Serializable {
    Cuisine()
    {

    }
    public String getResturant_id() {
        return resturant_id;
    }

    public void setResturant_id(String resturant_id) {
        this.resturant_id = resturant_id;
    }

    public String getCousine_name() {
        return cousine_name;
    }

    public void setCousine_name(String cousine_name) {
        this.cousine_name = cousine_name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAvailability_dates() {
        return availability_dates;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public Cuisine(String resturant_id, String cousine_name, String ingredients, String about, String picture, String video, String price, String availability_dates, String timming, String no_of_times_ordered, String rating, String discount_price, String offer) {
        this.resturant_id = "";
        this.cousine_name = "";
        this.ingredients = "ingredients";
        this.about = "about";
        this.picture = "picture";
        this.video = "video";
        this.price = "price";
        this.availability_dates = "availability_dates";
        this.timming = "timming";
        this.no_of_times_ordered = "no_of_times_ordered";
        this.rating = "rating";
        this.discount_price = "discount_price";
        this.offer = "offer";
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public void setAvailability_dates(String availibility_dates) {
        this.availability_dates = availibility_dates;
    }

    public String getTimming() {
        return timming;
    }

    public void setTimming(String timming) {
        this.timming = timming;
    }

    public String getNo_of_times_ordered() {
        return no_of_times_ordered;
    }

    public void setNo_of_times_ordered(String no_of_times_ordered) {
        this.no_of_times_ordered = no_of_times_ordered;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    String resturant_id,cousine_name,ingredients,about,picture,video,price,availability_dates,timming,no_of_times_ordered,rating;
    String discount_price,offer;
}

