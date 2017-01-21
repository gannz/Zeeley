package com.zeeley;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class showInterest extends AppCompatActivity {
    final int Section = 0;
    final int normalItem = 1;
    final int title = 2;
    final int image = 3;
     myAdapter adapter;
    // private ArrayList<Integer> interestList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_interest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ListView listView = (ListView) findViewById(R.id.list);
        adapter=new myAdapter();
        listView.setAdapter(adapter);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Interests");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem search = menu.findItem(R.id.mysearch);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    class viewHolder {
        ImageView image;
        TextView textView;
      /*  public viewHolder(View view){
            image=(ImageView) view.findViewById(R.id.childimage);
            textView = (TextView) view.findViewById(R.id.childtitle);
        }*/
    }

    class myAdapter extends BaseAdapter {
        private List<item> list;
        private List<item> result;
        private List<item> copy;
        LayoutInflater inflater;
        public void setdatasource(List<item> list) {
            this.list = list;
            this.copy = new ArrayList<>(list);
            result = new ArrayList<item>();

        }
        public  void filter(String text) {
            if (text.isEmpty()) {
                result.clear();
                result.addAll(copy);
            } else {
                result.clear();
                text = text.toLowerCase();
                for (item i : copy) {
                    if(!i.isSection){
                        if (i.getTitle().toLowerCase().contains(text)) {
                            result.add(i);
                        }
                    }

                }

            }
            list.clear();
            list.addAll(result);
            notifyDataSetChanged();
        }

        public myAdapter() {
            inflater = LayoutInflater.from(showInterest.this);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).isSection ? Section : normalItem;

        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEnabled(int position) {
            return !list.get(position).isSection;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            item i = (item) getItem(position);
            return i.getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                viewHolder holder = new viewHolder();
                if (getItemViewType(position) == Section) {
                    convertView = inflater.inflate(R.layout.section, parent, false);
                    holder.textView = (TextView) convertView.findViewById(R.id.list_item_section_text);
                    holder.textView.setText(list.get(position).getTitle());
                    convertView.setTag(holder);
                } else if (getItemViewType(position) == normalItem) {
                    convertView = inflater.inflate(R.layout.child, parent, false);
                    holder.textView = (TextView) convertView.findViewById(R.id.childtitle);
                    holder.textView.setText(list.get(position).getTitle());
                    holder.image = (ImageView) convertView.findViewById(R.id.childimage);
                    holder.image.setImageResource(list.get(position).getImage());
                    convertView.setTag(holder);

                }
            } else {
                viewHolder holder = (viewHolder) convertView.getTag();
                if (getItemViewType(position) == Section) {
                    holder.textView.setText(list.get(position).getTitle());
                } else {
                    holder.textView.setText(list.get(position).getTitle());
                    holder.image.setImageResource(list.get(position).getImage());
                }
            }
            return convertView;
        }
    }

    ArrayList<item> list = new ArrayList<>();

    private void setlistData() {
        String sectionTitles[] = {"Food", "Outdoor", "RockBand", "Find company for", "Travel", "Dating", "Chating"};
        int items[] = {4, 5, 3, 3, 2, 1, 1};
        int k = 1;
        for (int i = 0; i < 7; i++) {
            item section = new item(sectionTitles[i], true, -1);
            list.add(section);
            for (int j = 1; j <= items[i]; j++) {
                item itemObj = new item(constants.getInterstTitle(k), false, k);
                itemObj.setImage(constants.getInterestImage(k));
                list.add(itemObj);
                k++;
            }
        }
    }
}
