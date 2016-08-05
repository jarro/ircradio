package com.cratorsoft.android.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;
import com.earthflare.android.ircradio.RadioBot;

import org.jibble.pircbot.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UserAdapter extends BaseAdapter {


    private List<User> users = new ArrayList<User>();

    public UserAdapter(Context c) {
        mContext = c;

        if (Globo.botManager.ircBots.containsKey(Globo.chatlogActNetworkName) && Globo.chatlogActType.equals("channel")) {

            RadioBot rb = Globo.botManager.ircBots.get(Globo.chatlogActNetworkName);
            if (rb.inChannel(Globo.chatlogActChannelName)) {
                //populate array
                User[] userArray = rb.getUsers(Globo.chatlogActChannelName);
                users = Arrays.asList(userArray);
                Collections.sort(users);

            }

        }

    }

    public int getCount() {
        return users.size();
    }

    public Object getItem(int position) {
        return users.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        TextView tv = (TextView) TextView.inflate(mContext, R.layout.row_userlist, null);
        User user = users.get(position);
        tv.setText(user.getPrefix() + user.getNick());
        return tv;
    }

    private Context mContext;


}