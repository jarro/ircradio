package com.earthflare.android.ircradio;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

class LanguageResource {
	private  String[] languagekeys;
	private  Map<String,Integer> filemap = new HashMap<String,Integer>();
	private  Map<String,Map<String,String>> tranlatemap = new HashMap<String,Map<String,String>>();
	private  Context ctx;
	
	LanguageResource(Context ctx) {
	
		this.ctx = ctx;
		//filemap.put("lang_keys", R.raw.lang_keys );
		//filemap.put("en", R.raw.en);
		//filemap.put("en_GB", R.raw.en);
		//filemap.put("fr", R.raw.fr );
		//filemap.put("it", R.raw.it );
		//filemap.put("de", R.raw.de );
		//filemap.put("es", R.raw.es );
		
		languagekeys = new String[0];
		//loadkeys();
		
	}
	
	
   	

	public String getLRString(String key, String language) {

        return ("get LR string");
        /*
		//check if language loaded
		
		if ( !this.tranlatemap.containsKey(language) ) {
			//load language
			this.loadlanguage(language);
		}
		
				
		//return string
		return tranlatemap.get(language).get(key);
		*/

	}

	private void loadkeys() {
		
		 String text;
    	
    	
    	 try { InputStream is = ctx.getResources().openRawResource (filemap.get("lang_keys")); 
         int size = is.available();
         byte[] buffer = new byte[size];
         is.read(buffer);
         is.close();
         text = new String(buffer);
        
    
         } catch (IOException e) { 
           // Should never happen! 
           throw new RuntimeException(e); 
         }
     	
         languagekeys = text.split("\r?\n|\r");
      
	}
	
	
	private void loadlanguage(String language) {
		
		/*
		
		Map<String,String> langvalues= new HashMap<String,String>();
    	String text;
    	
       
    	
        try { InputStream is = ctx.getResources().openRawResource (filemap.get(language)); 
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        text = new String(buffer);
       
   
        } catch (IOException e) { 
          // Should never happen! 
          throw new RuntimeException(e); 
        }
    	
        String[] values = text.split("\r?\n|\r");
    	
        for (int i=0; i<values.length; i++) {
        	
        	langvalues.put(this.languagekeys[i], values[i]);
        	
        }
		
		this.tranlatemap.put(language, langvalues);

		*/
	}

	
}
    

