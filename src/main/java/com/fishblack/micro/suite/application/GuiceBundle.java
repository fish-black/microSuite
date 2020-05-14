package com.fishblack.micro.suite.application;

import com.fishblack.micro.suite.annotations.SuppressFBWarnings;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceFilter;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.List;

public class GuiceBundle<T extends Configuration> implements ConfiguredBundle<T> {
	final Logger logger;
	private final AutoConfig autoConfig;
	private final List<Module> modules;
	private final InjectorFactory injectorFactory;
	private Injector injector;
	private DropwizardEnvironmentModule dropwizardEnvironmentModule;
	private Optional<Class<T>> configurationClass;
	private Stage stage;

	public static <T extends Configuration> GuiceBundle.Builder<T> newBuilder() {
		return new GuiceBundle.Builder();
	}

	private GuiceBundle(Stage stage, AutoConfig autoConfig, List<Module> modules, Optional<Class<T>> configurationClass, InjectorFactory injectorFactory) {
		this.logger = LoggerFactory.getLogger(GuiceBundle.class);
		Preconditions.checkNotNull(modules);
		Preconditions.checkArgument(!modules.isEmpty());
		Preconditions.checkNotNull(stage);
		this.modules = modules;
		this.autoConfig = autoConfig;
		this.configurationClass = configurationClass;
		this.injectorFactory = injectorFactory;
		this.stage = stage;
	}

	public void initialize(Bootstrap<?> bootstrap) {
		if (this.configurationClass.isPresent()) {
			this.dropwizardEnvironmentModule = new DropwizardEnvironmentModule((Class)this.configurationClass.get());
		} else {
			this.dropwizardEnvironmentModule = new DropwizardEnvironmentModule(Configuration.class);
		}

		this.modules.add(new JerseyModule());
		this.modules.add(this.dropwizardEnvironmentModule);
	}

	@SuppressFBWarnings({"DM_EXIT"})
	private void initInjector() {
		try {
			this.injector = this.injectorFactory.create(this.stage, ImmutableList.copyOf(this.modules));
		} catch (Exception var2) {
			this.logger.error("Exception occurred when creating Guice Injector - exiting", var2);
			System.exit(1);
		}

	}

	public void run(T configuration, Environment environment) {
		environment.servlets().addFilter("Guice Filter", GuiceFilter.class).addMappingForUrlPatterns((EnumSet)null, false, new String[]{environment.getApplicationContext().getContextPath() + "*"});
		this.setEnvironment(configuration, environment);
		this.initInjector();
		if (this.autoConfig != null) {
			this.autoConfig.run(environment, this.injector);
		}

	}

	private void setEnvironment(T configuration, Environment environment) {
		this.dropwizardEnvironmentModule.setEnvironmentData(configuration, environment);
	}

	public Injector getInjector() {
		return this.injector;
	}


	public static class Builder<T extends Configuration> {
		private AutoConfig autoConfig;
		private List<Module> modules = Lists.newArrayList();
		private Optional<Class<T>> configurationClass = Optional.absent();
		private InjectorFactory injectorFactory = new InjectorFactoryImpl();

		public Builder() {
		}

		public GuiceBundle.Builder<T> addModule(Module module) {
			Preconditions.checkNotNull(module);
			this.modules.add(module);
			return this;
		}

		public GuiceBundle.Builder<T> setConfigClass(Class<T> clazz) {
			this.configurationClass = Optional.of(clazz);
			return this;
		}

		public GuiceBundle.Builder<T> setInjectorFactory(InjectorFactory factory) {
			Preconditions.checkNotNull(factory);
			this.injectorFactory = factory;
			return this;
		}

		public GuiceBundle.Builder<T> enableAutoConfig(String... basePackages) {
			Preconditions.checkNotNull(basePackages.length > 0);
			Preconditions.checkArgument(this.autoConfig == null, "autoConfig already enabled!");
			this.autoConfig = new AutoConfig(basePackages);
			return this;
		}

		public GuiceBundle<T> build() {
			return this.build(Stage.PRODUCTION);
		}

		public GuiceBundle<T> build(Stage s) {
			return new GuiceBundle(s, this.autoConfig, this.modules, this.configurationClass, this.injectorFactory);
		}
	}
}