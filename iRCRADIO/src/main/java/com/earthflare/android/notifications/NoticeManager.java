package com.earthflare.android.notifications;

import java.util.Date;
import java.util.LinkedList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.cratorsoft.android.aamain.ActMain;
import com.cratorsoft.android.aamain.ActMain_;
import com.earthflare.android.ircradio.ActChatLog;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.NavItem;
import com.earthflare.android.ircradio.R;

public class NoticeManager {

	
	static LinkedList<Notice> noticeList = new LinkedList<Notice>();
	private final int maxNotice = 50;
	
	
	public static void addNamed(Context ctx, String accountname, String message, long network, String channel, String sender, long uid) {
		
		Notice n = new Notice();
		
		n.accountname = accountname;
		n.message = message;
		n.network = network;
		n.channel = channel;
		n.sender = sender;
		n.uid = uid;
		
		n.time = new Date();
		n.noticetype = "named";
		
		noticeList.addFirst(n);
		
		
		if (Globo.pref_notification_channelmessage) {
		NotificationManager mManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent statusintent = new Intent(ctx, ActMain_.class);
        statusintent.setAction(ActMain.LAUNCH_NOTIFICATIONLINK);
		statusintent.putExtra("uid",uid);
        statusintent.putExtra("accountname", accountname);
        statusintent.putExtra("network", network);
        statusintent.putExtra("channelname", channel);
        statusintent.putExtra("acttype", NavItem.CHANNEL);
        statusintent.putExtra("notificationlink", true);
        
		//Notification notification = new Notification(R.drawable.ic_stat_gg_white_notification,accountname + " " + channel + " " + sender, System.currentTimeMillis());


		Notification notification = new Notification.Builder(ctx)
				.setSmallIcon(R.drawable.ic_stat_gg_white_notification)
				.setContentTitle("IRC Radio")
				.setContentText(ctx.getString(R.string.ui_messagefrom) + " " + sender)
				.setContentIntent(PendingIntent.getActivity(ctx, 0, statusintent, PendingIntent.FLAG_CANCEL_CURRENT))
				.setDefaults( Notification.DEFAULT_SOUND)
				.build();

		/*
		notification.setLatestEventInfo(ctx,"IRC Radio",
				ctx.getString(R.string.ui_messagefrom) + " " + sender,
				PendingIntent.getActivity(ctx, 0, statusintent, PendingIntent.FLAG_CANCEL_CURRENT));
		notification.defaults |= Notification.DEFAULT_SOUND;
		*/
        mManager.notify(2, notification);

		}
        
		if (Globo.actNoticeVisible) {
			postToActNotice(n);
		}
	  	  
	}
	
   public static void addPrivate(Context ctx, String accountname, String message, long network, String channelname, String sender, long uid) {
		
		Notice n = new Notice();
		
		n.accountname = accountname;
		n.message = message;
		n.network = network;
		n.channel = channelname;
		n.sender = sender;
		n.uid = uid;
		
		n.time = new Date();
		n.noticetype = "pmed";
		
		noticeList.addFirst(n);
		
		if (Globo.pref_notification_newprivatemessage) {
		//send notification to status bar and beep if preference enabled
		NotificationManager mManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent statusintent = new Intent(ctx, ActMain_.class);
        statusintent.setAction(ActMain.LAUNCH_NOTIFICATIONLINK);
        statusintent.putExtra("uid",uid);
        statusintent.putExtra("accountname", accountname);
        statusintent.putExtra("network", network);
        statusintent.putExtra("channelname", channelname);
        statusintent.putExtra("acttype", NavItem.CHANNEL);
        statusintent.putExtra("notificationlink", true);


		//Notification notification = new Notification(R.drawable.ic_stat_gg_white_notification,accountname + " " + sender, System.currentTimeMillis());

			Notification notification = new Notification.Builder(ctx)
					.setSmallIcon(R.drawable.ic_stat_gg_white_notification)
					.setContentTitle("IRC Radio")
					.setContentText(ctx.getString(R.string.ui_messagefrom) + " " + sender)
					.setContentIntent(PendingIntent.getActivity(ctx, 0, statusintent, PendingIntent.FLAG_CANCEL_CURRENT))
					.setDefaults( Notification.DEFAULT_SOUND)
					.build();

		/*
		notification.setLatestEventInfo(ctx,"IRC Radio",
				ctx.getString(R.string.ui_messagefrom) + " " + sender,
				PendingIntent.getActivity(ctx, 0, statusintent, PendingIntent.FLAG_CANCEL_CURRENT));
		notification.defaults |= Notification.DEFAULT_SOUND;
		*/
        mManager.notify(2, notification);


		}
        
        if (Globo.actNoticeVisible) {
			postToActNotice(n);
		}
        
	}
	
	
   private static void postToActNotice(Notice note) {
	
	   Message msg = new Message();
	   Bundle data = new Bundle();
	   data.putString("action", "add");
	   data.putSerializable("notice", note);
	   msg.setData(data);
	   Globo.handlerNotice.sendMessage(msg);
	   
   }
   
   synchronized public void addToList(Notice notice){
		noticeList.addFirst(notice);	
		
		if (noticeList.size() > this.maxNotice) {
			noticeList.removeLast();
		}
		
		
}
}
