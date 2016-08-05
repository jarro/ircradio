package com.cratorsoft.android.drawer;


import android.text.SpannableString;

public interface DrawerDisplayer {
	public void drawerOpen();
	public void drawerClose();
	public void drawerRefresh();
	public boolean drawerIsClosed();
	public boolean drawerIsOpen();
	public void setFragTitle(String title, String subtitle);
    public void setFragTitle(SpannableString title, String subtitle);
	public void setDrawerTitle(String title, String subtitle);
}
