package com.nlmk.LAL.MobileGuns.error;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }
}