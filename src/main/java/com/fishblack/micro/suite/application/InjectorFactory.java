package com.fishblack.micro.suite.application;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import java.util.List;

public interface InjectorFactory {
	Injector create(Stage var1, List<Module> var2);
}
