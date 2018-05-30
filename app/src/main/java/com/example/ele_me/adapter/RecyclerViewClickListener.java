package com.example.ele_me.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.example.BaseApplication;

public class RecyclerViewClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener onItemClickListener;
    public RecyclerViewClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_UP:
                View childView = rv.findChildViewUnder(e.getX(),e.getY());
                if (BaseApplication.DEBUG)Log.e(BaseApplication.TAG, "onInterceptTouchEvent "+e.getY());
                if (rv.getChildAdapterPosition(childView) >= 0)
                onItemClickListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
                break;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    //内部接口，定义点击方法以及长按方法
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
