package com.example.ele_me.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.ele_me.R;
import com.example.ele_me.util.InjectView;
import com.example.ele_me.util.Injector;
import com.example.ele_me.util.SharedPreferencesUtil;

public class LoginActivity extends Activity {
    @InjectView(R.id.iv_head_left)
    private ImageView head_left;
    @InjectView(R.id.linear_above_toHome)
    private LinearLayout above_toHome;
    @InjectView(R.id.tv_common_above_head)
    private TextView above_tittle;
    @InjectView(R.id.login_submit)
    private TextView login;
    private SharedPreferencesUtil SpUtil;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        Injector.get(this).inject();// init views
        SpUtil = new SharedPreferencesUtil(this, "SharedPreferences");
        initView();
        setListener();
    }

    private void setListener() {
        // TODO Auto-generated method stub
        above_toHome.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();

            }
        });
        login.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                SpUtil.setValue("loginFlag", true);
                finish();

            }
        });

    }

    private void initView() {
        above_tittle.setText("login");
        head_left.setImageResource(R.drawable.abc_ic_ab_back_holo_dark);

    }

}
