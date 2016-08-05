package com.earthflare.android.ircradio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.earthflare.android.logmanager.LogManager;

public class TemplateChannel extends ActMessanger {

	// ACTION BAR
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;

	protected void initDrawer() {

		this.mDrawerLayout = (DrawerLayout) this
				.findViewById(R.id.drawer_layout);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.drawer_open, R.string.drawer_close);
		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {		
		super.onPostCreate(savedInstanceState);
		
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerToggle.syncState();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.channelmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		
		switch (item.getItemId()) {

		case R.id.menupreferences:
			startActivity(new Intent(this, EditPreferences.class));
			return true;

		case R.id.menuabout:
			DialogAbout ap = new DialogAbout(this);
			ap.show();
			return true;

		case R.id.menuinfo:
			this.launchWeb(this.getString(R.string.app_url));
			return true;

		case R.id.clearlogs:
			clearLogsConfirm();
			return true;
		}

		return false;

	}

	public void launchWeb(String url) {

		if (haveNet()) {
			Intent intent = new Intent(this, ActWebView.class);
			intent.putExtra("url", url);
			startActivity(intent);
		} else {
			DialogInfo di = new DialogInfo(
					this,
					this.getString(R.string.error_title_networkerror),
					this.getString(R.string.error_message_internetrequiredtodisplayinfopage)
							+ "\n");
			di.show();
		}

	}

	public boolean haveNet() {

		ConnectivityManager connec = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec.getActiveNetworkInfo() == null)
			return false;
		if (connec.getActiveNetworkInfo().isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}

	protected void clearLogsConfirm() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.alert_title_confirmclearlogs)
				.setMessage(R.string.alert_message_confirmclearlogs)
				.setPositiveButton(R.string.button_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// Stop the activity
								TemplateChannel.this.clearLogs();
							}

						}).setNegativeButton(R.string.button_cancel, null)
				.show();

	}

	protected void clearLogs() {
		try {
			MessageQueue.INSTANCE.flush();
		} catch (Exception f) {
		}
		LogManager.resetLogs();

	}

}
