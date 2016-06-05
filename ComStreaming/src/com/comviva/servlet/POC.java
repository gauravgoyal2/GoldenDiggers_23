package com.comviva.servlet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class POC {

    //private static final String EMAIL_PATTERN = "^[_A-Za-z0-9\\+]+(\\.[_A-Za-z0-9-]+)*@mahindracomviva.com";// + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
   
    
    public static void main(String[] args) {
	
	//Random random = new Random();
	//Integer value = random.nextInt(8999) + 1000; // will generate a number 1000 to 9999
	
	//Pattern pattern;
	 //Matcher matcher;
	//pattern = Pattern.compile(EMAIL_PATTERN);
	//matcher = pattern.matcher("gaurav.goyal2@mahindracomviva.com");
	//System.out.println(matcher.matches());
	
	try{
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH.mm.ss");
	    Date date1 = sdf.parse("2016-21-04 11.45.10");
	    Date date2 = sdf.parse("2016-20-04 11.45.10");
	    long timestamp = date1.getTime();
	    int result = date1.compareTo(date2);
	    switch(result){
	    case -1:
		System.out.println("date1 is greater");
		break;
	        case 0:
	            System.out.println("d1 is equal to d2");
	            break; 
	        case 1:
	            System.out.println("d1 is greater than d2");
	            break;
	             
	        default:
	            break;
	    }
	    
	}catch(Exception e){
	    e.printStackTrace();
	}
    }

    
}
