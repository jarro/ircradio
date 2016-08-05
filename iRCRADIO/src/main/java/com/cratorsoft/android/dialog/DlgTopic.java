package com.cratorsoft.android.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;

public class DlgTopic extends DialogFragment{



    Bundle data;

    public static DlgTopic newInstance() {

        DlgTopic frag = new DlgTopic();
        return frag;
    }



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {


        SpannableString topic = new SpannableString(getString(R.string.ui_couldnotretrievetopic));

        if (Globo.botManager.ircBots.containsKey(Globo.chatlogActNetworkName) ) {
            if (Globo.botManager.ircBots.get(Globo.chatlogActNetworkName).channelMap.containsKey(Globo.chatlogActChannelName)) {
                if (Globo.botManager.ircBots.get(Globo.chatlogActNetworkName).channelMap.get(Globo.chatlogActChannelName).containsKey("topic")) {
                    topic = new SpannableString (Globo.botManager.ircBots.get(Globo.chatlogActNetworkName).channelMap.get(Globo.chatlogActChannelName).get("topic"));
                }
            }
        }


		
		MaterialDialog mdlg = new MaterialDialog.Builder(this.getActivity())
			.title(getString(R.string.ui_topic) + Globo.chatlogActChannelName)
			.customView(R.layout.dlg_topic,true)
			.positiveText(this.getActivity().getString(R.string.button_ok))
			.build();

        View v = mdlg.getCustomView();
        TextView tv = (TextView) v.findViewById( R.id.TextView01);
        android.text.util.Linkify.addLinks(topic, android.text.util.Linkify.ALL);
        tv.setText(topic);

        return mdlg;
			
		
		
	}
	
	
}
