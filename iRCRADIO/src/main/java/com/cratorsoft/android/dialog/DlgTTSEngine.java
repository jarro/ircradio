package com.cratorsoft.android.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.ttslanguage.TTSEngine;
import com.cratorsoft.android.ttslanguage.TTSLanguage;
import com.cratorsoft.android.ttslanguage.TTSLanguageManager;
import com.cratorsoft.android.ttslanguage.TTSPersister;
import com.earthflare.android.ircradio.R;

import java.util.List;

public class DlgTTSEngine extends DialogFragment{


    public TTSEngine engine;

    public static DlgTTSEngine newInstance(TTSEngine engine) {

        DlgTTSEngine frag = new DlgTTSEngine();
        frag.setRetainInstance(true);
        frag.engine = engine;
        return frag;

    }



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String title = "TTS";
        List<CharSequence> languagelabels = engine.getLanguageLabels();




		MaterialDialog mdlg = new MaterialDialog.Builder(this.getActivity())
			.title(title)
            .items((CharSequence[]) languagelabels.toArray(new CharSequence[languagelabels.size()]))
            .itemsCallbackMultiChoice(engine.getActiveIntArray(), new MaterialDialog.ListCallbackMultiChoice() {
                @Override
                public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                    activateLanguages(integers);
                    return true;
                }
            })
            .negativeText(this.getActivity().getString(R.string.button_cancel))
			.positiveText(this.getActivity().getString(R.string.button_ok))
			.build();
			
		return mdlg;
			
		
		
	}



    private void activateLanguages(Integer[] activearray){

        //deactivate all
        for(TTSLanguage language : engine.getListTTSLanguage()){
            language.active = false;
        }


        List<TTSLanguage> langlist = engine.getListTTSLanguage();

        for (Integer position : activearray){

            langlist.get(position).active = true;

        }

        TTSPersister.INSTANCE.saveActiveList(engine);
        TTSLanguageManager.INSTANCE.updateActiveLanguages();

    }
	
}
