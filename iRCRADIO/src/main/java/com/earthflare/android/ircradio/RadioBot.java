package com.earthflare.android.ircradio;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;

import com.cratorsoft.android.language.IRCText;
import com.cratorsoft.android.language.LangMan;
import com.cratorsoft.android.language.ParsedLanguageAndEngine;
import com.cratorsoft.android.language.SCLM;
import com.earthflare.android.logmanager.LogManager;
import com.earthflare.android.logmanager.ServerLog;
import com.earthflare.android.trust.TrustAllSSLSocketFactory;


public class RadioBot extends PircBot{

  public long botID;
  public Boolean connecting = false;
  public Boolean killing = false;
  private Context ctx;
  public Map<String,Map<String,String>> channelMap = new HashMap<String,Map<String,String>>();
  public String botNetwork;
  public String accountName;
  private int reconnectcounter;
  private int reconnectinterval;
  private int reconnectretries;
  private Intent reconnectintent; 
  private boolean reconnect;
  
  private boolean autoidentify;
  private String nickserv;
  private String nickpass;
  private String perform;
  
  private boolean guesscharset;
  private boolean ssl;
  private ServerLog serverLog;
  
  public RadioBot(Context ctx, String nick,  String server, String port, long botID, String accountName, boolean guesscharset, boolean reconnect, boolean ssl) {
    
    this.ctx = ctx;    
    this.botNetwork = server;
    connecting = true;
    reconnectcounter = 0;
    this.reconnect = reconnect;
    this.guesscharset = guesscharset;
    this.ssl = ssl;
    
    this.setName(nick);
    this.setFinger("");
    this.setLogin(nick);
    
    String vname = "";
    
    try {
		PackageManager pm = ctx.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
		vname = pi.versionName;
	} catch (NameNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
    this.setVersion("Android IRC Radio V:" + vname  );
    this.botID = botID;
    this.accountName = accountName;
  }
  
  public void startBot(final Intent intent ) {
	  
	    reconnectintent = intent;	  
	  
	    Runnable startIrc = new Runnable() {
		    @Override
		    public void run() {
		      // TODO Auto-generated method stub
		    	BotManager.refreshUI(ctx);		      
		    	connectIrc(intent);
		    }		    
		  };
	    new Thread (startIrc).start();
	    
  }
  
  
  
  public void parseinput(String message, String channel) {		
	  if (Globo.chatlogActType.equals("server") ) {
		  if (message.length() == 0){return;}
		  if (!parsecommand(message,channel,"server1")) {
		      if (message.startsWith("/")){
		    	  this.sendRawLine(message.substring(1));
		      }else{
		    	  this.sendRawLine(message);
		      }
			}
		  return;
	  }
	  
	  if ( message.startsWith("/")) {
			if (!parsecommand(message,channel,"channel")) {
		      this.sendRawLine(message.substring(1));
			}
	  }else{
		  this.sendMessage(channel, message);
		  LogManager.writeChannelLog(MessageType.STANDARD, this.getNick(), "<" + this.getNick() + "> " + message,botID,channel, accountName, ctx, true, this.getNick());
	  }
  }  

  
  
  @Override
protected void onInvite(String targetNick, String sourceNick,
		String sourceLogin, String sourceHostname, String channel) {
	//if i was invited to a channel treat as private message
	//else treat as action
	  
	  String voiceout;
	  String textout;
	  String targetchannel;

      ParsedLanguageAndEngine plae = SCLM.INSTANCE.getChannelPLAE(botID,channel);

	  if ( targetNick.equals(this.getNick())   ) {	  
		  voiceout = sourceNick + " " + LangMan.INSTANCE.speechLookup(IRCText.invitedyouto, plae.locale)  + " " + removeChannelHash(channel);
		  textout = "* " + sourceNick + " " +LangMan.INSTANCE.logLookup(IRCText.invitedyouto,plae.locale)  + " " + channel;
		  targetchannel = sourceNick;
	      queueActionMessageFront(voiceout  , plae );
		  
		  
	  }else{
		  textout = "* " + sourceNick + " " + LangMan.INSTANCE.logLookup(IRCText.invited,plae.locale) + targetNick + LangMan.INSTANCE.logLookup(IRCText.tojoinchannel,plae.locale) + " " + channel;
		  targetchannel = channel;
		  
		  if (Globo.voiceServer == botID && targetchannel.equals(Globo.voiceChannel) && Globo.ttsspeak_invite ) {
              voiceout = sourceNick + " " + LangMan.INSTANCE.speechLookup(IRCText.invited, plae.locale) + targetNick + LangMan.INSTANCE.speechLookup(IRCText.tojoinchannel, plae.locale) + " " + channel;
		      queueActionMessage(voiceout  , plae );
		  }  
		  
	  }
	  
	  LogManager.writeChannelLog(MessageType.INVITE, sourceNick, textout  ,botID, targetchannel , accountName, ctx, false, this.getNick());
	  
}
  
private void identifyNick() {
	
	if (autoidentify) {
		this.sendMessage(nickserv, "identify " + nickpass);
	}
	
}
  
private void performScript() {
	
	String lines[] = perform.split("[\\r?\\n]+");

	for (String line: lines ) {
		parsecommand(line,"","server");
	}
	
}

@Override
protected void onMode(String channel, String sourceNick, String sourceLogin,
		String sourceHostname, String mode) {
	//echo mode as action

    ParsedLanguageAndEngine plae = SCLM.INSTANCE.getChannelPLAE(botID,channel);
	String textout = "* " + sourceNick + " " + LangMan.INSTANCE.logLookup(IRCText.setsmode,plae.locale) + " " + mode;
	
	LogManager.writeChannelLog(MessageType.MODE, sourceNick, textout,botID, channel , accountName, ctx, false, this.getNick());
	  
	if (Globo.voiceServer == botID && channel.equals(Globo.voiceChannel) && Globo.ttsspeak_mode ) {
        String voiceout = "* " + sourceNick + " " + LangMan.INSTANCE.speechLookup(IRCText.setsmode, plae.locale) + " " + mode;
	    queueActionMessage(voiceout  , SCLM.INSTANCE.getChannelPLAE(botID, channel) );
	}
	
}

@Override
protected void onNotice(String sourceNick, String sourceLogin,
		String sourceHostname, String target, String notice) {
	  
	  String channel;
	  int type;
	  if ( target.equals(this.getNick())   ) {
		  channel = sourceNick;
		  type = MessageType.PRIVATE_NOTICE;
	  }else{
		  channel = target;
		  type = MessageType.NOTICE;
	  }

	  LogManager.writeChannelLog(type, sourceNick, "* " + sourceNick + " " + notice,botID, channel , accountName, ctx, false, this.getNick());
	  
	  if (Globo.voiceServer == botID && target.equals(Globo.voiceChannel) && Globo.ttsspeak_notice) {
          ParsedLanguageAndEngine plae = SCLM.INSTANCE.getChannelPLAE(botID,channel);
	      queueActionMessage(LangMan.INSTANCE.speechLookup(IRCText.notice, plae.locale) + " " + LangMan.INSTANCE.speechLookup(IRCText.from, plae.locale) + " " + sourceNick + ". " + notice  , plae );
	  }else if(Globo.voiceServer == botID && target.equals(this.getNick()) && Globo.ttsspeak_privatenotice) {
          ParsedLanguageAndEngine plae = SCLM.INSTANCE.getServerPLAE(botID);
          queueActionMessage(LangMan.INSTANCE.speechLookup(IRCText.notice, plae.locale) + " " +  LangMan.INSTANCE.speechLookup(IRCText.from, plae.locale) + " " + sourceNick + ". " + notice  , plae );
	  }
	  
	  
}

private boolean parsecommand (String message, String channel, String chanorserver) {
	  
	  
	
	  String[] parts = message.split(" ");
	   
	  
	  String command = "";
	  String target = "";	  
	try {
		command = parts[0];
		target = parts[1];
	} catch (Exception e) {		
	}
      
	  String textmessage = "";
	  int offset = (command.length() + target.length());
	  if (offset + 2 <= message.length() ) {
		  offset += 2;
	  }else if (offset + 1 <= message.length() ) {
		  offset += 1;
	  }
	  textmessage = message.substring(offset);
	  String longmessage = "";
	  if (target.length() > 0 ) {longmessage = target;}  
	  if (textmessage.length() > 0) {longmessage += " " + textmessage;}
	  
	  
	  //private message
	  if (command.equalsIgnoreCase("/msg") || command.equalsIgnoreCase("/privmsg") || command.equalsIgnoreCase("/query")) {
          
		  if (!target.equals("")) {
			  this.sendMessage(target, textmessage);              
        	  LogManager.writeChannelLog(MessageType.STANDARD, this.getNick(), "<" + this.getNick() + "> " +  textmessage,botID,parts[1], accountName, ctx, true, this.getNick());
          }
          return true;
	  }
	  
	  
	  //me
	  if (command.equalsIgnoreCase("/me") && chanorserver.equals("channel") ){
		  
		  if (!target.equals("")) {
			  this.sendAction(channel, longmessage);              
        	  LogManager.writeChannelLog(MessageType.ACTION, this.getNick(), "* " + this.getNick() + " " +  longmessage,botID,channel, accountName, ctx, true, this.getNick());
          }
          return true;
	  }
	  
	  //notice
	  if (command.equalsIgnoreCase("/notice")){
		  
		  if (!target.equals("")) {
			  this.sendNotice(target, textmessage);              
        	  LogManager.writeChannelLog(MessageType.NOTICE, this.getNick(), "* " + this.getNick() + " " +  textmessage,botID,target, accountName, ctx, true, this.getNick());
          }
          return true;
	  }
	  
	  //part
	  if (command.equalsIgnoreCase("/part")){
		  
		  if (!target.equals("")) {
			  this.partChannel(target, textmessage);                      	  
          }else if ( chanorserver.equals("channel") ) {
        	  this.partChannel(channel, "");
          }
          return true;
	  }
	  
	  //quit
	  if (command.equalsIgnoreCase("/quit")){
		  this.quitServer(longmessage);                      	  
          return true;
	  }
	  
	  //invite
	  if (command.equalsIgnoreCase("/invite")){
		  this.sendInvite(target, textmessage);                      	  
          return true;
	  }
	  
	  //topic
	  if (command.equalsIgnoreCase("/topic")){
		  this.setTopic(target, textmessage);                      	  
          return true;
	  }
	  
	  //mode
	  if (command.equalsIgnoreCase("/mode")){
		  this.setMode(target, textmessage);                      	  
          return true;
	  } 
	  
	  //list
	  if (command.equalsIgnoreCase("/list")){
		  this.startListChannels(longmessage);                      	  
          return true;
	  }
	  
	  //join
	  if (command.equalsIgnoreCase("/join") || (command.equalsIgnoreCase("/j"))) {
		  this.joinChannel(longmessage);
		  return true;
	  }
	  
	  
	//quote
	  if (command.equalsIgnoreCase("/quote") ) {
		  this.sendRawLine(longmessage);
		  return true;
	  }
	  
	  
	  return false;
  }
  


@Override
protected void onServerResponse(int code, String response) {
	
		
	// strip leading nick info
	if (response.startsWith(this.getNick())) {
		response = response.substring(this.getNick().length() );
	}
	if (response.startsWith(" :")) {
		response = response.substring(2);
	}
	
	
	LogManager.writeServerLog("response",response,"",botID, accountName, ctx);
	
	
	// 
	if (code == 5) {
		 String[] strarray = response.split(" ");
		 for (String str : strarray) {
			 if (str.startsWith("NICKLEN=")) {				 				 
				 String nicklen = str.substring(8);
				 int i = 9;
				 try { i = Integer.parseInt(nicklen) ; }
				 catch (NumberFormatException e ){
					 //move along
				 }
				 String nick = this.getNick(); 
				 if (nick.length() > i) {
				   nick = (nick.substring(0,i));
				   this.setNick (nick);
				 }  
			 }
			 		
		 }
	}
}



@Override
protected void onPrivateMessage(String sender, String login, String hostname,
		String message) {
	
	LogManager.writeChannelLog(MessageType.PRIVATE, sender, "<" + sender + "> " + message,botID,sender, accountName, ctx, false, this.getNick());	
	
	if (Globo.voiceServer == botID && sender.equals(Globo.voiceChannel) && Globo.ttsspeak_privatemessage) {  
	    queueStandardMessage(sender,  message, SCLM.INSTANCE.getChannelPLAE(botID, sender) );
	}
	
}


  
  public void onMessage(String channel, String sender, String login, String hostname, String message) {
     	  
	  
	  LogManager.writeChannelLog(MessageType.STANDARD, sender, "<" + sender + "> " + message,botID,channel, accountName, ctx, false, this.getNick());    
	  
	  
	  
	  if (Globo.voiceServer == botID && channel.equals(Globo.voiceChannel) && Globo.ttsspeak_message ) {  
	        queueStandardMessage(sender,  message, SCLM.INSTANCE.getChannelPLAE(botID, channel) );
	  }
	  
	  
  }


  
  
  @Override
protected void onUnknown(String line) {
	// TODO Auto-generated method stub
	
	  if (line.startsWith("NOTICE AUTH :")) {
		  LogManager.writeServerLog("notice", line.substring(13), "", botID, accountName, ctx);
	  }
	  
}

@Override
  synchronized protected void onConnect() {
    
    
	//add autojoin channels
    addautojoins();
    
    //join channellist 
    synchronized (channelMap){
    for (String str : channelMap.keySet() ) {
    	
    	Map<String,String> params = channelMap.get(str);
    	String channelpass = params.get("channelpass");
    	
    	this.joinChannel(str,channelpass);
    	
    }
    }
    
    //refresh active view
    connecting = false;
    reconnectcounter = 0;           
    
    BotManager.refreshUI(ctx);
    
    //do autoidentify
    identifyNick();
    //do perform
    performScript();
    
    
  }


  @Override
  synchronized protected void onDisconnect() {
    
	//check if reconnect allowed and counter < 10 and not killing
    if (reconnect && reconnectcounter <= reconnectretries && !killing) {
    	//retry
    	
    	reconnectcounter++;
    	connecting = true;
    	Runnable startIrc = new Runnable() {
		    @Override
		    synchronized public void run() {
		      // TODO Auto-generated method stub
		    	BotManager.refreshUI(ctx);
		    	try {
					wait(reconnectinterval * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	connectIrc(reconnectintent);
		    }		    
		  };
	    new Thread (startIrc).start();
	    return;
    }
	  
	  
	connecting = false;    
    if (this.killing == false) {    	     	
    	Globo.botManager.ircBots.remove(botID);    	       
        this.dispose();
    	BotManager.refreshUI(ctx);    	
    }
    
  
  
  }


  @Override
  protected void onJoin(String channel, String sender, String login,
      String hostname) {

    ParsedLanguageAndEngine plae = SCLM.INSTANCE.getChannelPLAE(botID, channel);
    
    if ( (this.getNick().equals(sender))) {
        getChannel(channel);

    	LogManager.writeChannelLog(MessageType.ENTERED,sender,"* " + LangMan.INSTANCE.logLookup(IRCText.nowtalkingin,plae.locale)  + " " + channel + " " + LangMan.INSTANCE.logLookup(IRCText.as,plae.locale) + " "  + sender ,botID,channel, accountName, ctx, false, this.getNick());
    	queueActionMessage( LangMan.INSTANCE.speechLookup(IRCText.youhavejoined, plae.locale)  + " " + removeChannelHash(channel) + " " + LangMan.INSTANCE.speechLookup(IRCText.as, plae.locale) + " " + sender , plae );
    	BotManager.refreshUI(ctx);
    }else{
    
      if (channelMap.containsKey(channel)) {
    	  if (channelMap.get(channel).get("joinleave").equals("true") && Globo.ttsspeak_join ) {


    		  if (Globo.voiceServer == botID && channel.equals(Globo.voiceChannel) && Globo.ttsspeak_join ) {

    		    queueActionMessage( sender + " " + LangMan.INSTANCE.speechLookup(IRCText.hasjoinedthechannel, plae.locale)  , plae );
    		  }
    	    LogManager.writeChannelLog(MessageType.JOIN, sender, "* " + sender + " " + LangMan.INSTANCE.logLookup(IRCText.hasjoinedthechannel,plae.locale)  ,botID,channel, accountName, ctx, false, this.getNick());
    	}
      }
    }
    
  }

  
  



@Override
  protected void onAction(String sender, String login, String hostname,
      String target, String action) {

	  int type;
	  if ( target.equals(this.getNick())   ) {
		  target = sender;
		  type = MessageType.PRIVATE_ACTION;
	  }else{
		  type = MessageType.ACTION;
	  }
	
	  
	  
	LogManager.writeChannelLog(type, sender, "* " + sender + " " + action ,botID, target, accountName,ctx, false, this.getNick());
    
    if (Globo.voiceServer == botID && target.equals(Globo.voiceChannel) && Globo.ttsspeak_action ) {    
      queueActionMessage(sender + " " + action, SCLM.INSTANCE.getChannelPLAE(botID, target));
    }
    
  }

  @Override
  protected void onKick(String channel, String kickerNick, String kickerLogin,
      String kickerHostname, String recipientNick, String reason) {

    ParsedLanguageAndEngine plae = SCLM.INSTANCE.getChannelPLAE(botID,channel);

    if (this.getNick().equals(recipientNick) ) {

      LogManager.writeChannelLog(MessageType.KICK, "", "* " + kickerNick + " " + LangMan.INSTANCE.logLookup(IRCText.kicks,plae.locale) + " " + recipientNick + " : " + reason ,botID, channel, accountName,ctx, false, this.getNick());
      queueActionMessage( LangMan.INSTANCE.speechLookup(IRCText.youwerekickedby, plae.locale) + " " + kickerNick + " " + LangMan.INSTANCE.speechLookup(IRCText.from, plae.locale) + " " + removeChannelHash(channel) + " " + LangMan.INSTANCE.speechLookup(IRCText.for_, plae.locale) + " " + reason , plae );
      this.channelMap.remove(channel);
      BotManager.refreshUI(ctx);
      return;
    }
    
    LogManager.writeChannelLog(MessageType.KICK, "", "* " + kickerNick + " " + LangMan.INSTANCE.logLookup(IRCText.kicks,plae.locale) + " " + recipientNick + " : " + reason ,botID, channel, accountName,ctx, false, this.getNick());
    if (Globo.voiceServer == botID && channel.equals(Globo.voiceChannel) && Globo.ttsspeak_kick ) {    	
    	queueActionMessage(recipientNick + " " + LangMan.INSTANCE.speechLookup(IRCText.waskickedby, plae.locale) + " " + kickerNick + " " + LangMan.INSTANCE.speechLookup(IRCText.for_, plae.locale) + " " + reason , plae );
    }
    
    
    
  }

  
  
  

  @Override
  protected void onPart(String channel, String sender, String login,
      String hostname) {

    ParsedLanguageAndEngine plae = SCLM.INSTANCE.getChannelPLAE(botID,channel);
	  
    if (this.getNick().equals(sender)) {
    	this.channelMap.remove(channel);
    	BotManager.refreshUI(ctx);
    	return;
    }
    
    if (channelMap.get(channel).get("joinleave").equals("true") ) {
    	LogManager.writeChannelLog(MessageType.PART, "", "* " + sender + " " + LangMan.INSTANCE.logLookup(IRCText.hasleftthechannel,plae.locale)  ,botID, channel, accountName,ctx, false, this.getNick());
        if (Globo.voiceServer == botID  &&  channel.equals(Globo.voiceChannel) && Globo.ttsspeak_part) {        
    		queueActionMessage(sender + " " + LangMan.INSTANCE.speechLookup(IRCText.hasleftthechannel, plae.locale)  , plae );
        }
    }
  }


  @Override
  protected void onQuit(String sourceNick, String sourceLogin,
      String sourceHostname, String reason) {
      
	  boolean  nickInVoiceChan = false;
	   
	  //write to all message queues with join-leave enabled and nick is a member.  
	  String[] channels =  this.getChannels();
     for (String channel: channels) {
    	 User[] users = this.getUsers(channel);
    	 for (User user: users) {
    		 if (user.equals(sourceNick)) {
    			 //add to channel log if join leave enabled
    			 if (channelMap.get(channel).get("joinleave").equals("true")) {
                     ParsedLanguageAndEngine plae = SCLM.INSTANCE.getChannelPLAE(botID,channel);
    				 LogManager.writeChannelLog(MessageType.QUIT, sourceNick, "* " + sourceNick + " " + LangMan.INSTANCE.logLookup(IRCText.hasquit,plae.locale) + " : " + reason, this.botID, channel, accountName,ctx, false, this.getNick());
    				 if (channel.equals(Globo.voiceChannel) && this.botID == Globo.voiceServer && Globo.ttsspeak_quit){
    					 queueActionMessage( sourceNick + " " + LangMan.INSTANCE.speechLookup(IRCText.hasquit, plae.locale)  + " " + reason , plae );
    				 }
    			 }
    		 }
    	 }
    	 
     }
  
    
    
  }



@Override
protected void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
	
	LogManager.writeChannelLog(MessageType.TOPIC, "", topic, this.botID, channel, accountName, ctx, false, this.getNick());
	
	Map<String,String> channelinfo = this.getChannel(channel);
	channelinfo.put("topic", topic);
	
	
}

private void queueStandardMessage (String sender,  String message, ParsedLanguageAndEngine plae) {
	    MessageBundle mb = new MessageBundle();
	    mb.sender = sender;
	    mb.message = message;
	    mb.plae = plae;
	    mb.type = "standard";
        MessageQueue.INSTANCE.add(mb);
  }
  
  private void queuePrivateMessage (String sender,  String message, ParsedLanguageAndEngine plae) {
	    MessageBundle mb = new MessageBundle();
	    mb.sender = sender;
	    mb.message = message;
	    mb.type = "private";
        mb.plae = plae;
        MessageQueue.INSTANCE.addfront(mb);
 }
  

  private void queueActionMessage (String message,  ParsedLanguageAndEngine plae ) {
	    MessageBundle mb = new MessageBundle();	    
	    mb.message = message;
	    mb.type = "action";
	    mb.plae = plae;
        MessageQueue.INSTANCE.add(mb);
  }
  
  private void queueActionMessageFront (String message,  ParsedLanguageAndEngine plae ) {
	    MessageBundle mb = new MessageBundle();	    
	    mb.message = message;
	    mb.type = "action";
        mb.plae = plae;
        MessageQueue.INSTANCE.addfront(mb);
}

  public void returnError(String message) {
	    Message msg = new Message();
	    Bundle data = new Bundle();
	    data.putString("action", "error");
	    data.putString("message", message);	    
	    msg.setData(data);
        if(Globo.actAccountListVisible) {
            Globo.handlerAccountList.sendMessage(msg);
        }
	    
 }
  
  synchronized public void connectIrc(Intent connectIntent) {
	    String server = connectIntent.getStringExtra("server");
	    String nick = connectIntent.getStringExtra("nick");
	    String port = connectIntent.getStringExtra("port");	    
	    String serverpass = connectIntent.getStringExtra("serverpass");	    
	    String auth = connectIntent.getStringExtra("auth");
	    boolean ssl = connectIntent.getBooleanExtra("ssl", false);
	    
	    autoidentify = connectIntent.getBooleanExtra("autoidentify", false);
	    nickserv = connectIntent.getStringExtra("nickserv");
	    nickpass = connectIntent.getStringExtra("nickpass");
	    perform = connectIntent.getStringExtra("perform");
	    
	    boolean encodingoverride = connectIntent.getBooleanExtra("encodingoverride", false);
	    String encodingsend = connectIntent.getStringExtra("encodingsend");
	    String encodingreceive = connectIntent.getStringExtra("encodingreceive");
	    String encodingserver = connectIntent.getStringExtra("encodingserver");


	    String strReconnectinterval = connectIntent.getStringExtra("reconnectinterval");
        String strReconnectretries = connectIntent.getStringExtra("reconnectretries");
        try{
            reconnectinterval = Integer.valueOf(strReconnectinterval);
        }catch(Exception e){
            reconnectinterval = 30;
        }

        try{
          reconnectretries = Integer.valueOf(strReconnectretries);
        }catch(Exception e){
          reconnectretries = 12;
        }


	    
	    //test if bot connected
	   
	    if ( !this.isConnected() ) {
	    
	    	
	    }
	    
	    if (encodingoverride) {
	    try {
			this.setEncodingSend(encodingsend);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block			
		}
		
		try {
			this.setEncodingReceive(encodingreceive);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block			
		}
	    }
	    	
	    this.setAutoNickChange(true);    
	    
	    
	    		
	    
	    // Enable debugging output.
	    this.setVerbose(false);
	    
	    //set port to 6667 default if invalid
	    int intport = 6667;
	    if (isInt(port)) {
	      intport = Integer.parseInt(port);
	    }
	    

	    
	    try {	       
	        //this.startIdentServer();
              TrustAllSSLSocketFactory socketFactory = null;
	    	if (ssl) {
	    	  socketFactory = TrustAllSSLSocketFactory.getTrustAllSocket();	    	 
	    	}
	    	this.connect(server, intport, serverpass, socketFactory );
	       //break;
	       
	    } catch (NickAlreadyInUseException e) {	      
	      returnError( ctx.getString(R.string.error_nickinuse));
	      connecting = false;
	      reconnect = false;
	      onDisconnect();
	      return;
	    } catch (IOException e) {
	      e.printStackTrace();
	      returnError(ctx.getString(R.string.error_title_networkerror) + ": " + ctx.getString(R.string.error_ircservercouldnotbereached));
	      connecting = false;
	      reconnect = false;
	      onDisconnect();          
	      return;
	    } catch (IrcException e) {	                  
	      returnError(ctx.getString(R.string.error_ircservererror));
	      connecting = false;
	      reconnect = false;
	      onDisconnect();
	      //send irc error
	      return;
	    } 
	    
	    
	    //send raw auth line
	    if (!auth.equals("")) {
	      this.sendRawLine(auth);
	    }
	    
	    
	                
	  }
  
  
  public static boolean isInt(String i) {
	    try{
	      Integer.parseInt(i);
	      return true;
	    } catch(NumberFormatException nfe){
	      return false;
	    }
	  }
  
  
  public boolean inChannel (String channel) {
	  String[] chans = this.getChannels();	  
	  Arrays.sort(chans);
	  if ( Arrays.binarySearch(chans, channel) >= 0 ) {
		  return true;
	  }else{
		  return false;
	  }
	  
  }
  
  
public void disconnectserver() {
	
	reconnect = false;
	
	if (connecting) {
	 
		Runnable stopIrc = new Runnable() {
		    @Override
		    public void run() {
		      Globo.botManager.ircBots.remove(botID);    	             
		  	  BotManager.refreshUI(ctx);
		  	  RadioBot.this.dispose();
		    }		    
		  };
	    new Thread (stopIrc).start();
		
		
	}else{
	  this.quitServer("Got to keep moving!!");
	}
	
}
  

public Map<String,String> getChannel (String channel) {
        
	   
	    Map<String,String> channelinfo;						
		
		synchronized(this.channelMap){
		if (!this.channelMap.containsKey(channel) ) {
			channelinfo = new HashMap<String,String>();
			channelinfo.put("channelpass", "");
			channelinfo.put("joinleave", "true");
		   this.channelMap.put(channel, channelinfo);
		}else{
		   channelinfo = this.channelMap.get(channel);	
		}
		}
		return channelinfo;
  }

private void addautojoins() {
	//add all channels with autojoin enabled to channellist
	String sql = "Select * from channel where accountid = " + this.botID + " and autojoin = 'true' ";
	
	Cursor c = Globo.db.rawQuery(sql, null);
	 
	String channelpass;
	String channel;
	String joinleave;
	String language;
	
	while (c.moveToNext() ) {
	      
		  channelpass = c.getString(3);      	      
	      channel = c.getString(2).toLowerCase();	      
	      joinleave = c.getString(4);	      
	      language = c.getString(6);
	      
	      //add channelinfo to join channel
	      Map<String,String> channelinfo = new HashMap<String,String>();
			channelinfo.put("channelpass", channelpass);
			channelinfo.put("joinleave", joinleave);						
			channelinfo.put("language", language);
	        channelMap.put(channel,channelinfo);
	}
	
	c.close();
	
	
}



@Override
protected void onChannelInfo(String channel, int userCount, String topic) {
	// TODO Auto-generated method stub
	
	Channel cl = new Channel(channel,userCount,topic);
	synchronized(serverLog.channels){
	  serverLog.channels.addLast(cl);	  
	}
	
	
	
	if (Globo.actListChannelsVisible && this.botID == Globo.listChannelsNetwork){
		serverLog.newchannels = true;
		/*
		//send channel as message
		Message msg = new Message();
	    Bundle data = new Bundle();
	    data.putString("action", "addline");
	    data.putLong("network", botID);
	    data.putSerializable("chaninfo", cl);	    
	    msg.setData(data);
	    Globo.handlerListChannels.sendMessage(msg);
	    */
	}else {
		synchronized(serverLog.channelsProcessed) {
		  serverLog.channelsProcessed.addLast(cl);
		}
	}
	
	
}

public void startListChannels (String params) {
	serverLog = LogManager.getServer(botID, accountName);
	synchronized(serverLog.channels){
	  serverLog.channels.clear();
	}
	this.listChannels(params);
}



 
private String removeChannelHash (String channel) {
	
	if ( channel.startsWith("#", 0) ) {
		String result = channel.substring(1);
		return result;
	}else{
	    return channel;	
	}
	
}

}
