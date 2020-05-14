package com.fishblack.micro.suite.api;

import com.codahale.metrics.annotation.Timed;
import com.fishblack.micro.suite.dao.ServiceDao;
import com.fishblack.micro.suite.model.vo.ServiceVo;
import com.fishblack.micro.suite.service.BaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.fishblack.micro.suite.api.ContextRoot.CONTEXT_SERVICE;

@Path(CONTEXT_SERVICE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "ServiceApi", description = "REST API for service catalog items")
public class ServiceApi {
	private Logger logger = LoggerFactory.getLogger(ServiceApi.class);
	private final BaseService service;

	public ServiceApi(BaseService service) {
		this.service = service;
	}

	/**
	 * Get facet for support catalog items
	 *
	 * @param request
	 */
	@ApiOperation(value = "Retrieve facet of support catalog items",
			notes = "Retrieve facet of support catalog items",
			response = ServiceVo.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved.")})
	@Timed
	@Produces("application/hal+json")
	@GET
	@Path("/facet")
	public Response getResponse(@Context HttpServletRequest request) {
		ServiceVo vo = new ServiceVo();
		vo.setCode(0);
		vo.setMessage("Hello world!");
		return Response.ok(vo).build();

	}
}
