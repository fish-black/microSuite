package com.fishblack.micro.suite.service;

import com.fishblack.micro.suite.dao.ServiceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseService {
	private Logger logger = LoggerFactory.getLogger(BaseService.class);

	private final ServiceDao dao;

	public BaseService(ServiceDao dao) {
		this.dao = dao;
	}
}
