/*
	Copyright (C) 2008 Jeffrey Sharkey, http://jsharkey.org/
	
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.earthflare.android.cross;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class CrossView extends FrameLayout {

	
    boolean swipefail;
	Context context;
	
	public CrossView(Context context) {
		super(context);
		this.setClickable(true);
        this.context = context;
	}
	
	public CrossView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setClickable(true);
		this.context = context;
	}
	
	public CrossView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setClickable(true);
		this.context = context;
	}
	
	public MotionEvent downStart = null;
	
	public boolean onInterceptTouchEvent(MotionEvent event) {
		
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// keep track of the starting down-event
			downStart = MotionEvent.obtain(event);
			swipefail = false;
			break;
		case MotionEvent.ACTION_MOVE:
			// if moved horizontally more than slop*2, capture the event for ourselves
			if (!swipefail) {
			
				float deltaX = event.getX() - downStart.getX();
				float deltaY = event.getY() - downStart.getY();
		
				if(Math.abs(deltaX) > ViewConfiguration.get(context).getScaledTouchSlop()    ){
					return true;
				}else if (Math.abs(deltaY) > Math.abs(deltaX)){
					swipefail = true;
					return false;
				}
					
			}
				break;
				
		}
		
		// otherwise let the event slip through to children
		return false;
	
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		
		// check if we crossed an item
		float targetWidth = this.getWidth() / 4;
		float deltaX = event.getX() - downStart.getX(),
			deltaY = event.getY() - downStart.getY();
		
		boolean movedAcross = (Math.abs(deltaX) > targetWidth);
		boolean steadyHand = (Math.abs(deltaX / deltaY) > 2);
		
		if(movedAcross && steadyHand) {
			
			//do nothing if not end of motion
			if ( event.getAction() != MotionEvent.ACTION_UP) {return true;}
			
			boolean crossed = (deltaX > 0);
			
					
			
			// pass crossed event to any listeners
			
				
				
				listener.onCross( crossed);
				
			
			
			// and return true to consume this event
			return true;
	
		}
		
		return false;
	}

	private OnCrossListener listener; 
	
	public void addOnCrossListener(OnCrossListener listener) {
		this.listener = listener;
	}
	

}
