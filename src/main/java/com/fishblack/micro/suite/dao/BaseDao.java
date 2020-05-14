package com.fishblack.micro.suite.dao;

import com.fishblack.micro.suite.ServiceConfiguration;

public class BaseDao {

    ServiceConfiguration serviceConfiguration;

    public BaseDao(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }
}