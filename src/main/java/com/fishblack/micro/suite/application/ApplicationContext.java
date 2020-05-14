package com.fishblack.micro.suite.application;

import com.google.inject.Injector;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class ApplicationContext {
	static DBI jdbi;
	static Injector injector;

	public ApplicationContext(){

	}

	public static DBI getJdbi(Environment environment, DataSourceFactory dataSourceFactory){
		Class dbiClass = DBI.class;
		synchronized (DBI.class){
			if (jdbi == null){
				DBIFactory factory = new DBIFactory();
				jdbi = factory.build(environment, dataSourceFactory, "postgresql");
			}
			return jdbi;
		}

	}

	public static synchronized Injector getInjector(){
		return injector;
	}

	public static synchronized void setInjector(Injector injector){
		ApplicationContext.injector = injector;
	}
}
