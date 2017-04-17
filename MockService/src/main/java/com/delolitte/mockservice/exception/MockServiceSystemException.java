package com.delolitte.mockservice.exception;

public class MockServiceSystemException extends Exception {	 
	private static final long serialVersionUID = 3670096129928791764L;

	public MockServiceSystemException(String message)
	      {
	         super(message);
	      }
	
	public MockServiceSystemException(Exception e) {
		super(e);
	}
}
