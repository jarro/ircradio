package com.earthflare.android.ircradio;

import java.io.Serializable;

public class Channel implements Serializable{

	private static final long serialVersionUID = 2L;
	
	public String name;
	public int users;
	public String topic;
	
	
	public Channel(String name, int users, String topic) {
		
		this.name = name;
		this.users = users;
		this.topic = topic;
		
		
	}


	
	
}
