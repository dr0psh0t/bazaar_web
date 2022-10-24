package com.jadc.bazaar.event;

import org.springframework.context.ApplicationEvent;

public class AccountEvent extends ApplicationEvent {
	private String message;

	public AccountEvent(Object source,String message) {
		super(source);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
