package com.example.menuapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotiHelper  {
   static Context c;
   static  NotiHelper notiHelper;
 NotiHelper(Context c)
 {
     this.c=c;
 }
  static   void SendNotification(String to, String title, String body) throws JSONException {
        RequestQueue mreRequestQueue= Volley.newRequestQueue(c);
        JSONObject main = new JSONObject();
        main.put("to", "/topics/"+to);
        JSONObject not = new JSONObject();
        not.put("title", title);
        not.put("body", body);
        main.put("notification", not);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", main, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(c,response.toString(),Toast.LENGTH_SHORT).show();
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(c,error.toString(),Toast.LENGTH_SHORT).show();
            }
        }


        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("authorization", "key=AAAAwEFI5e4:APA91bFSQX3VcCztgTU7cgZ7SnM0XYDTH7wZXtQG4UyU5gJiiNX-6cDXxHJm9KgihoUCtxmxf74pdUYcPyutF0eNi7j7vmuUwo0a-UkY94wxXbpKy8iXg1w8PfJF9zGHmeJ5DGgXDAOy");
                map.put("content-type", "application/json");
                return map;
            }

        };
        // Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
        mreRequestQueue.add(jsonObjectRequest);
    }
}
