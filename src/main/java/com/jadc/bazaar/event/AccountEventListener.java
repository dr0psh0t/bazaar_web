package com.jadc.bazaar.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.jadc.service.AccountDocumentDBService;

@Component
public class AccountEventListener implements ApplicationListener<AccountEvent> {
	private final AccountDocumentDBService accountDocumentDBService;
	private static final Logger logger = LoggerFactory.getLogger(AccountEventListener.class);

	@Autowired
	public AccountEventListener(AccountDocumentDBService service) {
		this.accountDocumentDBService = service;
	}


	@Override
	public void onApplicationEvent(AccountEvent event) {
		logger.info("AccountEventListener: Received event" + event.getClass().getName());

		int id = Integer.parseInt(event.getMessage());

		if (id != 0) {
			accountDocumentDBService.createDatabase(id);
			logger.info("MongoDB & Credential Created - AccountEventListener");
		}
	}
}
