package com.cratorsoft.android.dbtable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="channel")
public class ChannelData {

	@DatabaseField(generatedId = true, columnName = "_id")
	public long id;
	
	@DatabaseField(canBeNull=false)
	public long accountid;
	
	@DatabaseField(canBeNull=false)
	public String channel;
	
	@DatabaseField(canBeNull=false)
	public String channelpass;
	
	//boolean
	@DatabaseField(canBeNull=false)
	public String joinleave;
	
	//boolean
	@DatabaseField(canBeNull=false)
	public String autojoin;
	
	@DatabaseField(canBeNull=false)
	public String language;




}
