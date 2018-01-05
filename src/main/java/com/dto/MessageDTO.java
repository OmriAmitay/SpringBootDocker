package com.dto;

import java.io.Serializable;

public class MessageDTO implements Serializable {

	private static final long serialVersionUID = -291248065103332051L;

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageDTO [message=" + message + "]";
	}
	
}
