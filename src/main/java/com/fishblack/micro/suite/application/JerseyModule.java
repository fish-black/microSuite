package com.fishblack.micro.suite.application;

import com.google.inject.servlet.ServletModule;
import com.squarespace.jersey2.guice.BootstrapModule;
import com.squarespace.jersey2.guice.BootstrapUtils;
import org.glassfish.hk2.api.ServiceLocator;

import java.lang.reflect.Proxy;

public class JerseyModule extends ServletModule {
	public JerseyModule() {
	}

	protected void configureServlets() {
		ServiceLocator delegate = BootstrapUtils.newServiceLocator();
		ServiceLocator locator = (ServiceLocator) Proxy.newProxyInstance(ServiceLocator.class.getClassLoader(), new Class[]{ServiceLocator.class}, (proxy, method, args) -> {
			return "shutdown".equals(method.getName()) ? null : method.invoke(delegate, args);
		});
		this.install(new BootstrapModule(locator));
		this.bind(HK2Linker.class).asEagerSingleton();
	}
}