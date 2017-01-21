package com.zeeley;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gannu on 16-06-2016.
 */
public class fragment_adapter extends BaseAdapter {
    private static final int MESSAGE = 1;
    private static final int DATE = 2;
    private static final int IMAGE = 3;
    private static LayoutInflater inflater = null;
    ArrayList<ChatMessage> chatMessageList;

    public fragment_adapter(Context context, ArrayList<ChatMessage> list) {
        chatMessageList = list;
        inflater = LayoutInflater.from(context);

    }

    public void changedataSource(ArrayList<ChatMessage> list) {
        chatMessageList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean isEnabled(int position) {

        ChatMessage message = (ChatMessage) getItem(position);
        if (message.isDate())
            return false;
        else
            return true;
    }

    @Override
    public int getItemViewType(int position) {

        ChatMessage message = (ChatMessage) getItem(position);
        if (message.isDate())
            return DATE;
        else if (message.isImage())
            return IMAGE;
        else return MESSAGE;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMessage message = (ChatMessage) getItem(position);
        int type = getItemViewType(position);

        if (convertView == null) {
            switch (type) {
                case IMAGE:

                    convertView = inflater.inflate(R.layout.img_msg, parent, false);
                    imgHolder holder = new imgHolder(convertView);
                    convertView.setTag(holder);
                    break;
                case DATE:

                    convertView = inflater.inflate(R.layout.date, parent, false);
                    dateHolder datehld = new dateHolder(convertView);
                    convertView.setTag(datehld);
                    break;
                case MESSAGE:

                    convertView = inflater.inflate(R.layout.message, parent, false);
                    viewholder vh = new viewholder(convertView);
                    convertView.setTag(vh);
                    break;

            }
        }
        switch (type) {
            case IMAGE:
                imgHolder ih = (imgHolder) convertView.getTag();
                ih.setData(message.getImage(), message.getTime());
                if (message.isMine()) {
                    ih.parent_layout.setGravity(Gravity.END);
                    ih.parent_layout.setPadding(100,0,0,0);
                } else{
                    ih.parent_layout.setGravity(Gravity.START);
                    ih.parent_layout.setPadding(0,0,100,0);
                }


                break;
            case MESSAGE:
                viewholder vh = (viewholder) convertView.getTag();
                vh.setData(message);
                if (message.isMine) {
                    vh.layout.setBackgroundResource(R.drawable.bubble2);
                    vh.parent_layout.setGravity(Gravity.END);
                    vh.parent_layout.setPadding(100,0,0,0);
                } else {
                    vh.layout.setBackgroundResource(R.drawable.bubble1);
                    vh.parent_layout.setGravity(Gravity.START);
                    vh.parent_layout.setPadding(0,0,100,0);
                }

                break;
            case DATE:
                dateHolder dh = (dateHolder) convertView.getTag();
                dh.date.setText(message.getDate());
                break;

        }

        return convertView;
    }

    public void deleteAll() {
        if (chatMessageList != null && chatMessageList.size() > 0) {
            chatMessageList.clear();
            notifyDataSetChanged();
            /// also delete conversation from database..
        }
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessageList.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

   /* @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = chatMessageList.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.message, parent, false);
            viewholder holder = new viewholder(convertView);
            convertView.setTag(holder);
        }
        viewholder holder = (viewholder) convertView.getTag();
        if (message.isMine() == null) {
            holder.layout.setBackgroundResource(R.drawable.bubble1);
            holder.parent_layout.setGravity(Gravity.CENTER);
            holder.msg.setText(message.getDate());

        } else if (message.isMine()) {

            holder.setData(message);
            holder.layout.setBackgroundResource(R.drawable.bubble2);
            holder.parent_layout.setGravity(Gravity.END);
        }
        // If not mine then align to left
        else {
            holder.setData(message);
            holder.layout.setBackgroundResource(R.drawable.bubble1);
            holder.parent_layout.setGravity(Gravity.START);
        }

        return convertView;
    }
*/

    public void add(ChatMessage object) {
        chatMessageList.add(object);
        notifyDataSetChanged();
    }

    private class viewholder {
        TextView msg, date;
        LinearLayout layout, parent_layout;

        public viewholder(View view) {
            msg = (TextView) view.findViewById(R.id.message_text);
            date = (TextView) view.findViewById(R.id.date);
            layout = (LinearLayout) view
                    .findViewById(R.id.bubble_layout);
            parent_layout = (LinearLayout) view
                    .findViewById(R.id.bubble_layout_parent);

        }

        public void setData(ChatMessage message) {
            this.msg.setText(message.getMessage());
            this.msg.setTextColor(Color.BLACK);
            this.date.setText(message.getTime());

        }
    }

    private class imgHolder {
        ImageView imageView;
        TextView date, title;
        LinearLayout layout, parent_layout;

        public imgHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.image);
            date = (TextView) view.findViewById(R.id.date);
            title = (TextView) view.findViewById(R.id.title);
            layout = (LinearLayout) view
                    .findViewById(R.id.bubble_layout);
            parent_layout = (LinearLayout) view
                    .findViewById(R.id.bubble_layout_parent);
        }

        public void setData(Bitmap img, String date) {
            imageView.setImageBitmap(img);
            this.date.setText(date);
        }
    }

    private class dateHolder {
        TextView date;
        LinearLayout layout, parent_layout;

        public dateHolder(View view) {
            date = (TextView) view.findViewById(R.id.date);
            parent_layout = (LinearLayout) view
                    .findViewById(R.id.bubble_layout_parent);
            parent_layout.setGravity(Gravity.CENTER);
        }
    }
}
