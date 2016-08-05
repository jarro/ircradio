package com.cratorsoft.android.ttslanguage;

import com.cratorsoft.android.util.I18nUtils;

import java.util.Locale;

/**
 * Created by j on 28/01/15.
 */
public class TTSStatus {

    public static int AVAILABLE = 0;
    public static int UNAVAILABLE = -1;

    public int status = TTSStatus.UNAVAILABLE;
    public String enginename;
    public Locale locale;





    public static TTSStatus getInstance(String language){



        String strLang = null;
        String strEng = null;

        boolean found = false;
        TTSStatus status = new TTSStatus();

        if (language != null && language.length() > 0){

                String[] langparts = language.split(":");
                if (langparts.length == 2){
                    //language and engine

                    strLang = langparts[0];
                    strEng = langparts[1];

                    Locale locale = I18nUtils.getLocaleFromString(strLang);
                    found = TTSLanguageManager.INSTANCE.validateEngineLanguage(locale, strEng);

                    if (found) {

                        status.status = TTSStatus.AVAILABLE;
                        status.enginename = strEng;
                        status.locale = locale;
                    }

                }


                if (!found){
                    //attempt fallback

                    strLang = langparts[0];
                    Locale locale = I18nUtils.getLocaleFromString(strLang);

                    if (locale != null){

                        Locale langlocale = new Locale (locale.getLanguage());
                        String enginename = TTSLanguageManager.INSTANCE.getFallback(langlocale.getLanguage());

                        if (enginename != null){
                            //found fallback
                            status.status = TTSStatus.AVAILABLE;
                            status.enginename = enginename;
                            status.locale = langlocale;

                        }

                    }


                }



        }else{
            //null language



        }

        return status;
    }


}
