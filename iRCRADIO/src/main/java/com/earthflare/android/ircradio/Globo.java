package com.earthflare.android.ircradio;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cratorsoft.android.db.DatabaseHelper;
import com.cratorsoft.android.events.RefreshToggles;
import com.cratorsoft.android.language.SCLM;
import com.cratorsoft.android.ttslanguage.TTSPipeline;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.SpannableString;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

public class Globo {

     public static final int CHANNELLOGMAX = 1000;

     public static AudioManager AM;
     public static final String DEFAULT = "en";

     public static boolean debug = false;

     //Context
     public static Context ctx;


     //Database
     public static SQLiteDatabase db;
     public static DatabaseHelper dbHelper;
     public static Handler dbHandler;

     //Titles
     public static CharSequence drawerTitle = "IRC Radio";
     public static CharSequence drawerSubtitle = "";
     public static SpannableString fragTitle = new SpannableString("");
     public static CharSequence fragSubtitle = "";

     //Tabs
     public static int tabaccount=0;

     //channel prefs
     public static boolean pref_chan_timestamp = false;
     public static boolean pref_chan_join = true;
     public static boolean pref_chan_part = true;
     public static boolean pref_chan_kick = true;
     public static boolean pref_chan_quit = true;
     public static boolean pref_chan_mode = true;
     public static boolean pref_chan_invite = true;
     public static boolean pref_chan_message = true;

     //speak prefs
     public static boolean ttsspeak_action = true;
     public static boolean ttsspeak_invite = true;
     public static boolean ttsspeak_join = true;
     public static boolean ttsspeak_kick = true;
     public static boolean ttsspeak_message = true;
     public static boolean ttsspeak_mode = true;
     public static boolean ttsspeak_notice = true;
     public static boolean ttsspeak_part = true;
     public static boolean ttsspeak_privatemessage = true;
     public static boolean ttsspeak_privatenotice = true;
     public static boolean ttsspeak_quit = true;

     public static boolean toaststate = false;
     public static boolean mutestate = false;
     public static boolean headphoneconnected = false;
     public static boolean pref_headphonemute = false;
     public static boolean phonebusystate = false;
     public static boolean globalToast = false;
     public static boolean pref_replacechat = true;
     public static boolean authorSpeech = true;
     public static boolean pref_mutemusic = false;
     public static boolean pref_splitstream = false;
     public static int pref_call_volume = 3;
     public static int default_call_volume = 3;
     public static boolean pref_notification_newprivatemessage = true;
     public static boolean pref_notification_channelmessage = true;

     //chat prefs
     public static boolean pref_chan_showchannav = true;
     public static boolean pref_chan_showactnav = true;

     public static float pref_pitch = 1;
     public static float pref_speed = 1;
   //VISIBLE Activity

     public static boolean actListChannelsVisible = false;
     public static Handler handlerListChannels;
     public static long listChannelsNetwork = 0;

     public static boolean actChannelListVisible = false;
     public static Handler handlerChannelList;

     public static boolean actNoticeVisible = false;
     public static Handler handlerNotice;

     public static boolean actAccountListVisible = false;
     public static Handler handlerAccountList;

     public static boolean actDashboard = false;
     public static Handler handlerDashboard;

     public static boolean actChatlogVisisble = false;
     public static AtomicBoolean chatlogChanNav;
     public static AtomicBoolean chatlogActNav;
     public static String  chatlogActType = "";
     public static long chatlogActNetworkName = 0;
     public static String chatlogActChannelName = "";
     public static String  chatlogActAccountName = "";
     public static Handler handlerChatLog;








     public static boolean ttsSwitch = true;


     //TOAST
     public static Toast toast;

     //TTS
     //public static TextToSpeech myTts;
     //track if we have muted music so we know wether or not to mute/unmute music
     //public static boolean musicmuted = false;

     public static TextToSpeech.OnUtteranceCompletedListener ttsCompleteListener = new TextToSpeech.OnUtteranceCompletedListener() {

           @Override
           public void onUtteranceCompleted(String utteranceId) {

               //speak next line if not empty
               MessageQueue.INSTANCE.messanger();

           }
          };


     public static void setTTSChannel(long accountid, String channel, String language) {

         SCLM.INSTANCE.setChannelLanguage(accountid, channel, language);
         Globo.voiceChannel = channel;
         Globo.voiceServer = accountid;
         Globo.voiceType = "channel";
         Globo.purgeTTS();

     }

    public static void setTTSServer(long accountid, String language) {

        SCLM.INSTANCE.setServerLanguage(accountid,language);
        Globo.voiceChannel = "";
        Globo.voiceServer = accountid;
        Globo.voiceType = "server";
        Globo.purgeTTS();

    }

     public static void PauseTTS () {

         if (Globo.phonebusystate == false) {
           Globo.phonebusystate = true;
           TTSPipeline.getInstance().stopTTS();
           MessageQueue.INSTANCE.idle.set(true);
           GloboUtil.abandonAudioFocus();
         }

     }

     public static void UnPauseTTS () {
         Globo.phonebusystate = false;
         MessageQueue.INSTANCE.messanger();
     }


    public static void purgeTTS () {


            TTSPipeline.getInstance().stopTTS();

            try {
                MessageQueue.INSTANCE.idle.set(true);
                MessageQueue.INSTANCE.flush();
            }catch(Exception e){

            }
            GloboUtil.abandonAudioFocus();


    }


     public static void togglemuteTTS () {

         try{
         if (Globo.mutestate == true) {
             Globo.mutestate = false;
             MessageQueue.INSTANCE.messanger();
         }else {
             Globo.mutestate = true;
             TTSPipeline.getInstance().stopTTS();
             GloboUtil.abandonAudioFocus();
         }
         }catch(Exception e){

         }finally{
             GloboUtil.setBoolean(Globo.ctx, "pref_mutestate", Globo.mutestate);
         }

         refreshToggles();

     }


     public static void toggleToastState () {

         if(Globo.toaststate){
             Globo.toaststate = false;
         }else{
             Globo.toaststate = true;
         }
         GloboUtil.setBoolean(Globo.ctx, "pref_toaststate", Globo.toaststate);
         refreshToggles();
     }

     public static void refreshToggles(){

         EventBus.getDefault().post(new RefreshToggles());

         Intent serviceIconUpdate = new Intent(Globo.ctx, IrcRadioService.class);
         serviceIconUpdate.putExtra("action", "updateicon");
         Globo.ctx.startService(serviceIconUpdate);

     }


     //RadioBots
     public static BotManager botManager = new BotManager();

     //Voice and Toast channel
     public static long voiceServer = 0;
     public static String voiceChannel = "";
     public static String voiceType = "channel";
     //public static String voiceLanguage = "en";
     //public static Locale voiceDefault;
     //public static boolean voicedefaultLoaded = false;
     //public static boolean voicechannelLoaded = false;
     //public static LanguageResource voiceResource;

     //Wifi Wake Lock
     public static WifiLocker wifiLocker;


     public static Map<Integer,String> languagecodes = new HashMap<Integer,String>();
     public static Map<String,Integer> languagecodesreverse = new HashMap<String,Integer>();
     static{
     languagecodes.put(0, "en");
     languagecodes.put(1, "en_GB");
     languagecodes.put(2, "fr");
     languagecodes.put(3, "it");
     languagecodes.put(4, "de");
     languagecodes.put(5, "es");
     for( Integer inty: languagecodes.keySet() ) {
        languagecodesreverse.put(languagecodes.get(inty) , inty );
     }
     }


     //aduio listner
     public static AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
         @Override
         public void onAudioFocusChange(int focusChange) {

         }
     };

   }
