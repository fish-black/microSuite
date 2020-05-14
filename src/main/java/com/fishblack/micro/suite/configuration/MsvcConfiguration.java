package com.fishblack.micro.suite.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(
		ignoreUnknown = true
)
public class MsvcConfiguration extends Configuration {
	@JsonProperty
	@NotEmpty
	private String hostname;

	@JsonProperty
	private int queryPort;

	@JsonProperty
	private String protocol;

	@JsonProperty
	private String authUser;

	@JsonProperty
	private String authPassword;

	@NotNull
	@JsonProperty
	private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

	@JsonProperty
	String trustStore;

	@JsonProperty
	String trustStorePassword;

	@JsonProperty
	Boolean strictSSL = false;

	public String getTrustStore() {
		return trustStore;
	}

	public void setTrustStore(String trustStore) {
		this.trustStore = trustStore;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getAuthUser() {
		return authUser;
	}

	public void setAuthUser(String authUser) {
		this.authUser = authUser;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	public Boolean getStrictSSL() {
		return strictSSL;
	}

	public void setStrictSSL(Boolean strictSSL) {
		this.strictSSL = strictSSL;
	}

	public JerseyClientConfiguration getHttpClientConfiguration() {
		return httpClient;
	}

	public String getHostname() {
		return hostname;
	}

	public int getQueryPort() {
		return queryPort;
	}
}
