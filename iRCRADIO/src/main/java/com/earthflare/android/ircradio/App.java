package com.earthflare.android.ircradio;

import com.cratorsoft.android.customTranslations.CustomTransManager;
import com.cratorsoft.android.db.DBUtil;
import com.cratorsoft.android.taskmanager.BusyAsyncTaskBase;
import com.cratorsoft.android.ttslanguage.TTSLanguageManager;

import android.content.res.Configuration;

 
public class App extends android.app.Application{



	@Override
	public void onCreate() {		
		super.onCreate();
		
		Globo.ctx = this;
        GloboUtil.globalizePrefs(this);
        GloboUtil.globalizeChannelPrefs(this);
		DBUtil.initDBHandler();
        BusyAsyncTaskBase.setDefaultHandler(Globo.dbHandler);
		DBUtil.openDB(this);

		//copy lang assets to external storage
		CustomTransManager.moveAllLookupsToPublicStorage();

        TTSLanguageManager.INSTANCE.backgroundRefresh();

			
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	

}
