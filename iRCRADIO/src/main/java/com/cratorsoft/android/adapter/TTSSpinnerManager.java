package com.cratorsoft.android.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import com.cratorsoft.android.ttslanguage.TTSLanguage;
import com.cratorsoft.android.ttslanguage.TTSLanguageManager;
import com.earthflare.android.ircradio.R;


import java.util.List;

/**
 * Created by j on 09/02/15.
 */
public enum TTSSpinnerManager {

    INSTANCE;


    public ArrayAdapter<TTSLanguage> getTTSAdapter(Context ctx){


        List<TTSLanguage> activeLanguageList = TTSLanguageManager.INSTANCE.getActiveLanguageList(true);

        ArrayAdapter<TTSLanguage> adapter = new ArrayAdapter<TTSLanguage>(ctx, android.R.layout.simple_spinner_item, activeLanguageList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return  adapter;


    }


    public int searchPosition(ArrayAdapter<TTSLanguage> adapter, String name){

        int position = -1;

        for(int i=0 ; i<adapter.getCount() ; i++){
            TTSLanguage ttsLanguage = adapter.getItem(i);


            if (!ttsLanguage.blankPlaceholer && ttsLanguage.nameWithEngine.equals(name) ){
                position = i;
                break;
            }

        }

        return position;

    }


}
