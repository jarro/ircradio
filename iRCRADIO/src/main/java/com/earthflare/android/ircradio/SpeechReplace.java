package com.earthflare.android.ircradio;

import java.util.HashMap;
import java.util.StringTokenizer;

public class SpeechReplace {

  HashMap<String, String> hm;
  HashMap<String, String> nem;
  
  public String replace(String message, String language) {

      /*
	  String lang = language.substring(0, 1);
 
      StringTokenizer Tok = new StringTokenizer(message);
      
      
      String out = "";
      String token;
      
      if (lang.equals("en")) {
            
        while (Tok.hasMoreElements()) {
               token = (String)Tok.nextElement();
            if (hm.containsKey(token)) {
              out += " " + hm.get(token);
            }else{
              out += " " + token;
            }
        }
      
      }else{
    	 
    	  while (Tok.hasMoreElements()) {
              token = (String)Tok.nextElement();
           if (nem.containsKey(token)) {
             out += " " + Globo.voiceResource.getLRString(nem.get(token),language);
           }else{
             out += " " + token;
           }
       }
    	  
    	  
      }

      return out;
      */
      return null;
  }  
  
  
  public SpeechReplace() {
  
	  nem = new HashMap<String,String>();
	  nem.put(":)" , "r_sr_smiles");
	  nem.put(":-)" , "r_sr_smiles");
	  nem.put(":-p" , "r_sr_sticksouttongue");
	  nem.put(":>" , "r_sr_smiles");
	  nem.put(":p" , "r_sr_sticksouttongue");
	  nem.put(":P" , "r_sr_sticksouttongue");
	  nem.put(";)" , "r_sr_winks");
	  nem.put(";-)" , "r_sr_winks");
	  nem.put(":(" , "r_sr_frowns");
    
	  
	  hm = new HashMap<String, String>();
       
       
       hm.put(".." , ",");
       hm.put("..." , ",");
       hm.put("2nd" , "second");
       hm.put("31337" , "eleet");
       hm.put("56k" , "56 k");
       hm.put(":)" , "smiles");
       hm.put(":-)" , "smiles");
       hm.put(":-p" , "sticks tongue out");
       hm.put(":>" , "smiles");
       hm.put(":p" , "sticks tongue out");
       hm.put(":P" , "sticks tongue out");
       hm.put(";)" , "winks");
       hm.put(";-)" , "winks");
       hm.put(":(" , "frowns");
       hm.put("aamof" , "as a matter of fact");
       hm.put("ad" , "add");
       hm.put("adn" , "any day now");
       hm.put("afaik" , "as far as i know");
       hm.put("afair" , "as far as i remember");
       hm.put("afj" , "april fool's joke");
       hm.put("afk" , "away from the keyboard");
       hm.put("asap" , "as soon as possible");
       hm.put("at0mic_" , "atomic");
       hm.put("atm" , "at the moment");
       hm.put("awc" , "after while, crocodile");
       hm.put("awgthtgtwta?" , "are we going to have to go through with this again?");
       hm.put("ayt" , "are you there?");
       hm.put("b4" , "before");
       hm.put("b4n" , "bye for now");
       hm.put("bak" , "back at keyboard");
       hm.put("bakerw0rk" , "baker-work");
       hm.put("bbfn" , "bye bye for now");
       hm.put("bbiab" , "be back in a bit");
       hm.put("bbiaf" , "be back in a few");
       hm.put("bbialb" , "be back in a little bit");
       hm.put("bbl" , "be back later");
       hm.put("bcnu" , "be seeing you");
       hm.put("bf" , "boy friend");
       hm.put("brb" , "be right back");
       hm.put("bta" , "but then again");
       hm.put("btw" , "by the way");
       hm.put("bye?" , "are you ready to say goodbye");
       hm.put("bykt" , "but you knew that");
       hm.put("byob" , "bring your own bottle");
       hm.put("byom" , "bring your own mac");
       hm.put("cmiiw" , "correct me if i'm wrong");
       hm.put("cu" , "see you");
       hm.put("cul" , "see you later");
       hm.put("cul8r" , "see you later");
       hm.put("cula" , "see you later, alligator");
       hm.put("cya" , "see ya");
       hm.put("d/l" , "download");
       hm.put("d00d" , "dood");
       hm.put("diku?" , "do i know you?");
       hm.put("dtrt" , "do the right thing");
       hm.put("dvd" , "d v d");
       hm.put("esad" , "eat shit and die");
       hm.put("f2f" , "face to face");
       hm.put("fcfs" , "first come, first served");
       hm.put("fitb" , "fill in the blank");
       hm.put("flyaway" , "fly away");
       hm.put("foad" , "frack off and die");
       hm.put("foaf" , "friend of a friend");
       hm.put("freebsd" , "free bsd");
       hm.put("fs" , "for sale");
       hm.put("fubar" , "fracked up beyond all repair");
       hm.put("fud" , "fear, uncertainty and doubt");
       hm.put("fwiw" , "for what it's worth");
       hm.put("fya" , "for your amusement");
       hm.put("fyi" , "for your information");
       hm.put("ga" , "go ahead");
       hm.put("gal" , "get a life");
       hm.put("gb" , "gigabytes");
       hm.put("gd&r" , "grinning, ducking and running");
       hm.put("gd&wvvf" , "grinning, ducking, and walking very, very fast");
       hm.put("gf" , "girl friend");
       hm.put("giwist" , "gee, i wish i'd said that");
       hm.put("gmta" , "great minds think alike");
       hm.put("hhoj" , "ha ha only joking");
       hm.put("hhok" , "ha ha only kidding");
       hm.put("hhos" , "ha ha only serious");
       hm.put("hmm" , "hurrmmm");
       hm.put("hoyew" , "hanging on your every word");
       hm.put("hp/ux" , "hp ux");
       hm.put("hrm" , "herm");
       hm.put("hth" , "hope that helps!");
       hm.put("i8urdog" , "I ate your dog");
       hm.put("iac" , "in any case");
       hm.put("ianal" , "i am not a lawyer");
       hm.put("iaw" , "in accordance with");
       hm.put("ic" , "i see");
       hm.put("identd" , "ident d");
       hm.put("iirc" , "if i remember correctly");
       hm.put("ijwtk" , "i just want to know");
       hm.put("ijwts" , "i just want to say");
       hm.put("ikwum" , "i know what you mean");
       hm.put("ima" , "i might add");
       hm.put("imao" , "in my arrogant opinion");
       hm.put("imco" , "in my considered opinion");
       hm.put("ime" , "in my experience");
       hm.put("imho" , "in my humble opinion");
       hm.put("imnsho" , "in my not so humble opinion");
       hm.put("imo" , "in my opinion");
       hm.put("inpo" , "in no particular order");
       hm.put("irq" , "i r q");
       hm.put("iow" , "in other words");
       hm.put("ip" , "i p");
       hm.put("ipx" , "i p x");
       hm.put("irl" , "in real life");
       hm.put("isdn" , "i s d n");
       hm.put("iss" , "i'm so sure");
       hm.put("issygti" , "i'm so sure you get the idea!");
       hm.put("iswim" , "if you see what i mean");
       hm.put("iwbni" , "it would be nice if");
       hm.put("iyfeg" , "insert your favorite ethnic group");
       hm.put("iyswim" , "if you see what i mean");
       hm.put("jam" , "just a minute");
       hm.put("jic" , "just in case");
       hm.put("k3nny" , "kenny");
       hm.put("kb" , "kilobytes");
       hm.put("kibo" , "knowledge in, bullshit out");
       hm.put("kwim" , "know what i mean?");
       hm.put("kyfc" , "keep your fingers crossed");
       hm.put("l8r" , "later");
       hm.put("lin0x" , "linux");
       hm.put("ljbf" , "let's just be friends");
       hm.put("lol" , "laughing out loud");
       hm.put("lshmba" , "laughing so hard my belly aches");
       hm.put("lthtt" , "laughing too hard to type");
       hm.put("ltns" , "long time no see");
       hm.put("m8" , "mate");
       hm.put("mb" , "megabytes");
       hm.put("motas" , "member of the appropriate sex");
       hm.put("motd" , "message of the day");
       hm.put("motos" , "member of the opposite sex");
       hm.put("motss" , "member of the same sex");
       hm.put("msg" , "message");
       hm.put("myob" , "mind your own business");
       hm.put("n-tropy" , "entropy");
       hm.put("nah" , "no");
       hm.put("ne1" , "anyone");
       hm.put("netbsd" , "net bsd");
       hm.put("nhoh" , "never heard of him slash her");
       hm.put("np" , "no problem");
       hm.put("obo" , "or best offer");
       hm.put("obtw" , "oh, by the way");
       hm.put("oic" , "oh, i see");
       hm.put("ok" , "o k");
       hm.put("omg", "oh my god");
       hm.put("openbsd" , "open bsd");
       hm.put("otf" , "on the floor");
       hm.put("otfl" , "on the floor laughing");
       hm.put("otoh" , "on the other hand");
       hm.put("ott" , "over the top");
       hm.put("pci" , "p c i");
       hm.put("pmf" , "pardon my french");
       hm.put("pmfbi" , "pardon me for butting in");
       hm.put("pmfji" , "pardon me for jumping in");
       hm.put("pmji" , "pardon my jumping in");
       hm.put("pncah" , "please, no cursing allowed here");
       hm.put("pnpisa" , "p n p i s a");
       hm.put("po0p" , "poop");
       hm.put("ppl" , "people");
       hm.put("pstn" , "p s t n");
       hm.put("ptmm" , "please tell me more");
       hm.put("quazwork" , "quaz-work");       
       hm.put("re" , "regreet");
       hm.put("redhat" , "red hat");
       hm.put("rehi" , "hi again");
       hm.put("rl" , "real life");
       hm.put("rofl" , "rolling on floor laughing");
       hm.put("rotf" , "rolling on the floor");
       hm.put("rotfl" , "rolling on the floor laughing");
       hm.put("rotflol" , "rolling on the floor laughing out loud");
       hm.put("rsn" , "real soon now");
       hm.put("rsvp" , "please reply");
       hm.put("rtbm" , "read the bloody manual");
       hm.put("rtfaq" , "read the frequently asked questions");
       hm.put("rtfm" , "read the fracking manual");
       hm.put("rtm" , "read the manual");
       hm.put("ryfm" , "read your friendly manual");
       hm.put("scsi" , "scuzy");
       hm.put("sec" , "wait a second");
       hm.put("secs" , "seconds");
       hm.put("snafu" , "situation normal, all fouled up");
       hm.put("sol" , "shit out of luck");
       hm.put("sos" , "help!");
       hm.put("swim" , "see what i mean?");
       hm.put("tafn" , "that's all for now");
       hm.put("tanj" , "there ain't no justice");
       hm.put("tanstaafl" , "there ain't no such thing as a free lunch");
       hm.put("tcp" , "t c p");
       hm.put("thankx" , "thanks");
       hm.put("thx" , "thanks");
       hm.put("tia" , "thanks in advance");
       hm.put("tic" , "tongue in cheek");
       hm.put("tntl" , "trying not to laugh");
       hm.put("tnx" , "thanks");
       hm.put("tnxe6" , "thanks a million");
       hm.put("tptb" , "the powers that be");
       hm.put("ttbomk" , "to the best of my knowledge");
       hm.put("ttfn" , "ta ta for now");
       hm.put("ttksf" , "trying to keep a straight face");
       hm.put("ttyl" , "talk to you later");
       hm.put("ty" , "thank you");
       hm.put("tyvm" , "thank you very much");
       hm.put("uok" , "are you ok?");
       hm.put("w/o" , "without");
       hm.put("wb" , "welcome back");
       hm.put("wdyt" , "what do you think?");
       hm.put("wibni" , "wouldnt it be nice if");
       hm.put("willg" , "will g");
       hm.put("wrt" , "with regard to,  or with respect to");
       hm.put("wtb" , "want to buy");
       hm.put("wtf" , "what the frack");
       hm.put("wtg" , "way to go!");
       hm.put("wtgp" , "want to go private?");
       hm.put("wth" , "what the hell?");
       hm.put("wygiswypf" , "what you get is what you pay for");
       hm.put("xoxoxo" , "kisses and hugs");
       hm.put("yaba" , "yet another bloody acronym");
       hm.put("yaun" , "yet another unix nerd");
       hm.put("ygti" , "you get the idea?");
       hm.put("ygwypf" , "you get what you pay for");
       hm.put("yiu" , "yes, i understand");
       hm.put("yiwgp" , "yes, i will go private");
       hm.put("ymmv" , "your mileage may vary");

  }
 
  
  
}
