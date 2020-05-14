package com.fishblack.micro.suite.application;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class DropwizardEnvironmentModule<T extends Configuration> extends AbstractModule {
	private static final String ILLEGAL_DROPWIZARD_MODULE_STATE = "The dropwizard environment has not yet been set. This is likely caused by trying to access the dropwizard environment during the bootstrap phase.";
	private T configuration;
	private Environment environment;
	private Class<? super T> configurationClass;

	public DropwizardEnvironmentModule(Class<T> configurationClass) {
		this.configurationClass = configurationClass;
	}

	protected void configure() {
		Provider<T> provider = new DropwizardEnvironmentModule.CustomConfigurationProvider();
		this.bind(this.configurationClass).toProvider(provider);
		if (this.configurationClass != Configuration.class) {
			this.bind(Configuration.class).toProvider(provider);
		}

	}

	public void setEnvironmentData(T configuration, Environment environment) {
		this.configuration = configuration;
		this.environment = environment;
	}

	@Provides
	public Environment providesEnvironment() {
		if (this.environment == null) {
			throw new ProvisionException("The dropwizard environment has not yet been set. This is likely caused by trying to access the dropwizard environment during the bootstrap phase.");
		} else {
			return this.environment;
		}
	}

	private class CustomConfigurationProvider implements Provider<T> {
		private CustomConfigurationProvider() {
		}

		public T get() {
			if (DropwizardEnvironmentModule.this.configuration == null) {
				throw new ProvisionException("The dropwizard environment has not yet been set. This is likely caused by trying to access the dropwizard environment during the bootstrap phase.");
			} else {
				return DropwizardEnvironmentModule.this.configuration;
			}
		}
	}
}
