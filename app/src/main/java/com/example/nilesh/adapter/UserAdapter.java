package com.example.nilesh.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nilesh.database.DbHelper;
import com.example.nilesh.hike.AskPermission;
import com.example.nilesh.hike.R;
import com.example.nilesh.hike.SendMessage;
import com.example.nilesh.model.UserDetails;

import java.util.List;

public class UserAdapter extends BaseAdapter {

    private static class ViewHolder{
        TextView userName;
        RelativeLayout container;
        ImageView plus;
    }

    List<UserDetails> data;
    private Context context;
    private LayoutInflater layoutInflater;
    private DbHelper dbHelper;

    public UserAdapter(Context context, List<UserDetails> data)
    {
        this.context = context;
        dbHelper = new DbHelper(context);
        this.data = data;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.user_list_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.container = (RelativeLayout) convertView.findViewById(R.id.container);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.userNameList);
            viewHolder.plus = (ImageView) convertView.findViewById(R.id.plus);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //permission has been granted, so hide the plus icon
        if(!(dbHelper.getPriority(data.get(position).getUserName()).equalsIgnoreCase("")))
            viewHolder.plus.setVisibility(View.GONE);
        else
            viewHolder.plus.setVisibility(View.VISIBLE);

        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AskPermission.class);
                intent.putExtra("username", data.get(position).getUserName());
                context.startActivity(intent);
            }
        });

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SendMessage.class);
                intent.putExtra("username", data.get(position).getUserName());
                context.startActivity(intent);
            }
        });

        viewHolder.userName.setText(data.get(position).getUserName());
        return convertView;
    }
}
