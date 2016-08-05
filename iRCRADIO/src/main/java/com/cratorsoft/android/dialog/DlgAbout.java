package com.cratorsoft.android.dialog;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.earthflare.android.ircradio.R;

public class DlgAbout extends DialogFragment{

	

	public static DlgAbout newInstance() {
		DlgAbout frag = new DlgAbout();     
        return frag;
    }
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		PackageManager pm = this.getActivity().getPackageManager();	            
	   	PackageInfo pi;
	   	String vname = "";
		try {
			pi = pm.getPackageInfo(this.getActivity().getPackageName(), 0);
			vname = pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		MaterialDialog mdlg = new MaterialDialog.Builder(this.getActivity())
			.title(this.getActivity().getString(R.string.app_name))
			.content(("Version: " + vname + " \n\n" + this.getActivity().getString(R.string.app_clause) ))
			.positiveText(this.getActivity().getString(R.string.button_ok))
			.build();
			
		return mdlg;
			
		
		
	}
	
	
}
