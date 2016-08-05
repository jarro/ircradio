package com.earthflare.android.ircradio;

import java.util.Comparator;

public class ChannelComparator {

	public static Comparator nameComparator = new Comparator() {
		
		public int compare(Object person, Object anotherPerson) {
			    String lastName1 = ((Channel) person).name.toUpperCase();		    
			    String lastName2 = ((Channel) anotherPerson).name.toUpperCase();
		        return lastName1.compareToIgnoreCase(lastName2);
			}	
		};
		
		public static Comparator nameComparatorDescending = new Comparator() {
			   public int compare(Object person, Object anotherPerson) {
				    String lastName1 = ((Channel) person).name.toUpperCase();		    
				    String lastName2 = ((Channel) anotherPerson).name.toUpperCase();
			        return lastName2.compareToIgnoreCase(lastName1);
				}	
		};
		
		
		public static Comparator userComparator = new Comparator() {
			  public int compare(Object person, Object anotherPerson) {
			    Integer lastName1 = ((Channel) person).users;		    
			    Integer lastName2 = ((Channel) anotherPerson).users;
		        return lastName1.compareTo(lastName2);
			}	
		};
		
		public static Comparator userComparatorDescending = new Comparator() {
			  public int compare(Object person, Object anotherPerson) {
				  
				Integer lastName1 = ((Channel) person).users;		    
			    Integer lastName2 = ((Channel) anotherPerson).users;
		        return lastName2.compareTo(lastName1);
		        
			}	
		};
	
	
}
