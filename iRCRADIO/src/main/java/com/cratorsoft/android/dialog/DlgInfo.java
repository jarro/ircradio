package com.cratorsoft.android.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cratorsoft.android.aamain.Fargs;
import com.earthflare.android.ircradio.R;

public class DlgInfo extends DialogFragment{





    public static DlgInfo newInstance(Bundle data) {

        DlgInfo frag = new DlgInfo();
        frag.setArguments(data);
        return frag;
    }

    public static DlgInfo newInstance(String title, String message) {

        DlgInfo frag = new DlgInfo();

        Fargs fargs = Fargs.create();
        fargs.setTitle(title);
        fargs.setMessage(message);

        frag.setArguments(fargs.bundle);

        return frag;
    }



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {


        Fargs fargs = Fargs.create(this);

		String title = fargs.getTitle();
        String message = fargs.getMessage();
		
		MaterialDialog mdlg = new MaterialDialog.Builder(this.getActivity())
			.title(title)
			.content(message)
			.positiveText(this.getActivity().getString(R.string.button_ok))
			.build();
			
		return mdlg;
			
		
		
	}
	
	
}
