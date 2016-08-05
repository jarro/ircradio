package com.cratorsoft.android.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.cratorsoft.android.listener.ConfirmListener;
import com.earthflare.android.ircradio.R;


public class FragConfirm extends DialogFragment {
	
	Bundle data;
	
	public static FragConfirm newInstance(Bundle data) {
		 
		 FragConfirm frag = new FragConfirm();
		 frag.data = data;
         //Bundle args = new Bundle();
         //args.putLong("date", date);
         //frag.setArguments(args);
         return frag;
    }
	
	
	
	@Override
	public void onSaveInstanceState(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(arg0);
		
		arg0.putBundle("data", data);
	}



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
				
		if (savedInstanceState != null){
			this.data = savedInstanceState.getBundle("data");
		}
		
		 String title = "Confirm";
		 String message = data.getString("message");
		    		  			 
		 AlertDialog dialog = new AlertDialog(this.getActivity() ) {
			     protected void onCreate(Bundle savedInstanceState){
			     super.onCreate(savedInstanceState);
	     }};
			 
		 dialog.setTitle(title);
		 
		 dialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.button_cancel),new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
			        dialog.cancel();
			    } });  
		 
		 dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			        confirm();
			    } }); 
		dialog.setMessage(message);
			 
		return dialog;
			 
		
		
	}

	
	public void confirm(){
		
		((ConfirmListener)this.getTargetFragment()).onConfirm(this.getTag());
		
	}
	
	
	
}



