package com.example.ele_me.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.BaseApplication;
import com.example.ele_me.R;
import com.example.ele_me.entity.TestVolleyJson;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<TestVolleyJson.Data> mList;
    private Context mContext;

    @SuppressWarnings("unchecked")
    public ChatAdapter(List<TestVolleyJson.Data> list, Context context){
        this.mList = list;
        this.mContext = context;
    }
    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.im_test, parent,false);/////
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, int position) {
        TestVolleyJson.Data data = mList.get(position);
        final String imgUri = data.getImage_link();
        holder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.ic_launcher));
        holder.imageView.setTag(imgUri);
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    URL url = new URL(imgUri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (imgUri.equals(holder.imageView.getTag())) {
                    if (BaseApplication.DEBUG)Log.e(BaseApplication.TAG, "onPostExecute"+(Bitmap)o);
                    holder.imageView.setImageBitmap((Bitmap)o);
                }
            }
        }.execute();
        holder.fromName.setText(data.getFrom());
        holder.msg.setText(data.getMessage());
        holder.date.setText(data.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView fromName;
        TextView msg;
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pic);
            fromName = itemView.findViewById(R.id.from_name);
            msg = itemView.findViewById(R.id.msg);
            date = itemView.findViewById(R.id.date);
        }
    }
}
