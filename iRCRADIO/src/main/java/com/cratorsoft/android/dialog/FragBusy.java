package com.cratorsoft.android.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.cratorsoft.android.listener.OnAbortListener;

public class FragBusy extends DialogFragment {

	private OnAbortListener listener = null;
	
	public static FragBusy newInstance(String message, boolean cancelable) {
		FragBusy frag = new FragBusy();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        
        frag.setCancelable(cancelable);
        
        return frag;
    }

	
	public static FragBusy newInstance(String message) {		
		return FragBusy.newInstance(message, false);		
	}
	
	public static FragBusy newInstance() {		
		return FragBusy.newInstance(null, false);		
	}
	
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		String message = getArguments().getString("message");

		ProgressDialog pd = new ProgressDialog(getActivity());		
		pd.setIndeterminate(true);
		pd.setOnCancelListener( new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//getActivity().finish();				
			} } );		
		
		pd.setMessage(message);
		
		//this.setStyle(STYLE_NORMAL, ThemeUtil.getAlertTheme(this.getActivity()));
		
		return pd;
		
		
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		super.onCancel(dialog);
		
		if (this.listener != null){
			this.listener.onAbort();
		}
		
	}

	
	public void setOnAbortListener(OnAbortListener listener){
		
		this.listener = listener;
		
	}
	
}
