package com.cratorsoft.android.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.cratorsoft.android.ttslanguage.TTSEngine;
import com.cratorsoft.android.ttslanguage.TTSLanguageManager;

import java.util.List;

/**
 * Created by j on 27/01/15.
 */
public enum TTSEngineAdapter {


    INSTANCE;


    public Adapter getAdapter(Context ctx){
        return new Adapter(ctx, TTSLanguageManager.INSTANCE.getListTTSEngine());
    }


    public static class Adapter extends ArrayAdapter<TTSEngine>{

        public Adapter(Context context, List<TTSEngine> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
        }


    }

}
