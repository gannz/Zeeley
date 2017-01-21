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
 */
public class chatadapter extends RecyclerView.Adapter<chatadapter.myviewholder> {
    private List<dataobject> list;
    private List<dataobject> result;
    private List<dataobject> copy;
    private ClickListener clickListener;
    dataobject data;
    Context context;
    Resources res;

    public chatadapter(Context context) {
        this.context = context;
        res = context.getResources();
    }

    public void setdatasource(List<dataobject> list) {
        this.list = list;
        this.copy = new ArrayList<>(list);
        result = new ArrayList<dataobject>();

    }

    public void changedatasource(List<dataobject> list) {
        this.list = list;
        this.copy.clear();
        this.copy.addAll(list);
        if (result == null) {
            result = new ArrayList<dataobject>();
        }
    }

    public void setListener(ClickListener listener) {
        clickListener = listener;
    }

    @Override
    public myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatrow, parent, false);

        return new myviewholder(itemView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(myviewholder holder, final int position) {
        data = list.get(position);

        holder.imageView.setImageBitmap(constants.getImage(Integer.valueOf(data.getId()), context));
        holder.name.setText(data.getName());
        holder.date.setText(data.getDate());
        holder.lastmessage.setText(data.getLastmessage());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(data);
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
            for (dataobject i : copy) {
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
        dataobject d = list.get(position);
        list.remove(position);
        list.add(0, d);
        notifyDataSetChanged();
    }

    public class myviewholder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name, date, lastmessage;
        public View view;


        public myviewholder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.chatimage);
            name = (TextView) itemView.findViewById(R.id.chatname);
            date = (TextView) itemView.findViewById(R.id.date);
            view = itemView;
            lastmessage = (TextView) itemView.findViewById(R.id.message);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    clickListener.onClick(list.get(getAdapterPosition()));
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(list.get(getAdapterPosition()));
                }
            });
        }

    }

    public interface ClickListener {
        void onClick(dataobject d);

        void onLongClick(dataobject d);

    }

}
