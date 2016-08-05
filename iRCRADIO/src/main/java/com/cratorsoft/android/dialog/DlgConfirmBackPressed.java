package com.cratorsoft.android.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cratorsoft.android.confirmback.ActConfirmBackPressed;
import com.earthflare.android.ircradio.R;

public class DlgConfirmBackPressed extends DialogFragment {
	
	Bundle data;
	
	public static DlgConfirmBackPressed newInstance(Bundle data) {
		 
		 DlgConfirmBackPressed frag = new DlgConfirmBackPressed();
		 frag.data = data;
         return frag;
    }
	
	
	
	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		arg0.putBundle("data", data);
	}



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
				
		if (savedInstanceState != null){
			this.data = savedInstanceState.getBundle("data");
		}


		String title = data.getString("title");
		String message = data.getString("message");

        MaterialDialog dlg = new MaterialDialog.Builder(this.getActivity())
                .title(title)
                .content(message)
                .positiveText(this.getActivity().getString(R.string.button_ok))
                .negativeText(this.getActivity().getString(R.string.button_cancel))
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						confirm();
					}
				})
                .build();

        return dlg;


			 
	}

	
	
	public void confirm(){
	    this.getDialog().cancel();
	    ((ActConfirmBackPressed)this.getActivity()).goBack();	
	}
	
	
	
}



