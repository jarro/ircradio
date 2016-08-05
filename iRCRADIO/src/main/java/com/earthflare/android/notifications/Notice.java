package com.earthflare.android.notifications;

import java.io.Serializable;
import java.util.Date;

public class Notice implements Serializable{

	//Types
	//named = name said in channel
	//pmed = new private message
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6003960787960731974L;
	public String message;
	public long network;
	public String accountname;
	public String channel;
	public String sender;
	public Date time = new Date();
	public String noticetype;
	public long uid;
	
	
	public Notice() {
		
	}
	
}
