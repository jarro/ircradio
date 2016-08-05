package com.earthflare.android.buttons;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

public class TextButton extends Button{


	public TextButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TextButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TextButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void drawableStateChanged() {
		// TODO Auto-generated method stub
		
	    if (isPressed()) {
	    	
	    	this.setShadowLayer(10, 5, 5, Color.RED);
	    	
	    }else if( isSelected() || isFocused() ) {
			
	    	this.setShadowLayer(10, 5, 5, Color.BLUE);
			
		}else {
			
			this.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
		}
		
		
		super.drawableStateChanged();
	}

	
	
	
}
