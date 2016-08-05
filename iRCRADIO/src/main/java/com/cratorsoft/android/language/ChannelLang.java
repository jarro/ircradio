package com.cratorsoft.android.language;

import java.util.Locale;

/**
 * Created by j on 11/02/15.
 */
public class ChannelLang {

    public String channel;
    ParsedLanguageAndEngine plae;

    public ChannelLang(String channel, String languageAndEngine){

        plae = new ParsedLanguageAndEngine(languageAndEngine);
        this.channel = channel;

    }

}
