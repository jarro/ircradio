package com.earthflare.android.ircradio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jibble.pircbot.User;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PromptUser extends Dialog implements OnItemClickListener {

	private Button  saveButton;
	private Button  cancelButton;
    private Context ctx;
	private UserAdapter adapter;
  
	public PromptUser(Context context) {
		super(context);
    
		this.ctx = context;
		
		
		}
	

	protected void onCreate(Bundle savedInstanceState) {
		initialize();
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		User user = (User)adapter.getItem(arg2);
		ActChatLog acl = (ActChatLog)ctx;
		EditText et = (EditText)acl.findViewById(R.id.textinput);
		et.setText("/msg " + user.getNick() + " ");
		
		//move carat to end of line
		Editable etext = et.getText();
		int position = etext.length();
		Selection.setSelection(etext, position); 
		
		this.dismiss();
		
	}

	
	protected void initialize() {
		
		ListView lv = (ListView)this.findViewById(R.id.ListViewUsers);
		adapter = new UserAdapter(ctx);
	    lv.setAdapter(adapter);
	    lv.setOnItemClickListener(this);
	    
		this.setTitle(Globo.chatlogActChannelName);
	    
	}
  
  
    
    public class UserAdapter extends BaseAdapter {
        

        private List<User> users = new ArrayList<User>();

        public UserAdapter(Context c) {
            mContext = c;
            
            if (Globo.botManager.ircBots.containsKey(Globo.chatlogActNetworkName) && Globo.chatlogActType.equals("channel")  ) {
            	
            	RadioBot rb = Globo.botManager.ircBots.get(Globo.chatlogActNetworkName);
            	if (rb.inChannel(Globo.chatlogActChannelName)) {
            		//populate array
            	    User[] userArray = rb.getUsers(Globo.chatlogActChannelName);
            	    users =  Arrays.asList(userArray);
            	    Collections.sort(users);
            	    
            	}
            	
            }
            
        }

        public int getCount() {
            return users.size();
        }

        public Object getItem(int position) {
            return users.get(position);        	
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
                    	
        	TextView  tv = (TextView)TextView.inflate(mContext, R.layout.row_userlist, null);        	        	
        	User user = users.get(position); 
        	tv.setText(user.getPrefix() + user.getNick());        	
        	return tv;
        }

        private Context mContext;

        
    }

	
	
}