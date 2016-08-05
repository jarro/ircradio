package com.earthflare.android.ircradio;



import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

public class LineFormatter {
	
	private static final int standard 	= 0xFFBEBEBE;
	private static final int quit  		= 0xFFFF8C00;
	private static final int action 	= 0xFFFF1493;
	private static final int topic 		= 0xFFFFD700;
	private static final int kick 		= 0xFFFF6347;
	private static final int part 		= 0xFFFFFF00;
	private static final int notice 	= 0xFFBA55D3;
	private static final int invite 	= 0xFF6B8E23;
	private static final int mode   	= 0xFF0AB40A;
	private static final int response  	= 0xFFBEBEBE;
	
	public static List format(int type, int senderLength, int msgLength) {
		
		List<SpanFormat> spans = new ArrayList<SpanFormat>();	
		
		switch(type){
		
		case MessageType.STANDARD:
		case MessageType.PRIVATE:
			spans.add(new SpanFormat(0,senderLength + 3, Color.WHITE));
			if (senderLength + 3 < msgLength ) {
			  spans.add(new SpanFormat(senderLength + 3, msgLength , standard ));
			}
			break;
		case MessageType.JOIN:
		case MessageType.ENTERED:
			spans.add(new SpanFormat(0,msgLength,Color.GREEN));
			break;
		case MessageType.ERROR:
			spans.add(new SpanFormat(0,msgLength,Color.RED));
			break;
		case MessageType.QUIT:
			spans.add(new SpanFormat(0,msgLength,quit));
			break;
		case MessageType.ACTION:
		case MessageType.PRIVATE_ACTION:			
			spans.add(new SpanFormat(0,msgLength,action));
			break;
		case MessageType.TOPIC:
			spans.add(new SpanFormat(0,msgLength,topic));
			break;
		case MessageType.KICK:
			spans.add(new SpanFormat(0,msgLength,kick));
			break;
		case MessageType.PART:
			spans.add(new SpanFormat(0,msgLength,part));
			break;
		case MessageType.NOTICE:
		case MessageType.PRIVATE_NOTICE:			
			spans.add(new SpanFormat(0,msgLength,notice));
			break;
		case MessageType.INVITE:
			spans.add(new SpanFormat(0,msgLength,invite));
			break;
		case MessageType.MODE:
			spans.add(new SpanFormat(0,msgLength,mode));
			break;
		default:
			spans.add(new SpanFormat(0,msgLength,Color.WHITE));
			break;					
		}
		
	return spans;	
		
	}
	
	
}
