package com.earthflare.android.ircradio;

import java.io.Serializable;

import android.text.SpannableString;

public class SpannableStringSerial extends SpannableString implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public SpannableStringSerial(CharSequence source) {
		super(source);
		
	}
  	
}
