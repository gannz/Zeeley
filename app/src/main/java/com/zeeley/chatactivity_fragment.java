package com.zeeley;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class chatactivity_fragment extends Fragment implements View.OnClickListener {
    private EditText msg_edittext;
    public ArrayList<ChatMessage> chatlist;
    public fragment_adapter adapter;
    ListView msgListView;
    public static final String USER = "user";
    onlineUser user;
    long server_id = -1;

    public chatactivity_fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = Parcels.unwrap(getArguments().getParcelable(USER));
            server_id = user.getId();

        }

    }

    public static chatactivity_fragment getInstance(Parcelable user) {
        chatactivity_fragment fragment = new chatactivity_fragment();
        Bundle b = new Bundle();
        b.putParcelable(USER, user);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chatactivity_fragment, container, false);
        msg_edittext = (EditText) view.findViewById(R.id.messageEditText);
        msgListView = (ListView) view.findViewById(R.id.msgListView);
        ImageButton sendButton = (ImageButton) view
                .findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(this);

        // ----Set autoscroll of listview when a new ChatMessage arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
        //--------------------------------
       /* if(server_id!=-1){
            getConversation(server_id);
        }*///----------------------------
        chatlist = new ArrayList<>();
        adapter = new fragment_adapter(getActivity(), chatlist);
        msgListView.setAdapter(adapter);
        return view;
    }

    public void getConversation(long id) {
        // get conversation from database and populate the listview..
    }

   /* public void sendTextMessage(View v)  {
        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(message, true);
            chatMessage.Time = CommonMethods.getCurrentTime();
            msg_edittext.setText("");
            adapter.add(chatMessage);
            adapter.notifyDataSetChanged();
//  send msg to server and insert data into database.
            List<dataobject> data = MainActivity.c.datasource;
            if (data != null && data.size() > 0) {
                for (int j = 0; j < data.size(); j++) {
                    if (data.get(j).getUser().getId() == user.getId()) {
                        dataobject obj = data.get(j);
                        if (j != 0) {
                            data.remove(j);
                            data.add(0, obj);
                           // MainActivity.c.setDatasource(data, j);
                        }
                        obj.setLastmessage(message);
                        obj.setDate(CommonMethods.getCurrentDate());
                        //MainActivity.c.setDatasource(data, 0);
                    }
                }
                long id = 1;// get id from above.
                dataobject object = new dataobject(user.getName(), CommonMethods.getCurrentDate(), message, user.getImage(), user);
                data.add(object);
               // MainActivity.c.setDatasource(data, -1);
            } else {
                long id = 1;// insert data into database
                dataobject object = new dataobject(user.getName(), "date", message, user.getImage(), user);
                data.add(object);
                //MainActivity.c.setDatasource(data, -1);

            }
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageButton:

                  // sendTextMessage(v);


        }
    }

}


