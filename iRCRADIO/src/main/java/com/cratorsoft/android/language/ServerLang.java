package com.cratorsoft.android.language;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by j on 10/02/15.
 */
public class ServerLang {

    Map<String, ChannelLang> channelLangMap = new HashMap<String,ChannelLang>();

    ParsedLanguageAndEngine plae;
    long server;

    public ServerLang(long server, String languageAndEngine){
        this.server = server;
        plae = new ParsedLanguageAndEngine(languageAndEngine);
    }


    public void setLanguageAndEngine(String languageAndEngine){

        plae = new ParsedLanguageAndEngine(languageAndEngine);

    }




}
