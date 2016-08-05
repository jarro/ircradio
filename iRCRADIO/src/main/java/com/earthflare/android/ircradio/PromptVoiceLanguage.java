package com.earthflare.android.ircradio;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class PromptVoiceLanguage extends AlertDialog.Builder implements OnClickListener {

	private Button  saveButton;
	private Button  cancelButton;
    private Context ctx;
	
    private String  channel;
    private long  network;
    private String  type;
    
  
	
	public PromptVoiceLanguage(Context context, String type,  long network, String channel ) {
		super(context);
    
		this.ctx = context;
		this.type = type;
		this.network = network;
		this.channel = channel;
		
		
		this.setPositiveButton(ctx.getString(R.string.button_ok), this);
		this.setNegativeButton(ctx.getString(R.string.button_cancel), null);
		  
		View content = View.inflate(ctx, R.layout.promptvoicelanguage, null);
		  
		this.setView(content);
		  
		  
		  this.setTitle(R.string.alert_title_selectlanguage);
		
		}
	


@Override
public void onClick(DialogInterface dialog, int which) {
	// TODO Auto-generated method stub
	AlertDialog ad = (AlertDialog)dialog;
	
	switch(which) {
		case DialogInterface.BUTTON_POSITIVE:
            /*
			String language = Globo.languagecodes.get( ((Spinner)ad.findViewById(R.id.language)).getSelectedItemPosition()); 
			Globo.setTTS(network, channel, type, language, ctx);
			Globo.botManager.refreshUI(ctx);
			*/

			break;
	}
}


  
  
    
}
	
