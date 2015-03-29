package com.example.nilesh.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nilesh.hike.AskPermission;
import com.example.nilesh.hike.R;
import com.example.nilesh.model.MessageDetails;

import java.util.List;

/**
 * Created by Richa on 3/28/2015.
 */

public class MessageAdapter extends BaseAdapter {

    private static class ViewHolder{
        TextView userName;
        TextView messgae;
        RelativeLayout messageContainer;
    }
    List<MessageDetails> data;
    private Context context;
    private LayoutInflater layoutInflater;

    public MessageAdapter(Context context, List<MessageDetails> data)
    {
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.message_list_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.messgae = (TextView) convertView.findViewById(R.id.message);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.user);
            viewHolder.messageContainer = (RelativeLayout) convertView.findViewById(R.id.messageContainer);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if(data.get(position).isLoggedInUserSender())
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.userName.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0, 0, 10, 0);
            viewHolder.userName.setLayoutParams(params);
            params = (RelativeLayout.LayoutParams)viewHolder.messgae.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0, 0, 10, 0);
            viewHolder.messgae.setLayoutParams(params);
            viewHolder.messageContainer.setBackgroundColor(0x837429);
        }
        else
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.userName.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(10, 0, 0, 0);
            viewHolder.userName.setLayoutParams(params);
            params = (RelativeLayout.LayoutParams)viewHolder.messgae.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(10, 0, 0, 0);
            viewHolder.messgae.setLayoutParams(params);
            viewHolder.messageContainer.setBackgroundColor(0x235672);
        }
        viewHolder.userName.setText(data.get(position).getUser());
        viewHolder.messgae.setText(data.get(position).getMessage());
        return convertView;
    }
}
