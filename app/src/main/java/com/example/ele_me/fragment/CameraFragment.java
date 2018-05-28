package com.example.ele_me.fragment;

import org.json.JSONObject;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ele_me.R;
import com.example.ele_me.activity.Camera2Activity;
import com.example.ele_me.activity.GalleryActivity;
import com.example.ele_me.activity.IMTest;
import com.example.ele_me.entity.TestVolleyJson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressLint("NewApi")
public class CameraFragment extends Fragment implements OnClickListener {
    private View currentView;
    private LinearLayout openMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        currentView = inflater.inflate(R.layout.slidingpane_camera_layout,
                container, false);
        openMenu = currentView
                .findViewById(R.id.linear_above_toHome);
        openMenu.setOnClickListener(this);
        currentView.findViewById(R.id.btn_camera_gallery).setOnClickListener(
                this);
        currentView.findViewById(R.id.btn_camera_camera).setOnClickListener(
                this);
        currentView.findViewById(R.id.volley).setOnClickListener(
                this);
        RequestQueue volley = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://staging.talentslist.com/api/user/23/chat",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String ss = response.toString();
                Log.e("POPUWAL","onResponse "+ss);
                Gson gson = new Gson();
                List<TestVolleyJson> testVolleyJsons = gson.fromJson(ss, new TypeToken<List<TestVolleyJson>>(){}.getType());
                //TestVolleyJson testVolleyJson = response..
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("POPUWAL","onErrorResponse "+error.toString());
            }
        });
        volley.add(jsonObjectRequest);
        return currentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.linear_above_toHome):
                openMenu.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        final SlidingPaneLayout slidingPaneLayout = getActivity()
                                .findViewById(R.id.slidingpanellayout);
                        if (slidingPaneLayout.isOpen()) {
                            slidingPaneLayout.closePane();
                        } else {
                            slidingPaneLayout.openPane();
                        }
                    }
                });

                break;
            case R.id.btn_camera_gallery:
                startActivity(GalleryActivity.class);
                break;
            case R.id.btn_camera_camera:
                startActivity(Camera2Activity.class);
                break;
            case R.id.volley:
                startActivity(IMTest.class);
                break;
            default:
                break;

        }

    }

    private void startActivity(final Class<?> activityClass) {
        getActivity().startActivity(new Intent(getActivity(), activityClass));
    }
}
