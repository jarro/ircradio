package com.cratorsoft.android.chat;

/**
 * Created by j on 08/12/14.
 */
public class MenuData {

    public long network;
    public String account;
    public String channel;
    public String type;

    public MenuData(long network, String account, String channel, String type) {
        this.network = network;
        this.account = account;
        this.channel = channel;
        this.type = type;
    }

}
