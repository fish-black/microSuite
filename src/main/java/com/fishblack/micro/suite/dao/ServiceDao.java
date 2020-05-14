package com.fishblack.micro.suite.dao;

import com.fishblack.micro.suite.ServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by huanli on 2015/9/28.
 */
public class ServiceDao extends BaseDao {
    private Logger logger = LoggerFactory.getLogger(ServiceDao.class);

    public ServiceDao(ServiceConfiguration serviceConfiguration) {
        super(serviceConfiguration);
    }
}