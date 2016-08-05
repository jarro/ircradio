package com.cratorsoft.android.language;

import com.cratorsoft.android.ttslanguage.TTSLanguageManager;
import com.cratorsoft.android.util.I18nUtils;

import java.util.Locale;

/**
 * Created by j on 12/02/15.
 */
public class ParsedLanguageAndEngine {

    public String enginename = "";
    public String languageAndEngine;
    public Locale locale;
    public boolean hascountry = false;
    public boolean hasvariant = false;


    public ParsedLanguageAndEngine(String language){

        boolean validLanguage = (language != null && language.length() > 0);
        if (!validLanguage){
            languageAndEngine = "en";
            locale = new Locale("en");
            return;
        }

        String strLang = null;
        String strEng = null;

        String[] langparts = language.split(":");
        if (langparts.length == 2){

            enginename = langparts[1];

        }

        languageAndEngine = language;
        strLang = langparts[0];
        locale = I18nUtils.getLocaleFromString(strLang);

        hascountry =  (null != locale.getCountry() && locale.getCountry().length() > 0);
        hasvariant =  (null != locale.getVariant() && locale.getVariant().length() > 0);


    }


}
