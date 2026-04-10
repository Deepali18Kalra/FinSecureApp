package com.ds.app.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

class MaskingUtilTest {
	
	//mask email

	@Test
	@Description("mask emial method test")
	void testMaskEmail() {
		
		//sample input
		String email = "john.doe@gmail.com";
		
		//expected output
		String expectedOutput = "joh***@gmail.com";
		
		//call actua; method and expected output from method
		String result = MaskingUtil.maskEmail(email);
		
		//asert
		assertEquals(expectedOutput, result);

	}
	
	//mask email -  null
	@Test
	@Description("mask emial null input method test")
	void testMaskEmail_Null() {
		
		//sample input
		String email = null;
		
		//expected output
		String expectedOutput = "***@***.com";
		
		//call actua; method and expected output from method
		String result = MaskingUtil.maskEmail(email);
		
		//asert
		assertEquals(expectedOutput, result);

	}
	

	@Test
	@Description("mask phone method test")
	void testMaskPhone() {
		
		// sample input
		String phone = "9876543210";
		
		//expected output
		String expectedOutput = "******3210";
		
		String result = MaskingUtil.maskPhone(phone);
		
		assertEquals(expectedOutput, result);
	
	}
	
    //  maskPhone — null
    @Test
    @Description("mask phone null input method test")
    void testMaskPhone_Null() {

        // Sample Input
        String phone = null;

        // Expected Output
        String expectedOutput = "******";

        // Call actual method and Expected output from method
        String result = MaskingUtil.maskPhone(phone);

        // Assert
        assertEquals(expectedOutput, result);
    }

	@Test
	@Description("mask last method test")
	void testMaskLast() {
		
		// sample input
		int visibleCount = 4;
		String documentNumber = "1234567890";
		
		// expected output
		 String expectedOutput = "XXXXXX7890";
		 
		//call actual method and expected output from method
		String result = MaskingUtil.maskLast(visibleCount, documentNumber);
		
		assertEquals(expectedOutput, result);
			
	}
	
	@Test
	@Description("mask last null input method test")
	void testMaskLast_Null() {

	        // Sample Input
	        int visibleCount = 4;
	        String documentNumber = null;

	        // Call actual method and Expected output from method
	        String result = MaskingUtil.maskLast(
	                visibleCount, documentNumber);

	        // Assert
	        assertNull(result);
	}
	
	
	 //   maskEmail — short name
    @Test
    @Description("mask email short name method test")
    void testMaskEmail_ShortName() {

        // Sample Input
        String email = "ab@gmail.com";

        // Expected Output
        String expectedOutput = "ab***@gmail.com";

        // Call actual method and Expected output from method
        String result = MaskingUtil.maskEmail(email);

        // Assert
        assertEquals(expectedOutput, result);
    }

}//end class
