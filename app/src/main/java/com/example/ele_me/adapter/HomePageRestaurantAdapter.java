package com.example.ele_me.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.ele_me.R;
import com.example.ele_me.entity.RestaurantEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomePageRestaurantAdapter extends BaseAdapter {

    private Context context;
    private List<RestaurantEntity> mlist;
    private LayoutInflater inflater;

    public HomePageRestaurantAdapter(Context context,
                                     List<RestaurantEntity> list) {
        this.context = context;
        this.mlist = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.restaurant_list_item_dev, null);
            holder = new ViewHolder();
            holder.name = convertView
                    .findViewById(R.id.restaurant_list_item_name);
            holder.logo = convertView
                    .findViewById(R.id.restaurant_list_item_logo);
            holder.item_msg = convertView
                    .findViewById(R.id.restaurant_list_item_msg);
            holder.buy_nums = convertView
                    .findViewById(R.id.restaurant_list_item_buynums);
            holder.rate = convertView
                    .findViewById(R.id.restaurant_list_item_rate);
            holder.rest = convertView
                    .findViewById(R.id.restaurant_list_item_avaiable);
            holder.favor = convertView
                    .findViewById(R.id.restaurant_list_item_favor);
            holder.promotion = convertView
                    .findViewById(R.id.restaurant_list_item_present_promotion_container);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mlist.get(position).getIs_favor()) {
            holder.favor.setVisibility(View.VISIBLE);
        }
        if (mlist.get(position).getIs_rest()) {
            holder.rest.setVisibility(View.VISIBLE);
            holder.rate.setVisibility(View.GONE);
        }
        holder.name.setText(mlist.get(position).getName());
        holder.buy_nums.setText(mlist.get(position).getBuy_nums());
        holder.item_msg.setText(mlist.get(position).getItem_msg());
        holder.rate.setNumStars(mlist.get(position).getRate_numbers());

        //������ƹ���Ϣ�����
        if (!TextUtils.isEmpty(mlist.get(position).getPromotion())) {
            LinearLayout promotionLayout = (LinearLayout) inflater.inflate(R.layout.restaurant_promotion_with_icon_text_view, null);
            TextView promotionTv = promotionLayout.findViewById(R.id.restaurant_promotion_description);
            TextView promotionIcon = promotionLayout.findViewById(R.id.restaurant_promotion_icon);
            if (mlist.get(position).getIs_half()) {
                promotionIcon.setBackgroundColor(R.color.orange_ff);
                promotionIcon.setText("��");
            }
            if (mlist.get(position).getIs_mins()) {
                promotionIcon.setBackgroundColor(R.color.red_light);
                promotionIcon.setText("��");
            }
            promotionTv.setText(mlist.get(position).getPromotion());
            holder.promotion.addView(promotionLayout);
        }

        ImageLoader.getInstance().displayImage(mlist.get(position).getLogo(), holder.logo);

        return convertView;
    }

    public class ViewHolder {
        public ImageView logo;
        public TextView name;
        public TextView rate_numbers;//��������
        public TextView buy_nums;//�۳�����
        public TextView item_msg;//������Ϣ
        public ImageView rest;//�Ƿ���Ϣ
        public ImageView favor;//�Ƿ�ϲ��
        public RatingBar rate;
        public LinearLayout promotion;//�ƹ���Ϣ

    }

}
