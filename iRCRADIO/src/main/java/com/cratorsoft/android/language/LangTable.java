package com.cratorsoft.android.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by j on 10/02/15.
 */
public class LangTable {

    LangTable(){};

    String code;

    public List<RegexCommand> replaceAllByRegex = new ArrayList<>();
    public List<String> blockMessageByRegex = new ArrayList<>();
    public List<String> blockMessageByStartsWith = new ArrayList<>();
    public List<String> blockMessageByUser = new ArrayList<>();
    public Map<String, String> ircMap = new HashMap<>();
    public Map<String, String> replaceMap = new HashMap<>();;
    public Map<String, String> startswithMap = new HashMap<>();;



    public static class RegexCommand{

        String regex;
        String text;

    }







}
