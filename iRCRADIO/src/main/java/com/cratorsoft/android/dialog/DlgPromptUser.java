package com.cratorsoft.android.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cratorsoft.android.chat.UserAdapter;
import com.cratorsoft.android.frag.FragChatLog;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;

import org.jibble.pircbot.User;

public class DlgPromptUser extends DialogFragment implements AdapterView.OnItemClickListener{





    public static DlgPromptUser newInstance() {

        DlgPromptUser frag = new DlgPromptUser();

        return frag;
    }



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {



		String title = Globo.chatlogActChannelName;

		
		MaterialDialog mdlg = new MaterialDialog.Builder(this.getActivity())
			.title(title)
			//.customView(R.layout.dlg_prompt_user)
            .adapter(new UserAdapter(this.getActivity()),null)
			.positiveText(this.getActivity().getString(R.string.button_ok))
            .build();

        /*
        ListView listview = (ListView)mdlg.getCustomView().findViewById(R.id.ListViewUsers);

        UserAdapter adapter = new UserAdapter(this.getActivity());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
        */


		return mdlg;
			
		
		
	}


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        UserAdapter adapter = (UserAdapter) arg0.getAdapter();
        User user = (User)adapter.getItem(arg2);
        FragChatLog acl = (FragChatLog) this.getTargetFragment();
        EditText et = (EditText)acl.getView().findViewById(R.id.textinput);
        et.setText("/msg " + user.getNick() + " ");

        //move carat to end of line
        Editable etext = et.getText();
        int position = etext.length();
        Selection.setSelection(etext, position);

        this.dismiss();

    }
	
}
