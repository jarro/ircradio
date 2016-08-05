package com.cratorsoft.android.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cratorsoft.android.aamain.Action;
import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.db.DBInsert;
import com.cratorsoft.android.db.DBQAccount;
import com.cratorsoft.android.db.DBQChannel;
import com.cratorsoft.android.db.DBUpdate;
import com.cratorsoft.android.dbtable.AccountData;
import com.cratorsoft.android.dbtable.ChannelData;
import com.cratorsoft.android.frag.FragAccountList_;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;

import java.sql.SQLException;

public class DlgPromptFilter extends DialogFragment {
	

	public static DlgPromptFilter newInstance() {
		 
		DlgPromptFilter frag = new DlgPromptFilter();
        return frag;
    }



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {


        MaterialDialog dlg = new MaterialDialog.Builder(this.getActivity())
                .title(R.string.ui_filterlist)
                .customView(R.layout.dlg_prompt_filter,false)
                .positiveText(this.getActivity().getString(R.string.button_ok))
                .negativeText(this.getActivity().getString(R.string.button_cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        filter(dialog);
                    }
                })
                .build();



        return dlg;


			 
	}

	
	
	public void filter(MaterialDialog mdlg) {

        int min;
        int max;
        String filter;

        try {
            min = Integer.parseInt((String)( (TextView)mdlg.getCustomView().findViewById(R.id.minusers) ).getText().toString());
        } catch (NumberFormatException e) {
            min = Integer.MIN_VALUE;
        }

        try {
            max = Integer.parseInt((String)( (TextView)mdlg.getCustomView().findViewById(R.id.maxusers) ).getText().toString());
        } catch (NumberFormatException e) {
            max = Integer.MAX_VALUE;
        }

        filter = (String)((TextView)mdlg.getCustomView().findViewById(R.id.filtertext)).getText().toString();

        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("action", "filter");
        data.putString("filtertext", filter);
        data.putInt("min", min);
        data.putInt("max", max);
        msg.setData(data);

        if(Globo.actListChannelsVisible){
            Globo.handlerListChannels.sendMessage(msg);
        }


	}
	
	
	
}



