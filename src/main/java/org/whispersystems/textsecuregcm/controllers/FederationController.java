package org.whispersystems.textsecuregcm.controllers;

import org.whispersystems.textsecuregcm.storage.AccountsManager;

public class FederationController {

	protected final AccountsManager accounts;
	protected final MessageController messageController;

	public FederationController(AccountsManager accounts, MessageController messageController) {
		this.accounts = accounts;
		this.messageController = messageController;
	}
}
