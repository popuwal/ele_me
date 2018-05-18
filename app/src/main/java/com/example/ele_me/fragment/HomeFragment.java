package com.example.ele_me.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.ele_me.R;
import com.example.ele_me.activity.DianPingWebActivity;
import com.example.ele_me.activity.RestaurantDetailActivity;
import com.example.ele_me.adapter.HomePageRestaurantAdapter;
import com.example.ele_me.entity.RestaurantEntity;
import com.example.ele_me.util.RefreshableListView;
import com.example.ele_me.util.RefreshableListView.OnRefreshListener;

@SuppressLint("NewApi")
public class HomeFragment extends Fragment implements OnClickListener {
    private View currentView;
    private LinearLayout openMenu;
    private RefreshableListView mListView;
    private HomePageRestaurantAdapter adapter;
    private List<RestaurantEntity> mlist;
    private int total = 21;
    private int step = 7;
    private int add = 7;
    private View listHeaderView;
    private ImageView head_pic;
    /* popuwal 20180518 Add location start*/
    private static String TAG = "POPUWAL";
    private TextView textView;
    private LocationManager locationManager;
    LocationListener listener;
    Geocoder geocoder;
    /* popuwal 20180518 Add location end*/

    public void setCurrentViewPararms(FrameLayout.LayoutParams layoutParams) {
        currentView.setLayoutParams(layoutParams);
    }

    public FrameLayout.LayoutParams getCurrentViewParams() {
        return (LayoutParams) currentView.getLayoutParams();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        currentView = inflater.inflate(R.layout.slidingpane_home_layout, container, false);
        mListView = currentView.findViewById(R.id.mineList);
        openMenu = currentView.findViewById(R.id.linear_above_toHome);
        listHeaderView = getActivity().getLayoutInflater().inflate(R.layout.home_head_view, null);
        head_pic = listHeaderView.findViewById(R.id.iv_home_head);
        openMenu.setOnClickListener(this);
        getDate();
        setListener();
        /* Add Location test*/
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG, "onLocationChanged "+location);
                if (location != null) {
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (Address add:addresses) {
                        Log.e(TAG, "location onLocationChanged is: " + add.getAddressLine(0)+" for "+add.getThoroughfare());
                    }
                    textView.setText(addresses.get(0).getAddressLine(0)+"location changed");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e(TAG, "onLocationChanged "+extras.toString());
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e(TAG, "onLocationChanged provider is enable");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e(TAG, "onLocationChanged onProviderDisabled");
            }
        };
        textView = (TextView) currentView.findViewById(R.id.tv_common_above_head);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        getLocation();
        /**/
        return currentView;
    }

    private void getLocation() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},103 );
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 105);
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.e(TAG, "location is: " + location+" provider for Network");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);
        geocoder = new Geocoder(getContext());
        try {
            if (location != null) {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                StringBuffer buffer = new StringBuffer();
                for (Address add:addresses) {
                    buffer.append(add.getFeatureName()+"\n");
                    Log.e(TAG, "location is: " + add.getAddressLine(0)+" for "+add.getFeatureName());
                }
                textView.setText(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setListener() {

        mListView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(RefreshableListView listView) {
                new NewDataTask().execute();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent intent = new Intent(getActivity(),
                        RestaurantDetailActivity.class);
                intent.putExtra("name", mlist.get(position + 1).getName());
                startActivity(intent);
            }
        });
        head_pic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        DianPingWebActivity.class);
                startActivity(intent);
            }
        });
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
        }
    }

    private void getDate() {

        mlist = new ArrayList<RestaurantEntity>();
        RestaurantEntity restaurant1 = new RestaurantEntity();
        restaurant1.setLogo("drawable://" + R.drawable.pic_jigongbao);
        restaurant1.setName("齐鲁兄弟鸡公煲");
        restaurant1.setItem_msg("月售208单 / 20元起送 / 30分钟");
        restaurant1.setRate_numbers(1);
        restaurant1.setIs_rest(true);
        restaurant1.setPromotion("指定食品，每份减4元");
        restaurant1.setIs_half(true);
        restaurant1.setIs_mins(true);
        mlist.add(restaurant1);
        restaurant1 = null;

        RestaurantEntity restaurant2 = new RestaurantEntity();
        restaurant2.setLogo("drawable://" + R.drawable.pic_jixiang);
        restaurant2.setName("吉祥混沌");
        restaurant2.setItem_msg("月售128单 / 14元起送 / 20分钟");
        restaurant2.setPromotion("【新】下单立减3元，份份减3，加赠500ml康师傅果汁！");
        restaurant2.setIs_mins(true);
        restaurant2.setRate_numbers(2);
        mlist.add(restaurant2);
        restaurant2 = null;

        RestaurantEntity restaurant3 = new RestaurantEntity();
        restaurant3.setLogo("drawable://" + R.drawable.pic_milishi);
        restaurant3.setName("迷离士汉堡");
        restaurant3.setItem_msg("月售221单 / 12元起送 / 30分钟");
        restaurant3.setIs_favor(true);
        restaurant3.setRate_numbers(3);
        restaurant3.setPromotion("【新】赠500ml康师傅果汁！");
        restaurant3.setIs_half(true);
        mlist.add(restaurant3);
        restaurant3 = null;

        RestaurantEntity restaurant4 = new RestaurantEntity();
        restaurant4.setLogo("drawable://" + R.drawable.pic_shaxian);
        restaurant4.setName("沙县小吃");
        restaurant4.setItem_msg("月售218单 / 11元起送 / 10分钟");
        restaurant4.setIs_rest(true);
        restaurant4.setRate_numbers(4);
        restaurant4.setPromotion("帅哥给你送餐！");
        restaurant4.setIs_mins(true);
        mlist.add(restaurant4);
        restaurant4 = null;

        RestaurantEntity restaurant5 = new RestaurantEntity();
        restaurant5.setLogo("drawable://" + R.drawable.pic_shiguifan);
        restaurant5.setName("韩式石锅饭");
        restaurant5.setItem_msg("月售82单 / 14元起送 / 22分钟");
        restaurant5.setIs_favor(true);
        restaurant5.setRate_numbers(5);
        restaurant5.setIs_mins(true);
        mlist.add(restaurant5);
        restaurant5 = null;

        RestaurantEntity restaurant6 = new RestaurantEntity();
        restaurant6.setLogo("drawable://" + R.drawable.pic_tengqi);
        restaurant6.setName("藤崎寿司");
        restaurant6.setItem_msg("月售34单 / 11元起送 / 10分钟");
        restaurant6.setRate_numbers(2);
        restaurant6.setIs_mins(true);
        mlist.add(restaurant6);
        restaurant6 = null;

        RestaurantEntity restaurant7 = new RestaurantEntity();
        restaurant7.setLogo("drawable://" + R.drawable.pic_xiaohongmao);
        restaurant7.setName("小红帽快餐厅");
        restaurant7.setItem_msg("月售233单 / 14元起送 / 20分钟");
        restaurant7.setRate_numbers(3);
        restaurant7.setIs_mins(true);
        mlist.add(restaurant7);
        restaurant7 = null;

        adapter = new HomePageRestaurantAdapter(getActivity(), mlist);
        mListView.setAdapter(adapter);
        mListView.addHeaderView(listHeaderView);
    }

    private class NewDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            int current = mListView.getAdapter().getCount();
            if (current < total) {
                add += step;
                mListView.removeHeaderView(listHeaderView);
                getDate();
            }

            mListView.completeRefreshing();

            super.onPostExecute(result);
        }
    }

}
