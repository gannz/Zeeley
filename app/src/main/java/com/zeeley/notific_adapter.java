package com.zeeley;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by gannu on 05-08-2016.
 */
public class notific_adapter extends RecyclerView.Adapter<notific_adapter.viewholder> {
    Context context;
    LayoutInflater inflater;
    ClickListener clickListener;
    ArrayList<notificObj> list = new ArrayList<>();

    public notific_adapter(Context context,ArrayList<notificObj> list) {
        this.list=list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    public void setListener(ClickListener listener) {
        clickListener = listener;
    }

    @Override
    public viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new viewholder(inflater.inflate(R.layout.notific_row, parent, false));
    }

    @Override
    public void onBindViewHolder(viewholder holder, final int position) {
        notificObj obj=list.get(position);
        holder.image.setImageResource(obj.getProfilePic());
        holder.distance.setText(obj.getDistance());
        holder.interst.setText(constants.getInterstTitle(obj.getInterest()));
        holder.name.setText(obj.getName());
        holder.okImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Invitation is accepted", Toast.LENGTH_SHORT).show();
                list.remove(position);
                notifyItemRemoved(position);
            }
        });
        holder.cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Invitation is rejected",Toast.LENGTH_SHORT).show();
                list.remove(position);
                notifyItemRemoved(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class viewholder extends RecyclerView.ViewHolder {
        public ImageView image, okImage, cancelImage;
        public TextView name, interst, distance;
        public View view;


        public viewholder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.chatimag);
            okImage = (ImageView) itemView.findViewById(R.id.accepted);
            cancelImage = (ImageView) itemView.findViewById(R.id.rejected);
            name = (TextView) itemView.findViewById(R.id.name);
            interst = (TextView) itemView.findViewById(R.id.interest);
            view = itemView;
            distance = (TextView) itemView.findViewById(R.id.distance);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickListener.onLongClick(getAdapterPosition());
                    return false;
                }
            });
        }
    }

    public interface ClickListener {
        void onClick(dataobject d);

        void onLongClick(int position);


    }
}
