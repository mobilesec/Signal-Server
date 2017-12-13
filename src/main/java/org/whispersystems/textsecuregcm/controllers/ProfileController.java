package org.whispersystems.textsecuregcm.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;
import org.whispersystems.textsecuregcm.entities.Profile;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;

import io.dropwizard.auth.Auth;

@Path("/v1/profile")
public class ProfileController {

	private final RateLimiters rateLimiters;
	private final AccountsManager accountsManager;

	public ProfileController(RateLimiters rateLimiters, AccountsManager accountsManager) {
		this.rateLimiters = rateLimiters;
		this.accountsManager = accountsManager;
	}

	@Timed
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{number}")
	public Profile getProfile(@Auth Account account, @PathParam("number") String number,
			@QueryParam("ca") boolean useCaCertificate) throws RateLimitExceededException {
		rateLimiters.getProfileLimiter().validate(account.getNumber());

		Optional<Account> accountProfile = accountsManager.get(number);

		if (!accountProfile.isPresent()) {
			throw new WebApplicationException(Response.status(404).build());
		}

		return new Profile(accountProfile.get().getName(), accountProfile.get().getAvatar(),
				accountProfile.get().getIdentityKey());
	}

	@Timed
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/name/{name}")
	public void setProfile(@Auth Account account,
			@PathParam("name") @UnwrapValidatedValue(true) @Length(min = 72, max = 72) Optional<String> name) {
		account.setName(name.orNull());
		accountsManager.update(account);
	}
}
