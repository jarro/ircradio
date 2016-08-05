package com.earthflare.android.ircradio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Backup extends TemplateChannel implements OnClickListener{

	private String backuppathnosdcard = "/apps/ircradio/backup.db";
    private String backupfolder = "/sdcard/apps/ircradio";
	private String backuppath = "/sdcard/apps/ircradio/backup.db";
	private String databasepath = "/data/data/com.earthflare.android.ircradio/databases/ircradio";
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		

		
		this.setContentView(R.layout.backup);
		initDrawer();
		
		Button backupButton = (Button) findViewById(R.id.Button01);	      
	    backupButton.setOnClickListener(this); 
	    Button restoreButton = (Button) findViewById(R.id.Button02);	      
	    restoreButton.setOnClickListener(this);
	    TextView tv1 = (TextView) findViewById(R.id.TextView01);
	    tv1.setText(tv1.getText() + "\n" + backuppathnosdcard);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
  	    case R.id.Button01:
  		  try {
					
  			      this.backupDataBase();
  			      
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
  		  break;
  		  
  	  case R.id.Button02:
  		  
  		  confirm();
  		  
  		  break;    
  	   	}
	}

	
	private void backupDataBase() throws IOException{
         
		File file = new File (backupfolder);
		file.mkdirs();
		
				
        //Path to the just created empty db, which I carefully closed so I have safe write access
        FileOutputStream databaseOutputStream = new FileOutputStream(backuppath);
        FileInputStream databaseInputStream = new FileInputStream(databasepath);

        //make buffered streams
		BufferedInputStream bufin = new BufferedInputStream(databaseInputStream);
		BufferedOutputStream bufout = new BufferedOutputStream(databaseOutputStream);
        
        byte[] buffer = new byte[1];
        int length;
        while ( (length = bufin.read(buffer)) > 0 ) {
                bufout.write(buffer);                
        }

        //Close the streams
        bufout.flush();
        bufout.close();
        bufin.close();
        
       
        
        finish(); 
 } 
	
	
	private void restoreDatabase() throws IOException{
        		
		//check file exists
		File dbfile = new File(backuppath);
		if (!dbfile.exists()) {
			
			DialogInfo alert = new DialogInfo(this, this.getString(R.string.alert_filenotfound),this.getString(R.string.alert_backupnotfound));
			alert.show();
			return;
			
		}
		
        //Path to the just created empty db, which I carefully closed so I have safe write access
        FileOutputStream databaseOutputStream = new FileOutputStream(databasepath);
        FileInputStream databaseInputStream = new FileInputStream(backuppath);

        //make buffered streams
		BufferedInputStream bufin = new BufferedInputStream(databaseInputStream);
		BufferedOutputStream bufout = new BufferedOutputStream(databaseOutputStream);
        
        byte[] buffer = new byte[1];
        int length;
        while ( (length = bufin.read(buffer)) > 0 ) {
                bufout.write(buffer);                
        }

        //Close the streams
        bufout.flush();
        bufout.close();
        bufin.close();
        finish();
 } 
	
	
	public void confirm() {
		
		ConfirmListener cl = this.new ConfirmListener();
		Builder alert = new AlertDialog.Builder(this)
        .setTitle(R.string.backup_confirmimport)
        .setMessage(getString(R.string.backup_confirmimportmessage))
        .setPositiveButton(R.string.button_yes, cl)
        .setNeutralButton(R.string.button_cancel, cl)
        .setIcon(android.R.drawable.ic_dialog_alert);     
       alert.show();
	}
	
	
	class ConfirmListener implements DialogInterface.OnClickListener {      
	     public long id;      
	     @Override
	     public void onClick(DialogInterface arg0, int button) {      
	       if (button == DialogInterface.BUTTON1)  {
	         //delete entry
	         try {
				restoreDatabase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       }     
	     }    
	   }
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}  
	
}
