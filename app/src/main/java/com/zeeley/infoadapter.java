package com.zeeley;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gannu on 30-06-2016.
 */
public  class infoadapter extends RecyclerView.Adapter<infoadapter.myviewholder> {
    private List<onlineUser> list;
    private List<onlineUser> result;
    private List<onlineUser> copy;
    private ClickListener clickListener;
    Resources res;
    Context context;
    public infoadapter(Context context) {
        this.context = context;
        res = context.getResources();
    }

    public void setdatasource(List<onlineUser> list) {
        this.list = list;
        this.copy = new ArrayList<onlineUser>(list);
        result = new ArrayList<onlineUser>();

    }

    public void setListener(ClickListener listener) {
        clickListener = listener;
    }

    @Override
    public myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inforow, parent, false);

        return new myviewholder(itemView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(final myviewholder holder, final int position) {

        onlineUser data = list.get(position);
        /*Bitmap src = BitmapFactory.decodeResource(res, data.getImage());
        RoundedBitmapDrawable dr =
                RoundedBitmapDrawableFactory.create(res, src);
        dr.setCircular(true);*/
        //dr.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
        // imageView.setImageDrawable(dr);
        holder.imageView.setImageBitmap(data.getImage());
        //holder.imageView.setImageResource(R.drawable.userimg);
        holder.large.setText(data.getName());
       // holder.top.setText(data.getDistance());
        holder.top.setText("N/A");
        //holder.bottom.setText(data.getLocation());
        holder.bottom.setText("N/A");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(list.get(position),false);
                holder.invite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        clickListener.onClick(list.get(position),true);
                    }
                });
            }
        });
    }

    public void filter(String text) {
        if (text.isEmpty()) {
            result.clear();
            result.addAll(copy);
        } else {
            result.clear();
            text = text.toLowerCase();
            for (onlineUser i : copy) {
                if (i.getName().toLowerCase().contains(text)) {
                    result.add(i);
                }
            }

        }
        list.clear();
        list.addAll(result);
        notifyDataSetChanged();

    }

    public void swap(int position) {
        onlineUser d = list.get(position);
        list.remove(position);
        list.add(0, d);
    }

    public class myviewholder extends RecyclerView.ViewHolder {
        public ImageView imageView,invite;
        public TextView large, top, bottom;

        public myviewholder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.chatimage);
            invite = (ImageView) itemView.findViewById(R.id.invitation);
            large = (TextView) itemView.findViewById(R.id.name);
            top = (TextView) itemView.findViewById(R.id.distance);
            bottom = (TextView) itemView.findViewById(R.id.location);

        }

    }

    public interface ClickListener {
        void onClick(onlineUser user,boolean isInvite);

        void onLongClick(onlineUser user);
    }

}
