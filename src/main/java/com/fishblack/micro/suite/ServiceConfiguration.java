package com.fishblack.micro.suite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fishblack.micro.suite.rabbitmq.RabbitMQBundleConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.client.proxy.AuthConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ServiceConfiguration extends Configuration {

	@Valid
	@NotNull
	@JsonProperty
	private FlywayFactory flywayFactory;

	@Valid
	@NotNull
	@JsonProperty
	private DataSourceFactory dataSourceFactory = new DataSourceFactory();

	@JsonProperty("swagger")
	public SwaggerBundleConfiguration swaggerBundleConfiguration;

	@JsonProperty("rabbitmq")
	public RabbitMQBundleConfiguration rabbitMQBundleConfiguration;

	public RabbitMQBundleConfiguration getRabbitMQBundleConfiguration() {
		rabbitMQBundleConfiguration.setAddresses(rabbitMQBundleConfiguration.getHost());
		return rabbitMQBundleConfiguration;
	}

	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory() {
		return dataSourceFactory;
	}

	@JsonProperty("database")
	public void setDatabase(DataSourceFactory dataSourceFactory) {
		this.dataSourceFactory = dataSourceFactory;
	}

	@JsonProperty("flyway")
	public FlywayFactory getFlywayFactory() {
		return flywayFactory;
	}

	@JsonProperty("flyway")
	public void setFlywayFactory(FlywayFactory flywayFactory) {
		this.flywayFactory = flywayFactory;
	}

	@JsonProperty
	private AuthConfiguration auth = new AuthConfiguration();


}
