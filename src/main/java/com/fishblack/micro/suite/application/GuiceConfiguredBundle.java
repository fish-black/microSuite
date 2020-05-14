package com.fishblack.micro.suite.application;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.Collections;
import java.util.List;

public interface GuiceConfiguredBundle<T> extends ConfiguredBundle<T> {
	List<Package> getPackagesToScan();

	Module getModule();

	default List<String> getMigrationLocations() {
		return (List)(this.getMigrationLocation() == null ? Collections.emptyList() : Lists.newArrayList(new String[]{this.getMigrationLocation()}));
	}

	default String getMigrationLocation() {
		return null;
	}

	default String getName() {
		return this.getClass().getSimpleName();
	}

	default void run(T configuration, Environment environment) throws Exception {
	}

	default void initialize(Bootstrap<?> bootstrap) {
	}

	default void afterInjectorCreated(T configuration, Environment environment, Injector injector) {
	}
}