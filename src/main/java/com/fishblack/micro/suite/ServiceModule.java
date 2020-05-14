package com.fishblack.micro.suite;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Providers;
import org.glassfish.jersey.client.spi.ConnectorProvider;

import javax.validation.Validation;
import javax.validation.Validator;

public class ServiceModule extends AbstractModule {
	@Provides
	@Singleton
	public Validator provideValidator(){
		return Validation.byDefaultProvider().configure().buildValidatorFactory().getValidator();
	}

	@Override
	protected void configure() {
		bind(ConnectorProvider.class).toProvider(Providers.of(null));
	}
}
