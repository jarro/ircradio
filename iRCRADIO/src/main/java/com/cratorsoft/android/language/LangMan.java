package com.cratorsoft.android.language;

import com.cratorsoft.android.ttslanguage.TTSLanguage;
import com.earthflare.android.ircradio.Globo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by j on 10/02/15.
 */
public enum LangMan {

    INSTANCE;


    Map<String, String> localeMap;
    Map<String, LangTable> langTableMap;
    Locale defaultLocale;

    private LangMan(){

        defaultLocale = new Locale("en");
        localeMap = new HashMap<String, String>();
        langTableMap = new HashMap<String, LangTable>();
    }


    public synchronized LangTable getLangTable(Locale locale) {


        //check for mapping
        if (!localeMap.containsKey(locale.toString())) {
            loadLangTable(locale);
        }

        String key = localeMap.get(locale.toString());
        if (key == null) {
            return null;
        }
        return langTableMap.get(key);


    }


    private void loadLangTable(Locale locale) {

        //attempt to load resource

        boolean hascountry =  (null != locale.getCountry() && locale.getCountry().length() > 0);
        boolean hasvariant =  (null != locale.getVariant() && locale.getVariant().length() > 0);

        boolean languageloaded = false;
        String codeLanguage = "";

        boolean countryloaded = false;
        String codeCountry = "";

        boolean variantloaded = false;
        String codeVariant = "";


        //load variant
        if (hasvariant){
            codeVariant = locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant();
            if(localeMap.containsKey(codeVariant)){
                variantloaded = true;
            }else{
                variantloaded = loadAsset(codeVariant);
            }
        }

        if(!variantloaded){

            if(hascountry) {
                codeCountry = locale.getLanguage() + "_" + locale.getCountry() ;
                if(localeMap.containsKey(codeCountry)){
                    countryloaded = true;
                }else{
                    countryloaded = loadAsset(codeCountry);
                }
            }
        }


        if(!countryloaded){
            codeLanguage = locale.getLanguage();
            if(localeMap.containsKey(codeLanguage)){
                languageloaded = true;
            }else{
                languageloaded = loadAsset(codeLanguage);
            }
        }


        //propogate variant -> language
        if (hasvariant && variantloaded){

            if(!countryloaded){
                localeMap.put(codeCountry, localeMap.get(codeVariant));
                countryloaded = true;
            }

            if(!languageloaded){
                localeMap.put(codeLanguage, localeMap.get(codeCountry));
                languageloaded = true;
            }

        }

        if (hascountry && countryloaded){

            if(!languageloaded){
                localeMap.put(codeLanguage, localeMap.get(codeCountry));
                languageloaded = true;
            }

        }


        //propogate language -> variant
        if (languageloaded){

            if(hascountry && !countryloaded){
                localeMap.put(codeCountry, localeMap.get(codeLanguage));
                countryloaded = true;
            }

            if(hasvariant && !variantloaded){
                localeMap.put(codeVariant, localeMap.get(codeCountry));
                variantloaded = true;
            }

        }

        if(!languageloaded){
            localeMap.put(codeLanguage,null);
        }
        if (!countryloaded && hascountry){
            localeMap.put(codeCountry,null);
        }
        if (!variantloaded && hasvariant){
            localeMap.put(codeVariant,null);
        }

    }


    private InputStream getAssetInputStream(String assetfile) throws IOException {

        //load file from external storage
        //use internal asset on fail


        File rootpath = Globo.ctx.getExternalFilesDir(null);
        File externalLangFile = new File(rootpath.getAbsolutePath().toString() + "/lookup/" + assetfile + ".jsn");

        if (externalLangFile.exists()){
            return  new FileInputStream(externalLangFile);
        }else{
            return Globo.ctx.getAssets().open("lookup/" + assetfile + ".jsn");
        }


    }


    private boolean loadAsset(String assetfile){


        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        boolean success = false;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssetInputStream(assetfile), "UTF-8"));
            // do reading, usually loop until end of file reading
            String mLine;
            while ( (mLine = reader.readLine()) != null) {
                builder.append(mLine);
            }
            success = true;
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }


        if(success){
            try {
                Gson gson = new Gson();
                LangTable lt = gson.fromJson(builder.toString(), LangTable.class);
                langTableMap.put(assetfile, lt);
                localeMap.put(assetfile, assetfile);
                return true;
            }catch (Exception e){
                return false;
            }
        }else{
            return false;
        }
    }



    public String logLookup(String key, Locale locale){
        //fallback to english;

        LangTable lt = getLangTable(locale);
        if (lt == null){
           lt = getLangTable(defaultLocale);
        }

        if (lt == null){
            return "";
        }else{

            if (lt.ircMap.containsKey(key)){
                return lt.ircMap.get(key);
            }else{
                return "";
            }
        }

    }


    public String speechLookup(String key, Locale locale){
        //fallback to blank
        LangTable lt = getLangTable(locale);
        if (lt == null){
            return "";
        }else{

            if (lt.ircMap.containsKey(key)){
                return lt.ircMap.get(key);
            }else{
                return "";
            }
        }
    }


    public String speechReplace(String message, Locale locale){
        //fallback to blank

        LangTable lt = getLangTable(locale);
        if (lt == null){
            return message;
        }else {

            //apply block by startswith
            for (String startsWith :  lt.blockMessageByStartsWith){
                if (message.startsWith(startsWith)){
                    return "";
                }
            }

            //apply block by regex
            try {
                for (String regex : lt.blockMessageByRegex) {
                    if (message.matches(regex)) {
                        return "";
                    }
                }
            }catch(Exception e){}

            //apply replace all
            try {
                for (LangTable.RegexCommand regexCommand : lt.replaceAllByRegex) {
                   message = message.replaceAll(regexCommand.regex, regexCommand.text);
                }
            }catch(Exception e){}


            StringTokenizer Tok = new StringTokenizer(message);

            String out = "";
            String token;

            boolean replaced;
            while (Tok.hasMoreElements()) {
                replaced = false;
                token = (String) Tok.nextElement();

                if (lt.replaceMap.containsKey(token)) {
                    out += " " + lt.replaceMap.get(token);
                    replaced = true;
                }

                if (!replaced) {
                    for (String startswithkey : lt.startswithMap.keySet()) {

                        if (token.startsWith(startswithkey)) {
                            out += " " + lt.startswithMap.get(startswithkey);
                            replaced = true;
                            break;
                        }
                    }
                }

                if (!replaced) {
                    out += " " + token;
                }
            }
            return out;
        }

    }


}
