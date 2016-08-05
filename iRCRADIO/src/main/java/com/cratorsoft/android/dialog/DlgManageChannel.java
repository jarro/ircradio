package com.cratorsoft.android.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cratorsoft.android.aamain.Action;
import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.adapter.TTSSpinnerManager;
import com.cratorsoft.android.db.DBInsert;
import com.cratorsoft.android.db.DBQAccount;
import com.cratorsoft.android.db.DBQChannel;
import com.cratorsoft.android.db.DBUpdate;
import com.cratorsoft.android.dbtable.AccountData;
import com.cratorsoft.android.dbtable.ChannelData;
import com.cratorsoft.android.frag.FragAccountList_;
import com.cratorsoft.android.ttslanguage.TTSLanguage;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;

import java.sql.SQLException;

public class DlgManageChannel extends DialogFragment {
	
    public static String COMMANDLISTCHANNELS = "commandlistchannels";
    public static String FRAGACCOUNTLIST = "fragaccountlist";

    public static DlgManageChannel newManageFromCommandListChannels(long accountid, String channelname){

        ChannelData channelData = null;
        AccountData accountData = null;
        try {
            accountData = DBQAccount.account(accountid);
            channelData = DBQChannel.getChannelExists(accountid,channelname);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (accountData != null && channelData == null){
            return newInsert(accountData, channelname, DlgManageChannel.COMMANDLISTCHANNELS);
        }else if (accountData != null && channelData != null){
            return newEdit(accountData, channelData, DlgManageChannel.COMMANDLISTCHANNELS);
        }else {
            return null;
        }

    }


	public static DlgManageChannel newInsert(AccountData accountData, String channelname, String targetFrag) {
		 
		DlgManageChannel frag = new DlgManageChannel();

        Fargs fargs = new Fargs()
                .setAction(Action.INSERT)
                .setAccount(accountData.id)
                .setTitle(accountData.accountname)
                .setChannelName(channelname)
                .setTargetFrag(targetFrag);

        frag.setArguments(fargs.bundle);
        return frag;
    }

    public static DlgManageChannel newEdit(AccountData accountData, ChannelData channelData, String targetFrag) {

        DlgManageChannel frag = new DlgManageChannel();

        Fargs fargs = new Fargs()
                .setAction(Action.EDIT)
                .setAccount(accountData.id)
                .setChannel(channelData.id)
                .setChannelName(channelData.channel)
                .setTitle(accountData.accountname)
                .setTargetFrag(targetFrag);


        fargs.bundle.putString("channelpass", channelData.channelpass);
        fargs.bundle.putString("joinleave", channelData.joinleave);
        fargs.bundle.putString("language", channelData.language);
        fargs.bundle.putString("autojoin", channelData.autojoin);


        frag.setArguments(fargs.bundle);
        return frag;
    }

	

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);

	}



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {


        Fargs fargs = Fargs.create(this);

		String title = fargs.getTitle();

        final int action = fargs.getAction();

        MaterialDialog dlg = new MaterialDialog.Builder(this.getActivity())
                .title(title)
                .customView(R.layout.dlg_edit_channel,false)
                .positiveText(this.getActivity().getString(R.string.button_save))
                .negativeText(this.getActivity().getString(R.string.button_cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        save(dialog);
                    }
                })
                .build();
        dlg.setCanceledOnTouchOutside(false);


        ((EditText)dlg.getCustomView().findViewById(R.id.channel)).setText(fargs.getChannelName());


        //language spinner
        ArrayAdapter<TTSLanguage> languageAdapter = TTSSpinnerManager.INSTANCE.getTTSAdapter(this.getActivity());
        String chan_language = fargs.bundle.getString("language");
        Spinner splanguage = (Spinner) dlg.getCustomView().findViewById(R.id.language);
        splanguage.setAdapter(languageAdapter);
        splanguage.setSelection((int) TTSSpinnerManager.INSTANCE.searchPosition(languageAdapter,chan_language));


        if (fargs.getAction() == Action.INSERT){



        }else{

            ((EditText)dlg.getCustomView().findViewById(R.id.channelpass)).setText(fargs.bundle.getString("channelpass"));
            ((CheckBox)dlg.getCustomView().findViewById(R.id.joinleave)).setChecked(Boolean.valueOf(fargs.bundle.getString("joinleave")) );
            ((CheckBox)dlg.getCustomView().findViewById(R.id.autojoin)).setChecked(Boolean.valueOf(fargs.bundle.getString("autojoin")) );

        }


        return dlg;


			 
	}

	
	
	public void save(MaterialDialog materialDialog) {

        Fargs fargs = Fargs.create(this);

        final int action = fargs.getAction();
        final ChannelData cd = new ChannelData();


        EditText etChannel = (EditText) materialDialog.getCustomView().findViewById(R.id.channel);
        EditText etChannelPass = (EditText) materialDialog.getCustomView().findViewById(R.id.channelpass);
        CheckBox cbJoinLeave = (CheckBox) materialDialog.getCustomView().findViewById(R.id.joinleave);
        Spinner splanguage = (Spinner) materialDialog.getCustomView().findViewById(R.id.language);
        CheckBox cbAutoJoin = ((CheckBox) materialDialog.getCustomView().findViewById(R.id.autojoin));

        cd.accountid = fargs.getAccount();
        cd.channel = etChannel.getText().toString();
        cd.channelpass = etChannelPass.getText().toString();
        cd.joinleave = String.valueOf(cbJoinLeave.isChecked());
        try {
            cd.language = ((TTSLanguage) splanguage.getSelectedItem()).getLanguageForDB();
        }catch(Exception e){
            cd.language = "en";
        }
        cd.autojoin = String.valueOf(cbAutoJoin.isChecked());

        if (action == Action.EDIT) {
            cd.id = fargs.getChannel();
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {

                if (action == Action.INSERT) {
                    DBInsert.channel(cd);
                } else {
                    DBUpdate.saveChannel(cd);
                }

            }
        };

        Globo.dbHandler.post(r);

        if (fargs.getTargetFrag() == DlgManageChannel.FRAGACCOUNTLIST){
            ((FragAccountList_) this.getTargetFragment()).loadRefreshDataTask();
        }

	}
	
	
	
}



