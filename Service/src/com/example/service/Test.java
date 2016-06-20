package com.example.service;

import java.util.Arrays;

public class Test {
	public static void main(String[] args) {
		//String string1 = "1232313";
		//replace(string1);
		//System.out.println(string1);
		String string1 = "111";
		String string2 = "222";
		
		replace(string1, string2);
		System.out.println(string1+"   "+string2);
	}
	public static void replace(String a,String b){
		String temp = "";
		
		temp =a;
		a = b;
		b = temp;
		//System.out.println(string.replace('1', '2'));;
		//System.out.println(string);
	}
	public static byte[] intToBytes( int value )   
	{   
	    byte[] src = new byte[4];  
	    src[3] =  (byte) ((value>>24) & 0xFF);  
	    src[2] =  (byte) ((value>>16) & 0xFF);  
	    src[1] =  (byte) ((value>>8) & 0xFF);    
	    src[0] =  (byte) (value & 0xFF);                  
	    return src;   
	}  
	public static int bytesToInt(byte[] src, int offset) {  
	    int value;    
	    value = (int) ((src[offset] & 0xFF)   
	            | ((src[offset+1] & 0xFF)<<8)   
	            | ((src[offset+2] & 0xFF)<<16)   
	            | ((src[offset+3] & 0xFF)<<24));  
	    return value;  
	}  
}
