package com.cratorsoft.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.net.Uri;

public class FileUtil {

	//static String folderpath = "/apps/medhelper";
	
	
	
	public static BufferedWriter getStream(String path, String filename) {
		
		
		//File dirpath = new File(path);
		File dirpath = new File("path");
		dirpath.mkdirs();
		
		
		BufferedWriter bufferedWriter = null;
		
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(path + filename));
		} catch (IOException e1) {
			return null;
		}
		
				
		return bufferedWriter;
	
	}
	
	
	public static Uri getUri(String filepath) {		
		return Uri.parse ("file://" + filepath);
	}
	
	
	 public static String loadfile(Context ctx, Uri uri) throws URISyntaxException {
	    	
	    	//Get the text file
		    File file1 = new File(new URI(uri.toString() ));


	    	//Read text from file
	    	StringBuilder text = new StringBuilder();

	    	try {
	    	    BufferedReader br = new BufferedReader(new FileReader(file1));
	    	    String line;
	    	    
	    	    while ((line = br.readLine()) != null) {
	    	        text.append(line);
	    	        text.append('\n');
	    	    }
	    	    br.close();
	    	}
	    	catch (IOException e) {
	    	    //You'll need to add proper error handling here
	    	}

	    	return text.toString();
	    }
		
	    
	    
	    public static String readRawTextFile(Context ctx, int resId)
	    {
	         InputStream inputStream = ctx.getResources().openRawResource(resId);

	            InputStreamReader inputreader = new InputStreamReader(inputStream);
	            BufferedReader buffreader = new BufferedReader(inputreader);
	             String line;
	             StringBuilder text = new StringBuilder();

	             try {
	               while (( line = buffreader.readLine()) != null) {
	                   text.append(line);
	                   text.append('\n');
	                 }
	           } catch (IOException e) {
	               return null;
	           }
	             return text.toString();
	    }
	
	    
	    public static void writeStream(InputStream in, String fullpath) throws IOException {
	    	
	    
	    	//File file = new File(backupfolder);						
			//file.mkdirs();

			// Path to the just created empty db, which I carefully closed so I have
			// safe write access
			FileOutputStream databaseOutputStream = new FileOutputStream(fullpath);
			
			// make buffered streams
			BufferedInputStream bufin = new BufferedInputStream(in);
			BufferedOutputStream bufout = new BufferedOutputStream(
					databaseOutputStream);

			byte[] buffer = new byte[1];
			int length;
			while ((length = bufin.read(buffer)) > 0) {
				bufout.write(buffer);
			}

			// Close the streams
			bufout.flush();
			bufout.close();
			bufin.close();
	    
	    }
	
	    public static void copyFile(File src, File dst) throws IOException {
	        FileChannel inChannel = new FileInputStream(src).getChannel();
	        FileChannel outChannel = new FileOutputStream(dst).getChannel();
	        try {
	           inChannel.transferTo(0, inChannel.size(), outChannel);
	        } finally {
	           if (inChannel != null) {
	              inChannel.close();
	           }
	           if (outChannel != null) {
	              outChannel.close();
	           }
	        }
	     }
	    
	    
}
