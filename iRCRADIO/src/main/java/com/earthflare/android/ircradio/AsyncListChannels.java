package com.earthflare.android.ircradio;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.earthflare.android.logmanager.LogManager;

public class AsyncListChannels extends AsyncTask<Bundle,Void,Void>{

	ListChannelsAdapter adapter;
	Context ctx;
	DialogProgress dp;
	long network;
	
	public AsyncListChannels(Context ctx, ListChannelsAdapter adapter, long network) {
		super();
		this.ctx = ctx;
		this.adapter = adapter;
		this.network = network;
    }

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		adapter.setList(network);
		try {
			dp.dismiss();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		dp = new DialogProgress(ctx);
		
		dp.show();
	}

	@Override
	protected Void doInBackground(Bundle... bundle) {
		
		String action = bundle[0].getString("action");
		if (action.equals("sortchannels")) {
			LogManager.serverLogMap.get(network).sortChannels();
			
		}else if (action.equals("sortusers")) {
			LogManager.serverLogMap.get(network).sortUsers();
			
		}else if (action.equals("filter")) {
			LogManager.serverLogMap.get(network).filterChannels(bundle[0]);
			
		}else if (action.equals("synch")) {
			
			LogManager.serverLogMap.get(network).synchChannels();
			
		}
		
		return null;
	}

}
