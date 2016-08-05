package com.earthflare.android.ircradio;

import android.os.Bundle;

public class NavItem {

	public static final String CHANNEL = "channel";
	public static final String SERVER = "server";
	
	public long network;
	public String accountname;
	public String acttype;
	public String channelname;
	
	public NavItem(long network, String accountname) {
	
		this.network = network;
		this.accountname = accountname;
		this.acttype = SERVER;
		
	}
	
    public NavItem(long network, String accountname, String channelname) {
		
    	this.network = network;
		this.accountname = accountname;
		this.acttype = CHANNEL;
    	this.channelname = channelname;
    	
	}

    public NavItem(Bundle data){

        this.network = data.getLong("network");
        this.accountname = data.getString("accountname");
        this.acttype = data.getString("acttype");
        this.channelname = data.getString("channelname");

    }

   public NavItem(long network, String accountname, String channelname, String acttype) {
		
    	this.network = network;
		this.accountname = accountname;
		this.acttype = acttype;
    	this.channelname = channelname;
    	
	}
	
    public NavItem(boolean bad) {
    	acttype = "none";
    }
    
    public NavItem() {
    	this.network = Globo.chatlogActNetworkName;
    	this.accountname = Globo.chatlogActAccountName;
    	this.channelname = Globo.chatlogActChannelName;
    	this.acttype = Globo.chatlogActType;
    }


    public Bundle getBundle(){

        Bundle data = new Bundle();
        data.putLong("network",this.network);
        data.putString("accountname", this.accountname);
        data.putString("acttype", this.acttype);
        data.putString("channelname", this.channelname);

        return data;
    }



}

