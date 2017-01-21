package com.zeeley;

/**
 * Created by gannu on 01-07-2016.
 */

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
private ExpandableListView listView;
    private Context context;
    private List<String> parentTitles;
    private HashMap<String, List<String>> childTitles;
    private HashMap<String, int[]> childimages;
private int[] parentImages;
    ImageView ar;
    public ExpandableListAdapter(Context context, List<String> listDataHeader,int[] parentimages,
                                 HashMap<String, List<String>> listChildData, HashMap<String, int[]> childimages,ExpandableListView listView) {
        this.context = context;
        this.parentTitles = listDataHeader;
        this.childTitles = listChildData;
        this.parentImages=parentimages;
        this.childimages = childimages;
        this.listView=listView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        String title = this.childTitles.get(this.parentTitles.get(groupPosition))
                .get(childPosititon);
        int image = this.childimages.get(this.parentTitles.get(groupPosition))[childPosititon];
        childObject child = new childObject(title, image);
        return child;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child, null);
        }

        TextView childtitle = (TextView) convertView
                .findViewById(R.id.childtitle);
        ImageView childimage = (ImageView) convertView.findViewById(R.id.childimage);
         childObject childobject= (childObject) getChild(groupPosition,childPosition);
        childtitle.setText(childobject.getTitle());
        childimage.setImageResource(childobject.getImage());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition<5)
        return this.childTitles.get(this.parentTitles.get(groupPosition))
                .size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        String title =this.parentTitles.get(groupPosition);
        int image = this.parentImages[groupPosition];
        return new parentObject(title,image);
    }

    @Override
    public int getGroupCount() {
        return this.parentTitles.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.parent, null);
        }
         parentObject prnt= (parentObject) getGroup(groupPosition);
         ar= (ImageView) convertView.findViewById(R.id.arrow);
        ar.setImageResource(android.R.color.transparent);
        TextView parentTitle = (TextView) convertView
                .findViewById(R.id.parenttitle);
        parentTitle.setText(prnt.getTitle());
        ImageView parentImage= (ImageView) convertView.findViewById(R.id.image);
        parentImage.setImageResource(prnt.getImage());
        if(groupPosition<5){

            ar.setImageResource(R.drawable.down);
        }
        convertView.setTag(groupPosition);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position= (int) v.getTag();

                if(position<5){
                    if(listView.isGroupExpanded((Integer)v.getTag()))
                    {
                        listView.collapseGroup((Integer)v.getTag());
                        ar.setImageResource(R.drawable.down);
                    }else
                    {
                        listView.expandGroup((Integer)v.getTag());
                        ar.setImageResource(R.drawable.up);
                    }

                }
                else{
                    Intent i = new Intent(context, MainActivity.class);
                    context.startActivity(i);
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class childObject {
        String title;
        int image;

        public childObject(String title, int image) {
            this.image = image;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public int getImage() {
            return image;
        }
    }
    private class parentObject{
        String title;
        int image;

        public parentObject(String title, int image) {
            this.image = image;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public int getImage() {
            return image;
        }
    }
}
