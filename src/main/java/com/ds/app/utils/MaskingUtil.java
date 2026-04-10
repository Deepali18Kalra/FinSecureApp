package com.ds.app.utils;

public class MaskingUtil {
	

    private MaskingUtil() {}

	    public static String maskEmail(String email) {
	        if (email == null || !email.contains("@")) return "***@***.com";
	 
	        String[] parts = email.split("@");
	        String name   = parts[0];   
	        String domain = parts[1];   
	 
	        String visiblePart = name.length() > 3
	                ? name.substring(0, 3)
	                : name;
	 
	        return visiblePart + "***@" + domain;
	    }
	 
	    public static String maskPhone(String phone) {
	        if (phone == null || phone.length() < 4) return "******";
	 
	        String visible = phone.substring(phone.length() - 4);
	        return "*".repeat(phone.length() - 4) + visible;
	    }
	    
	 
	    public static String maskLast(int visibleCount, String value) {
	        if (value == null) return null;
	        if (value.isBlank()) return value;
	        if (value.length() <= visibleCount) return value;
	
	        String visible = value.substring(
	                value.length() - visibleCount);
	        return "X".repeat(value.length() - visibleCount) + visible;
	    }
		
	
}//end class
