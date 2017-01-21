package com.zeeley;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Arrays;

public class mygridActivity extends Activity {

    private DraggableGridView gridView;
    ArrayList<itemObject> items;
    Gson gson;
    SharedPreferences sharedPreferences;
    private boolean updated = false;
    private static final String DEFAULT = "default value";
    private static final String List = "listItems";
    SharedPreferences.Editor editor;
    private final int Requestcode = 45;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygrid);
        gson = new Gson();
        sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
        String json = sharedPreferences.getString(List, DEFAULT);
        if (json.equals(DEFAULT)) {
            items = new ArrayList<>();
            editor = sharedPreferences.edit();
            itemObject pizza = new itemObject(1, constants.getInterestImage(1), constants.getInterstTitle(1));
            items.add(pizza);
            itemObject cricket = new itemObject(5, constants.getInterestImage(5), constants.getInterstTitle(5));
            items.add(cricket);
            itemObject restaurant = new itemObject(15, constants.getInterestImage(15), constants.getInterstTitle(15));
            items.add(restaurant);
            itemObject taxi = new itemObject(16, constants.getInterestImage(16), constants.getInterstTitle(16));
            items.add(taxi);
            itemObject movie = new itemObject(13, constants.getInterestImage(13), constants.getInterstTitle(13));
            items.add(movie);
            itemObject football = new itemObject(6, constants.getInterestImage(6), constants.getInterstTitle(6));
            items.add(football);
            String jsonString = gson.toJson(items);
            editor.putString(List, jsonString);
            editor.apply();
        } else {
            itemObject[] itemObjects = gson.fromJson(json, itemObject[].class);
            items = new ArrayList<>(Arrays.asList(itemObjects));
        }
        gridView = ((DraggableGridView) findViewById(R.id.grid_view));
        gridView.setDeleteZone((DeleteDropZoneView) findViewById(R.id.delete_view));

        setUiListeners();
        inflater = LayoutInflater.from(this);
        for (itemObject object : items) {
            View view = inflater.inflate(R.layout.griditem, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            TextView textView = (TextView) view.findViewById(R.id.name);
            view.setId(object.getId());
            imageView.setImageResource(object.getImage());
            textView.setText(object.getName());
            gridView.addView(view);
        }
        Button addButon = (Button) findViewById(R.id.addMore);
        addButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mygridActivity.this, Interst_List.class);
                intent.putExtra(constants.SOURCE, constants.From_addInterest);
                startActivityForResult(intent, Requestcode);
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long pk) {
                int id=view.getId();
                Intent intent = new Intent();
                intent.putExtra(constants.Interest_Id, id);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        gridView.setNumberOfColumns(3);
        gridView.setCenterChildrenInGrid(true);
        gridView.setFixedChildrenWidth(70);
        gridView.setFixedChildrenHeight(70);
        this.setFinishOnTouchOutside(true);
    }

    @Override
    protected void onPause() {
        if (updated) {
            editor = sharedPreferences.edit();
            editor.remove(List);
            editor.putString(List, gson.toJson(items));
            editor.apply();
            updated = false;
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Requestcode && resultCode == RESULT_OK) {
            ArrayList<Integer> list = data.getIntegerArrayListExtra(constants.INTEREST_LISTS);
            if (!list.isEmpty()) {
                updated = true;
                boolean isPresent = false;
                for (int i : list) {
                    isPresent = false;
                    for (itemObject j : items) {
                        if (j.getId() == i) {
                            isPresent = true;
                            break;
                        }
                    }
                    if (!isPresent) {
                        items.add(new itemObject(i, constants.getInterestImage(i), constants.getInterstTitle(i)));
                        View view = inflater.inflate(R.layout.griditem, null);
                        ImageView imageView = (ImageView) view.findViewById(R.id.image);
                        TextView textView = (TextView) view.findViewById(R.id.name);
                        view.setId(i);
                        imageView.setImageResource(constants.getInterestImage(i));
                        textView.setText(constants.getInterstTitle(i));
                        gridView.addView(view);
                    }
                }

            }


        }
    }

    private void setUiListeners() {

        gridView.setOnRearrangeListener(new OnRearrangeListener() {
            public void onRearrange(int oldIndex, int newIndex) {
                if (items.isEmpty()) {
                    return;
                }
                updated = true;
                itemObject scheduledControl = items.remove(oldIndex);
                if (oldIndex < newIndex)
                    items.add(newIndex, scheduledControl);
                else
                    items.add(newIndex, scheduledControl);
            }

            public void onRearrange(boolean isDraggedDeleted, int draggedDeletedIndex) {
                if (items.isEmpty()) {
                    return;
                }

                if (isDraggedDeleted) {
                    updated = true;
                    items.remove(draggedDeletedIndex);
                }
            }
        });
    }

    class itemObject {
        int id;
        String name;
        int image;

        itemObject(int id, int image, String name) {
            this.id = id;
            this.name = name;
            this.image = image;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getImage() {
            return image;
        }
    }
}
