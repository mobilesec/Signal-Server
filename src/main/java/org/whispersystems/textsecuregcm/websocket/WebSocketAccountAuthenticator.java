package org.whispersystems.textsecuregcm.websocket;

import java.util.List;
import java.util.Map;

import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.auth.AccountAuthenticator;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.websocket.auth.AuthenticationException;
import org.whispersystems.websocket.auth.WebSocketAuthenticator;

import com.google.common.base.Optional;

import io.dropwizard.auth.basic.BasicCredentials;

public class WebSocketAccountAuthenticator implements WebSocketAuthenticator<Account> {

	private final AccountAuthenticator accountAuthenticator;

	private final Logger logger = LoggerFactory.getLogger(WebSocketAccountAuthenticator.class);

	public WebSocketAccountAuthenticator(AccountAuthenticator accountAuthenticator) {
		this.accountAuthenticator = accountAuthenticator;
	}

	@Override
	public Optional<Account> authenticate(UpgradeRequest request) throws AuthenticationException {
		try {
			Map<String, List<String>> parameters = request.getParameterMap();
			List<String> usernames = parameters.get("login");
			List<String> passwords = parameters.get("password");

			if (usernames == null || usernames.size() == 0 || passwords == null || passwords.size() == 0) {
				return Optional.absent();
			}

			BasicCredentials credentials = new BasicCredentials(usernames.get(0).replace(" ", "+"),
					passwords.get(0).replace(" ", "+"));

			Optional<Account> authenticate = accountAuthenticator.authenticate(credentials);
			logger.info("authenticate: " + authenticate.isPresent());
			if(authenticate.isPresent() ) logger.info("authenticate: " + authenticate.get().getName());
			
			return authenticate;
		} catch (io.dropwizard.auth.AuthenticationException e) {
			logger.info("authenticate: " + e.getMessage());
			throw new AuthenticationException(e);
		}
	}

}
