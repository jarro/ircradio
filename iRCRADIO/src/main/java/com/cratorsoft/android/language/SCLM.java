package com.cratorsoft.android.language;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by j on 12/02/15.
 * Server Channel Language Manager
 */
public enum SCLM {

    INSTANCE;

    private SCLM(){
        defaultPLae = new ParsedLanguageAndEngine("en");
        serverLangMap = new HashMap<Long, ServerLang>();
    }

    Map<Long, ServerLang> serverLangMap;
    ParsedLanguageAndEngine defaultPLae;


    public Locale getServerLocale(long server){
        if (!serverLangMap.containsKey(server)){
            return defaultPLae.locale;
        }else{
            return serverLangMap.get(server).plae.locale;
        }
    }


    public Locale getChannelLocale(long server, String channel){
        if(!serverLangMap.containsKey(server)){
            return defaultPLae.locale;
        }else{
            if (!serverLangMap.get(server).channelLangMap.containsKey(channel)){
                return serverLangMap.get(server).plae.locale;
            }else{
                return serverLangMap.get(server).channelLangMap.get(channel).plae.locale;
            }
        }
    }


    public String getServerLanguageAndEngine(long server){

        if (!serverLangMap.containsKey(server)){
            return defaultPLae.languageAndEngine;
        }else{
            return serverLangMap.get(server).plae.languageAndEngine;
        }

    }

    public String getChannelLanguageAndEngine(long server, String channel){

        if(!serverLangMap.containsKey(server)){
            return defaultPLae.languageAndEngine;
        }else{
            if (!serverLangMap.get(server).channelLangMap.containsKey(channel)){
                return serverLangMap.get(server).plae.languageAndEngine;
            }else{
                return serverLangMap.get(server).channelLangMap.get(channel).plae.languageAndEngine;
            }
        }

    }

    public ParsedLanguageAndEngine getServerPLAE(long server){

        if (!serverLangMap.containsKey(server)){
            return defaultPLae;
        }else{
            return serverLangMap.get(server).plae;
        }

    }


    public ParsedLanguageAndEngine getChannelPLAE(long server, String channel){

        if(!serverLangMap.containsKey(server)){
            return defaultPLae;
        }else{
            if (!serverLangMap.get(server).channelLangMap.containsKey(channel)){
                return serverLangMap.get(server).plae;
            }else{
                return serverLangMap.get(server).channelLangMap.get(channel).plae;
            }
        }

    }


    public void setChannelLanguage(long server, String channel, String languageAndEngine){
        if (!serverLangMap.containsKey(server)){
            serverLangMap.put(server, new ServerLang(server, languageAndEngine));
        }
        ServerLang sl = serverLangMap.get(server);
        sl.channelLangMap.put(channel,new ChannelLang(channel,languageAndEngine));
    }

    public void setServerLanguage(long server, String languageAndEngine){
        if (!serverLangMap.containsKey(server)){
            serverLangMap.put(server, new ServerLang(server, languageAndEngine));
        }else{
            serverLangMap.get(server).setLanguageAndEngine(languageAndEngine);
        }
    }


}
