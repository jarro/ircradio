package com.cratorsoft.android.ttslanguage;

import java.util.Locale;

/**
 * Created by j on 04/02/15.
 */
public class TTSFallback {

    public String enginename;
    public Locale locale;


    public TTSFallback(Locale locale, String enginename){
        this.locale = locale;
        this.enginename = enginename;
    }


}
