package com.example.ele_me.activity;

import org.json.JSONObject;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ele_me.R;
import com.example.ele_me.adapter.ChatAdapter;
import com.example.ele_me.entity.TestVolleyJson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class IMTest extends Activity {
    private List<TestVolleyJson.Data> dataList;
    private RecyclerView recyclerView;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (recyclerView != null){
                Log.e("POPUWAL", "objobjobj is: "+(List<TestVolleyJson.Data>)msg.obj);
                recyclerView.setAdapter(new ChatAdapter((List<TestVolleyJson.Data>)msg.obj,getApplicationContext()));
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_test_listview);
        recyclerView = findViewById(R.id.chat);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutmanager);
        RequestQueue volley = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://staging.talentslist.com/api/user/23/chat", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String ss = response.toString();
                //Log.e("POPUWAL","onResponse "+ss);
                Gson gson = new Gson();
                TestVolleyJson testVolleyJsons = gson.fromJson(ss, new TypeToken<TestVolleyJson>() {
                }.getType());
                Log.e("POPUWAL", "testVolleyJson from: " + testVolleyJsons.getData());
                dataList = testVolleyJsons.getData();
                Message message = new Message();
                message.obj = dataList;
                handler.sendMessage(message);
                //Log.e("POPUWAL","testVolleyJson from: "+testVolleyJsons.getLinks());
                //Log.e("POPUWAL","testVolleyJson from: "+testVolleyJsons.getMeta());
                //TestVolleyJson testVolleyJson = response..
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("POPUWAL", "onErrorResponse " + error.toString());
            }
        });
        volley.add(jsonObjectRequest);
    }
}
