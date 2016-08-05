package com.cratorsoft.android.aamain;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.earthflare.android.ircradio.R;

public class Fargs {

    public Bundle bundle;
    
    public static Fargs create(){
        return new Fargs();
    }
    
    public static Fargs create(Fragment frag){
        Fargs farg = new Fargs();
        farg.bundle = frag.getArguments();
        return farg;
    }
    
    public Fargs(){
        bundle = new Bundle();
    }

    public Fargs(Bundle bundle){
        this.bundle = bundle;
    }
    
    //Action
    public Fargs setAction(int action){        
        bundle.putInt("action",action);
        return this;        
    }
    
    
    public int getAction(){
        return bundle.getInt("action");
    }
    

    //Uid
    public Fargs setUid(long uid){
        bundle.putLong("uid",uid);
        return this;
    }

    public long getUid(){
        if (bundle.containsKey("uid")){
            return bundle.getLong("uid",-1);
        }else{
            return -1;
        }
    }

    
    //AccountId
    public Fargs setAccount(Long accountid){
        if (accountid != null) {
            bundle.putLong("accountid",accountid);
        }        
        return this;        
    }
    
    public Long getAccount(){
        if (bundle.containsKey("accountid")) {
            return bundle.getLong("accountid");
        }else{
            return null;
        }                
    }

    //ChannelId
    public Fargs setChannel(Long channelid){
        if (channelid != null) {
            bundle.putLong("channelid",channelid);
        }
        return this;
    }

    public Long getChannel(){
        if (bundle.containsKey("channelid")) {
            return bundle.getLong("channelid");
        }else{
            return null;
        }
    }

    //Network
    public Fargs setNetwork(Long network){
        if (network != null) {
            bundle.putLong("network",network);
        }
        return this;
    }

    public Long getNetwork(){
        if (bundle.containsKey("network")) {
            return bundle.getLong("network");
        }else{
            return null;
        }
    }

    
    //Title
    public Fargs setTitle(String title){        
        if (title != null) {
            bundle.putString("title",title);
        }        
        return this;        
    }
    
    public String getTitle(){
        if (bundle.containsKey("title")) {
            return bundle.getString("title");
        }else{
            return "";
        }
    }
    
    //Message
    public Fargs setMessage(String message){
        if (message != null) {
            bundle.putString("message",message);
        }
        return this;
    }

    public String getMessage(){
        if (bundle.containsKey("message")) {
            return bundle.getString("message");
        }else{
            return "";
        }
    }

    //ChannelName
    public Fargs setChannelName(String channelName){
        if (channelName != null) {
            bundle.putString("channelname",channelName);
        }
        return this;
    }

    public String getChannelName(){
        if (bundle.containsKey("channelname")) {
            return bundle.getString("channelname");
        }else{
            return "";
        }
    }

    //AccountName
    public Fargs setAccountName(String accountName){
        if (accountName != null) {
            bundle.putString("accountname",accountName);
        }
        return this;
    }

    public String getAccountName(){
        if (bundle.containsKey("accountname")) {
            return bundle.getString("accountname");
        }else{
            return "";
        }
    }


    //Acttype
    public Fargs setActtype(String acttype){
        if (acttype != null) {
            bundle.putString("acttype",acttype);
        }
        return this;
    }

    public String getActtype(){
        if (bundle.containsKey("acttype")) {
            return bundle.getString("acttype");
        }else{
            return null;
        }
    }


    //TargetFragment
    public Fargs setTargetFrag(String targetFrag){
        if (targetFrag != null) {
            bundle.putString("targetfrag",targetFrag);
        }
        return this;
    }

    public String getTargetFrag(){
        if (bundle.containsKey("targetfrag")) {
            return bundle.getString("targetfrag");
        }else{
            return "";
        }
    }

    
    //String 
    public Fargs setManualResource(String str){        
        if (str != null) {
            bundle.putString("manualitem",str);
        }        
        return this;        
    }
    
    public String getManualResource(){
        if (bundle.containsKey("manualitem")) {
            return bundle.getString("manualitem");
        }else{
            return null;
        }
    }


    //notificationlink
    public Fargs setNotificationLink(boolean notificationlink){
           bundle.putBoolean("notificationlink",notificationlink);
        return this;
    }

    public boolean getNotificationLink(){
        return bundle.getBoolean("targetfrag",false);
    }


    //preference resoure
    public Fargs setPreferencesResource(int preferencesresource){
        bundle.putInt("preferencesresource", preferencesresource);
        return this;
    }

    public int getPreferencesResource(){
        return bundle.getInt("preferencesresource", R.xml.preferences);
    }

}
