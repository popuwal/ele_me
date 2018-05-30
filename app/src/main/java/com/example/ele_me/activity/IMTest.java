package com.example.ele_me.activity;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.BaseApplication;
import com.example.ele_me.R;
import com.example.ele_me.adapter.ChatAdapter;
import com.example.ele_me.adapter.RecyclerViewClickListener;
import com.example.ele_me.entity.TestVolleyJson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class IMTest extends Activity  {
    private RecyclerView recyclerView;
    MyHandler myHandler;
    List<List<TestVolleyJson.Data>> tmplistList;

    /**
     * keep to handle sth later.
     */
    private static class MyHandler extends Handler{
        WeakReference<IMTest> activityWeakReference;

        MyHandler(IMTest activity){
            activityWeakReference = new WeakReference<IMTest>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activityWeakReference.get().recyclerView != null){
                if (BaseApplication.DEBUG)Log.e(BaseApplication.TAG, "data is: "+msg.obj);
                activityWeakReference.get().recyclerView.setAdapter(new ChatAdapter((List<TestVolleyJson.Data>) (msg.obj), activityWeakReference.get().getApplicationContext()));
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_test_listview);
        myHandler = new MyHandler(this);
        recyclerView = findViewById(R.id.chat);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutmanager);
        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(),DetailedChatActivity.class);
                List<TestVolleyJson.Data> data = tmplistList.get(position);
                int len = data.size();
                for (int i=0; i<len;i++){
                    intent.putExtra("data"+i, data.get(i));
                }
                intent.putExtra("len", len);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), "Item "+position+" clicked!", Toast.LENGTH_SHORT).show();
            }
        }));
        RequestQueue volley = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://staging.talentslist.com/api/user/23/chat", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String ss = response.toString();
                //Log.e("POPUWAL","onResponse "+ss);
                Gson gson = new Gson();
                TestVolleyJson testVolleyJsons = gson.fromJson(ss, new TypeToken<TestVolleyJson>() {
                }.getType());
                if (BaseApplication.DEBUG)Log.e(BaseApplication.TAG, "testVolleyJson from: " + testVolleyJsons.getData());
                List<TestVolleyJson.Data> data = reDesignListData(testVolleyJsons.getData());
                if (BaseApplication.DEBUG)Log.e(BaseApplication.TAG, "the newest item is: " + data);
                Message message = myHandler.obtainMessage();
                //message.obj = testVolleyJsons.getData(); //for all
                message.obj =data;
                myHandler.sendMessage(message);
                //Log.e("POPUWAL","testVolleyJson from: "+testVolleyJsons.getLinks());
                //Log.e("POPUWAL","testVolleyJson from: "+testVolleyJsons.getMeta());
                //TestVolleyJson testVolleyJson = response..
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (BaseApplication.DEBUG)Log.e(BaseApplication.TAG, "onErrorResponse " + error.toString());
            }
        });
        volley.add(jsonObjectRequest);

        if (getActionBar() != null)
        getActionBar().hide();
    }

    private List<TestVolleyJson.Data> reDesignListData(List<TestVolleyJson.Data> data) {
        List<TestVolleyJson.Data> temp = new LinkedList<TestVolleyJson.Data>();
        tmplistList = new LinkedList<List<TestVolleyJson.Data>>();
        List<String> friends = findFriends(data);
        if (BaseApplication.DEBUG)Log.e(BaseApplication.TAG, "I(23) have friends: "+friends);
        for (String friend: friends) {
            TestVolleyJson.Data tmpData = findTheLeatestItem(friend,data);
            List<TestVolleyJson.Data> listList = findFriendsAndPaiXu(friend,data );
            temp.add(tmpData);
            tmplistList.add(listList);
        }
        return temp;
    }

    private List<TestVolleyJson.Data> findFriendsAndPaiXu(String friend, List<TestVolleyJson.Data> data) {
        List<TestVolleyJson.Data> data1 = new LinkedList<TestVolleyJson.Data>();
        for (TestVolleyJson.Data da:data){
            if ((da.getFrom().equals("23") && friend.equals(da.getTo())) || (da.getFrom().equals(friend) && "23".equals(da.getTo()))){
                data1.add(da);
            }
        }
        Collections.sort(data1, new Comparator<TestVolleyJson.Data>() {
            @Override
            public int compare(TestVolleyJson.Data o1, TestVolleyJson.Data o2) {
                int i = o1.getCreated_at().compareTo(o2.getCreated_at());
                if (i>=0) return 1;
                return i;
            }
        });
        return data1;
    }

    private TestVolleyJson.Data findTheLeatestItem(String friend, List<TestVolleyJson.Data> data) {
        String tempMaxDate = "2014-09-02 11:22:22";
        TestVolleyJson.Data tmpData = null;
        for (TestVolleyJson.Data dat:data) {
            if ((dat.getFrom().equals("23") && friend.equals(dat.getTo())) || (dat.getFrom().equals(friend) && "23".equals(dat.getTo()))){
                if (tempMaxDate.compareTo(dat.getCreated_at())<0){
                    tempMaxDate = dat.getCreated_at();
                    tmpData = dat;
                }
            }
        }
        return tmpData;
    }

    private List<String> findFriends(List<TestVolleyJson.Data> data) {
        List<String> tmpList = new LinkedList<String>();
        for (TestVolleyJson.Data da:data) {
            if (da.getFrom().equals("23")){
                if ( !tmpList.contains(da.getTo()))
                tmpList.add(da.getTo());
            }else {
                if ( !tmpList.contains(da.getFrom()))
                    tmpList.add(da.getFrom());
            }
        }
        return tmpList;
    }
}
