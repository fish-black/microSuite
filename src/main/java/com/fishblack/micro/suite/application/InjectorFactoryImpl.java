package com.fishblack.micro.suite.application;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import java.util.List;

public class InjectorFactoryImpl implements InjectorFactory{
	public InjectorFactoryImpl() {
	}

	public Injector create(Stage stage, List<Module> modules) {
		return Guice.createInjector(stage, modules);
	}
}
