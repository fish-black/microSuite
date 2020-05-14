package com.fishblack.micro.suite;

import com.fishblack.micro.suite.api.ServiceApi;
import com.fishblack.micro.suite.configuration.MsvcConfiguration;
import com.fishblack.micro.suite.dao.ServiceDao;
import com.fishblack.micro.suite.service.BaseService;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.skife.jdbi.v2.DBI;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class ServiceBundle implements ConfiguredBundle<ServiceConfiguration> {
	@Override
	public void run(ServiceConfiguration configuration, Environment environment) throws Exception {
		// Create DB access object and plug into API
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

		ServiceDao serviceDao = new ServiceDao(configuration);
		BaseService baseService = new BaseService(serviceDao);
		environment.jersey().register(new ServiceApi(baseService));
	}

	@Override
	public void initialize(Bootstrap<?> bootstrap) {
		//RAML download api
		bootstrap.addBundle(new AssetsBundle("/raml", "/raml", ""));
	}

	private <T extends MsvcConfiguration> Client getClient(T configuration, Environment environment, String clientType) throws NoSuchAlgorithmException, KeyManagementException {
		// feature to enable basic authentication
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().build();
		/**
		 * if trust store & password is defined (i.e. not supporting trust store without password),
		 * create SSL context using Jersey 2 syntax.  (dropwizard's JerseyBuilder test is using the old way to create SSLContext
		 * pick one of the predefined hostname verifier base on configuration
		 * override the default registry by using a new registry.
		 */
		Registry<ConnectionSocketFactory> socketFactoryRegistry = null;
		if( configuration.getTrustStore() != null && configuration.getTrustStorePassword() != null ) {
			SslConfigurator sslConfig = SslConfigurator.newInstance()
					.trustStoreFile(configuration.getTrustStore())
					.trustStorePassword(configuration.getTrustStorePassword());
			SSLContext sslContext = sslConfig.createSSLContext();
			X509HostnameVerifier verifier = (configuration.getStrictSSL())? SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER:SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory())
					.register("https", new SSLConnectionSocketFactory(sslContext,verifier))
					.build();
		}
		Client client;
		JerseyClientConfiguration httpConfiguration = configuration.getHttpClientConfiguration();
		httpConfiguration.setTimeout(configuration.getHttpClientConfiguration().getTimeout());
		httpConfiguration.setConnectionTimeout(configuration.getHttpClientConfiguration().getConnectionTimeout());
		httpConfiguration.setTimeToLive(configuration.getHttpClientConfiguration().getTimeToLive());
		httpConfiguration.setGzipEnabled(false);
		httpConfiguration.setGzipEnabledForRequests(false);
		JerseyClientBuilder clientBuilder = new JerseyClientBuilder(environment).using(httpConfiguration);
		if( socketFactoryRegistry != null )
			clientBuilder.using(socketFactoryRegistry);
		client = clientBuilder.build(clientType).register(feature);
		return client;
	}
}
