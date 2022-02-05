/**
 * 
 */
package com.spring.springjwt.exception;

/**
 * @author PA
 *
 */
public class AuthServiceException  extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2755353323341547716L;

	public AuthServiceException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}