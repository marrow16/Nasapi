package com.adeptions.configuration;

import com.adeptions.api.Endpoint;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig() {
		// make sure the exception mappers are used...
		packages("com.adeptions.exceptions.mappers");
		// xml body writers...
		packages("com.adeptions.writers");
		// register our main endpoint...
		register(Endpoint.class);
	}
}
