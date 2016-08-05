package com.cratorsoft.android.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
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
import com.cratorsoft.android.db.DBUpdate;
import com.cratorsoft.android.dbtable.AccountData;
import com.cratorsoft.android.dbtable.ChannelData;
import com.cratorsoft.android.frag.FragAccountList_;
import com.cratorsoft.android.ttslanguage.TTSLanguage;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;

public class DlgPromptVoiceLanguage extends DialogFragment {
	

    public static DlgPromptVoiceLanguage newInstance(String type,  long network, String channel) {

        DlgPromptVoiceLanguage frag = new DlgPromptVoiceLanguage();

        Fargs fargs = new Fargs();
        fargs.bundle.putString("type", type);
        fargs.bundle.putLong("network", network);
        fargs.bundle.putString("channel", channel);

        frag.setArguments(fargs.bundle);
        return frag;
    }



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

        Fargs fargs = Fargs.create(this);

        final int action = fargs.getAction();

        MaterialDialog dlg = new MaterialDialog.Builder(this.getActivity())
                .title(R.string.alert_title_selectlanguage)
                .customView(R.layout.promptvoicelanguage,true)
                .positiveText(this.getActivity().getString(R.string.button_ok))
                .negativeText(this.getActivity().getString(R.string.button_cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setlanguage(dialog);
                    }
                })
                .build();


        //language spinner
        ArrayAdapter<TTSLanguage> languageAdapter = TTSSpinnerManager.INSTANCE.getTTSAdapter(this.getActivity());
        String chan_language = fargs.bundle.getString("language");
        Spinner splanguage = (Spinner) dlg.getCustomView().findViewById(R.id.language);
        splanguage.setAdapter(languageAdapter);
        splanguage.setSelection((int) TTSSpinnerManager.INSTANCE.searchPosition(languageAdapter,chan_language));

        return dlg;


			 
	}

	
	
	public void setlanguage(MaterialDialog dlg){

        Fargs fargs = Fargs.create(this);

        long network = fargs.bundle.getLong("network");
        String type = fargs.bundle.getString("type");
        String channel = fargs.bundle.getString("channel");

        Spinner splanguage = (Spinner) dlg.getCustomView().findViewById(R.id.language);
        String language;
        try {
            language = ((TTSLanguage) splanguage.getSelectedItem()).nameWithEngine;
        }catch(Exception e){
            return;
        }

        if (type.equals("channel")){
            Globo.setTTSChannel(network, channel.toLowerCase(),  language );
        }else{
            Globo.setTTSServer(network, language);
        }

        Globo.botManager.refreshUI(this.getActivity());

	}
	
	
	
}



