package com.earthflare.android.logmanager;



import java.io.Serializable;
import java.util.Date;

import android.text.SpannableString;
import android.text.format.DateFormat;

import com.earthflare.android.ircradio.MessageType;

public class LogEntry  implements Serializable{

private static final long serialVersionUID = 7526472295622776147L;


public SpannableString ss;
public long uid;
public boolean hasname;
public int type;
public String timestamp;

public LogEntry(SpannableString ss, long uid, boolean hasname, int type) {	
	this.ss = ss;
	this.uid = uid;
	this.hasname = hasname;
	this.type = type;
	timestamp = "[" + DateFormat.format("kk:mm", new Date()).toString() + "]";
}

public LogEntry(SpannableString ss, long uid) {	
	this.ss = ss;
	this.uid = uid;
	this.hasname = false;
	this.type = MessageType.OTHER;
	timestamp = "[" + DateFormat.format("kk:mm", new Date()).toString() + "]";
}



}
