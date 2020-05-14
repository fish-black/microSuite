package com.fishblack.micro.suite.application;

import com.codahale.metrics.health.HealthCheck;

public abstract class InjectableHealthCheck extends HealthCheck {
	public InjectableHealthCheck() {
	}

	public abstract String getName();
}
