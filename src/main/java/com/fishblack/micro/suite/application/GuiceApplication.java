package com.fishblack.micro.suite.application;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public abstract class GuiceApplication<T extends Configuration> extends Application<T> {
	private List<GuiceConfiguredBundle<T>> bundles = new ArrayList();
	protected GuiceBundle<T> guiceBundle;
	private Class configClass;
	private Injector injector;

	public GuiceApplication(Class<T> configClass) {
		this.configClass = configClass;
	}

	public abstract void registerGuiceBundles();

	public void registerBundle(GuiceConfiguredBundle bundle) {
		this.bundles.add(bundle);
	}

	public Injector getInjector() {
		return this.injector;
	}

	public void initialize(Bootstrap<T> bootstrap) {
		this.registerGuiceBundles();
		if (this.bundles != null) {
			Iterator var2 = this.bundles.iterator();

			while (var2.hasNext()) {
				GuiceConfiguredBundle bundle = (GuiceConfiguredBundle) var2.next();
				bootstrap.addBundle(bundle);
			}
		}

		GuiceBundle.Builder builder = GuiceBundle.newBuilder().addModule(this.collectModules()).setConfigClass(this.configClass);
		String[] packages = this.collectScanPackages();
		if (packages.length > 0) {
			builder.enableAutoConfig(packages);
		}

		this.guiceBundle = builder.build();
		bootstrap.addBundle(this.guiceBundle);
		FlywayBundle flywayBundle = this.constructFlywayBundle();
		if (flywayBundle != null) {
			bootstrap.addBundle(flywayBundle);
		}

	}

	protected abstract FlywayBundle constructFlywayBundle();

	protected Module getModule() {
		return Modules.EMPTY_MODULE;
	}

	protected Module collectModules() {
		List<Module> modules = this.bundles.stream().map(GuiceConfiguredBundle::getModule).filter(Objects::nonNull).collect(Collectors.toList());
		modules.add(this.getModule());
		return Modules.combine(modules);
	}

	protected String[] collectScanPackages() {
		return (String[]) this.bundles.stream().map(GuiceConfiguredBundle::getPackagesToScan).filter((l) -> {
			return l != null;
		}).flatMap(Collection::stream).map(Package::getName).toArray((x$0) -> {
			return new String[x$0];
		});
	}

	public final void run(T configuration, Environment environment) throws Exception {
		this.injector = this.guiceBundle.getInjector();
		Iterator var3 = this.bundles.iterator();

		while (var3.hasNext()) {
			GuiceConfiguredBundle<T> bundle = (GuiceConfiguredBundle) var3.next();
			bundle.afterInjectorCreated(configuration, environment, this.injector);
		}

		this.run(configuration, environment, this.injector);
	}

	public abstract void run(T var1, Environment var2, Injector var3) throws Exception;

	public Map collectMigrationLocations() {
		return this.bundles.stream().map((b) -> new ImmutablePair(b.getName(), b.getMigrationLocations())).filter((m) -> m.getRight() != null && !((List) m.getRight()).isEmpty()).collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (a, b) -> {
			return a;
		}, LinkedHashMap::new));
	}
}
