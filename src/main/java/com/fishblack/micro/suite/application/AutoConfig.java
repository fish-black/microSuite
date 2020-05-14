package com.fishblack.micro.suite.application;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.ProvidedBy;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.model.Resource;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;

public class AutoConfig {
	private static final Logger logger = LoggerFactory.getLogger(AutoConfig.class);
	private final Reflections reflections;

	public AutoConfig(String... basePackages) {
		Preconditions.checkArgument(basePackages.length > 0);
		ConfigurationBuilder cfgBldr = new ConfigurationBuilder();
		FilterBuilder filterBuilder = new FilterBuilder();
		String[] var4 = basePackages;
		int var5 = basePackages.length;

		for(int var6 = 0; var6 < var5; ++var6) {
			String basePkg = var4[var6];
			cfgBldr.addUrls(ClasspathHelper.forPackage(basePkg, new ClassLoader[0]));
			filterBuilder.include(FilterBuilder.prefix(basePkg));
		}

		cfgBldr.filterInputsBy(filterBuilder).setScanners(new Scanner[]{new SubTypesScanner(), new TypeAnnotationsScanner()});
		this.reflections = new Reflections(cfgBldr);
	}

	public void run(Environment environment, Injector injector) {
		this.addHealthChecks(environment, injector);
		this.addProviders(environment);
		this.addResources(environment);
		this.addTasks(environment, injector);
		this.addManaged(environment, injector);
		this.addParamConverterProviders(environment);
	}

	private void addManaged(Environment environment, Injector injector) {
		Set<Class<? extends Managed>> managedClasses = this.reflections.getSubTypesOf(Managed.class);
		Iterator var4 = managedClasses.iterator();

		while(var4.hasNext()) {
			Class<? extends Managed> managed = (Class)var4.next();
			Optional<? extends Managed> maybeManaged = this.getFromGuiceIfPossible(injector, managed);
			if (maybeManaged.isPresent()) {
				environment.lifecycle().manage((Managed)maybeManaged.get());
				logger.info("Added managed: {}", managed);
			}
		}

	}

	private void addTasks(Environment environment, Injector injector) {
		Set<Class<? extends Task>> taskClasses = this.reflections.getSubTypesOf(Task.class);
		Iterator var4 = taskClasses.iterator();

		while(var4.hasNext()) {
			Class<? extends Task> task = (Class)var4.next();
			Optional<? extends Task> maybeTask = this.getFromGuiceIfPossible(injector, task);
			if (maybeTask.isPresent()) {
				environment.admin().addTask((Task)maybeTask.get());
				logger.info("Added task: {}", task);
			}
		}

	}

	private void addHealthChecks(Environment environment, Injector injector) {
		Set<Class<? extends InjectableHealthCheck>> healthCheckClasses = this.reflections.getSubTypesOf(InjectableHealthCheck.class);
		Iterator var4 = healthCheckClasses.iterator();

		while(var4.hasNext()) {
			Class<? extends InjectableHealthCheck> healthCheck = (Class)var4.next();
			Optional<? extends InjectableHealthCheck> maybeHealthCheck = this.getFromGuiceIfPossible(injector, healthCheck);
			if (maybeHealthCheck.isPresent()) {
				environment.healthChecks().register(((InjectableHealthCheck)maybeHealthCheck.get()).getName(), (HealthCheck)maybeHealthCheck.get());
				logger.info("Added injectableHealthCheck: {}", healthCheck);
			}
		}

	}

	private void addProviders(Environment environment) {
		Set<Class<?>> providerClasses = this.reflections.getTypesAnnotatedWith(Provider.class);
		Iterator var3 = providerClasses.iterator();

		while(var3.hasNext()) {
			Class<?> provider = (Class)var3.next();
			environment.jersey().register(provider);
			logger.info("Added provider class: {}", provider);
		}

	}

	private void addResources(Environment environment) {
		Set<Class<?>> resourceClasses = this.reflections.getTypesAnnotatedWith(Path.class);
		Iterator var3 = resourceClasses.iterator();

		while(var3.hasNext()) {
			Class<?> resource = (Class)var3.next();
			if (Resource.isAcceptable(resource)) {
				environment.jersey().register(resource);
				logger.info("Added resource class: {}", resource);
			}
		}

	}

	private void addParamConverterProviders(Environment environment) {
		Set<Class<? extends ParamConverterProvider>> providerClasses = this.reflections.getSubTypesOf(ParamConverterProvider.class);
		Iterator var3 = providerClasses.iterator();

		while(var3.hasNext()) {
			Class<?> provider = (Class)var3.next();
			environment.jersey().register(provider);
			logger.info("Added ParamConverterProvider class: {}", provider);
		}

	}

	private <T> Optional<T> getFromGuiceIfPossible(Injector injector, Class<T> type) {
		if (!concreteClass(type) && !hasBinding(injector, type)) {
			logger.info("Not attempting to retrieve abstract class {} from injector", type);
			return Optional.absent();
		} else {
			return Optional.of(injector.getInstance(type));
		}
	}

	private static boolean concreteClass(Class<?> type) {
		return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
	}

	private static boolean hasBinding(Injector injector, Class<?> type) {
		return injector.getExistingBinding(Key.get(type)) != null || hasBindingAnnotation(type);
	}

	private static boolean hasBindingAnnotation(Class<?> type) {
		return type.isAnnotationPresent(ImplementedBy.class) || type.isAnnotationPresent(ProvidedBy.class);
	}
}

