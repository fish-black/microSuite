package com.fishblack.micro.suite.application;

import com.google.inject.Injector;
import com.squarespace.jersey2.guice.BootstrapUtils;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;

public class HK2Linker {
	@Inject
	public HK2Linker(Injector injector, ServiceLocator locator) {
		BootstrapUtils.link(locator, injector);
		BootstrapUtils.install(locator);
	}
}
