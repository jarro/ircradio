package com.cratorsoft.android.confirmback;

import android.os.Bundle;

import com.cratorsoft.android.dialog.DlgConfirmBackPressed;
import com.cratorsoft.android.taskmanager.BusyTaskFragment;
import com.earthflare.android.ircradio.R;

public class FragConfirmBackPressed extends BusyTaskFragment {

    
    

    @Override
    public void onResume() {        
        ((ActConfirmBackPressed)this.getActivity()).setFragConfirmBackPressed(this);
        super.onResume();        
    }

    
    
    @Override
    public void onPause() {
        ((ActConfirmBackPressed)this.getActivity()).setFragConfirmBackPressed(null);
        super.onPause();
    }
    

    public void onBackPressed(){
        Bundle bundle = new Bundle();
        bundle.putString("title", getString(R.string.alert_title_confirmback));
        bundle.putString("message", getString(R.string.alert_message_confirmback));

        DlgConfirmBackPressed dialog = DlgConfirmBackPressed.newInstance(bundle);
        dialog.show(this.getFragmentManager(), "confirmbackpressed"); 
    }
    

}
