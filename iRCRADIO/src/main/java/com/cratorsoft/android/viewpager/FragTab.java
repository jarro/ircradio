package com.cratorsoft.android.viewpager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragTab extends Fragment {

    
    @Override
    public void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        this.setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {                        
        Bundle args = this.getArguments();
        int layout = args.getInt("contentview");
        return inflater.inflate( layout, container , false);        
    }


    
    
}
