package com.fishblack.micro.suite;

import com.fishblack.micro.suite.application.ApplicationContext;
import com.fishblack.micro.suite.application.GuiceApplication;
import com.fishblack.micro.suite.rabbitmq.RabbitMQBundle;
import com.fishblack.micro.suite.rabbitmq.RabbitMQBundleConfiguration;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class ServiceApplication extends GuiceApplication<ServiceConfiguration> {

	public ServiceApplication() {
		super(ServiceConfiguration.class);
	}

	/**
	 * Main. Show starts here !!!
	 */
	public static void main(String[] args) throws Exception {
		new ServiceApplication().run(args);
	}

	@Override
	public String getName() {
		return "MicroSuite Service";
	}


	@Override
	public void registerGuiceBundles() {

	}

	@Override
	public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
		super.initialize(bootstrap);
		// Enable variable substitution with environment variables
		ConfigurationSourceProvider configurationSourceProvider = bootstrap.getConfigurationSourceProvider();
		configurationSourceProvider = new SubstitutingSourceProvider(configurationSourceProvider,
				new EnvironmentVariableSubstitutor(false));
		bootstrap.setConfigurationSourceProvider(configurationSourceProvider);

		// Swagger
		bootstrap.addBundle(new SwaggerBundle<ServiceConfiguration>() {
			@Override
			public SwaggerBundleConfiguration getSwaggerBundleConfiguration(ServiceConfiguration configuration) {
				return configuration.swaggerBundleConfiguration;
			}
		});

		// RabbitMQ
		bootstrap.addBundle(new RabbitMQBundle<ServiceConfiguration>() {
			@Override
			public RabbitMQBundleConfiguration getRabbitMQBundleConfiguration(ServiceConfiguration configuration) {
				return configuration.rabbitMQBundleConfiguration;
			}
		});

		bootstrap.addBundle(new ServiceBundle());
	}

	@Override
	protected FlywayBundle constructFlywayBundle() {
		return new FlywayBundle<ServiceConfiguration>() {
			@Override
			public DataSourceFactory getDataSourceFactory(ServiceConfiguration configuration) {
				return configuration.getDataSourceFactory();
			}
		};
	}

	@Override
	public void run(ServiceConfiguration configuration, Environment environment, Injector injector) throws Exception {
		ApplicationContext.setInjector(injector);
	}

	@Override
	public Module getModule() {
		return new ServiceModule();
	}
}
