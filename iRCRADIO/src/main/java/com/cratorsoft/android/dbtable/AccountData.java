package com.cratorsoft.android.dbtable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="account")
public class AccountData {

	@DatabaseField(generatedId = true, columnName = "_id")
	public long id;
	
	@DatabaseField(canBeNull=false)
	public String server;
	
	@DatabaseField(canBeNull=false)
	public String serverpass;
	
	@DatabaseField(canBeNull=false)
	public String port;
	
	//boolean
	@DatabaseField(canBeNull=false)
	public String joinleave;
	
	@DatabaseField(canBeNull=false)
	public String nick;
	
	@DatabaseField(canBeNull=false)
	public String auth;
	
	@DatabaseField(canBeNull=false)
	public String accountname;
	
	@DatabaseField(canBeNull=false)
	public String channel;
	
	@DatabaseField(canBeNull=false)
	public String channelpass;
	
	//boolean
	@DatabaseField(canBeNull=false)
	public String guesscharset;
	
	//boolean
	@DatabaseField(canBeNull=false)
	public String reconnect;
	
	//boolean
	@DatabaseField(canBeNull=false)
	public String ssl;
	
	//boolean
	@DatabaseField(canBeNull=false)
	public String autoidentify;
	
	@DatabaseField(canBeNull=false)
	public String nickserv;
	
	@DatabaseField(canBeNull=false)
	public String nickpass;
	
	@DatabaseField(canBeNull=false)
	public String perform;
	
	@DatabaseField(canBeNull=false)
	public String encodingoverride;
	
	@DatabaseField(canBeNull=false)
	public String encodingsend;
	
	@DatabaseField(canBeNull=false)
	public String encodingreceive;
	
	@DatabaseField(canBeNull=false)
	public String encodingserver;
	
	@DatabaseField(canBeNull=false)
	public String reconnectinterval;
	
	@DatabaseField(canBeNull=false)
	public String reconnectretries;

    @DatabaseField(canBeNull=false)
    public String language;

	public static AccountData createForInsert(){

        AccountData ad = new AccountData();

        ad.joinleave = "false";
        ad.reconnect = "true";
        ad.ssl = "false";
        ad.autoidentify = "false";
        ad.nickserv = "NickServ";
        ad.encodingoverride = "UTF-8";
        ad.encodingsend = "UTF-8";
        ad.encodingreceive = "UTF-8";
        ad.encodingserver = "UTF-8";
        ad.reconnectinterval = "30";
        ad.reconnectretries = "12";
        ad.language = "en";

        ad.channel = "";
        ad.channelpass ="";
        ad.auth = "";

        return ad;

    }
}
