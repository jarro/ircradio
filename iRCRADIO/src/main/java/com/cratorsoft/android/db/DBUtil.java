package com.cratorsoft.android.db;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.cratorsoft.android.util.BackupUtil;
import com.earthflare.android.ircradio.Globo;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DBUtil {

	
	public static void initDBHandler(){
		
		HandlerThread dbthread = new HandlerThread("dblifecycle");
		dbthread.start();
		Globo.dbHandler = new Handler(dbthread.getLooper());				
		
	}
	
	
	public static void openDB(final Context ctx) {
	
		Runnable r = new Runnable() {
			@Override
			public void run() {
                BackupUtil.backuprestore();
				Globo.dbHelper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
				Globo.db = Globo.dbHelper.getWritableDatabase();				
			}
		};
		Globo.dbHandler.post(r);		
		
	}
	
	
	public static void purgeDBHandler(){
	    
	    Globo.dbHandler.removeCallbacksAndMessages(null);
	    
	}
	

}
