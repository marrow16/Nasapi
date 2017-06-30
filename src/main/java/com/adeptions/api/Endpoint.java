package com.adeptions.api;

import com.adeptions.exceptions.*;
import com.adeptions.mappings.Mapping;
import com.adeptions.mappings.Mappings;
import com.adeptions.annotations.*;
import com.adeptions.wrappers.EndpointRequest;
import com.adeptions.wrappers.EndpointResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;

@Path("/{path: [a-zA-Z0-9\\-\\./\\_]+}")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class Endpoint {
	@Autowired
	Mappings mappings;

	@OPTIONS
	public Response options(@PathParam("path") String path,
							@Context UriInfo uriInfo,
							@Context HttpServletRequest httpRequest,
							@Context HttpServletResponse httpResponse) throws NasapiException {
		return doMethod("OPTIONS", path, uriInfo, httpRequest, httpResponse, null);
	}

	@GET
	public Response get(@PathParam("path") String path,
						@Context UriInfo uriInfo,
						@Context HttpServletRequest httpRequest,
						@Context HttpServletResponse httpResponse) throws NasapiException {
		return doMethod("GET", path, uriInfo, httpRequest, httpResponse, null);
	}

	@DELETE
	public Response delete(@PathParam("path") String path,
						   @Context UriInfo uriInfo,
						   @Context HttpServletRequest httpRequest,
						   @Context HttpServletResponse httpResponse,
						   InputStream requestBodyStream) throws NasapiException {
		return doMethod("DELETE", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@PUT
	public Response put(@PathParam("path") String path,
						@Context UriInfo uriInfo,
						@Context HttpServletRequest httpRequest,
						@Context HttpServletResponse httpResponse,
						InputStream requestBodyStream) throws NasapiException {
		return doMethod("PUT", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@POST
	public Response post(@PathParam("path") String path,
						 @Context UriInfo uriInfo,
						 @Context HttpServletRequest httpRequest,
						 @Context HttpServletResponse httpResponse,
						 InputStream requestBodyStream) throws NasapiException {
		return doMethod("POST", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@PATCH
	public Response patch(@PathParam("path") String path,
						  @Context UriInfo uriInfo,
						  @Context HttpServletRequest httpRequest,
						  @Context HttpServletResponse httpResponse,
						  InputStream requestBodyStream) throws NasapiException {
		return doMethod("PATCH", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@COPY
	public Response copy(@PathParam("path") String path,
						 @Context UriInfo uriInfo,
						 @Context HttpServletRequest httpRequest,
						 @Context HttpServletResponse httpResponse,
						 InputStream requestBodyStream) throws NasapiException {
		return doMethod("COPY", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@LINK
	public Response link(@PathParam("path") String path,
						 @Context UriInfo uriInfo,
						 @Context HttpServletRequest httpRequest,
						 @Context HttpServletResponse httpResponse,
						 InputStream requestBodyStream) throws NasapiException {
		return doMethod("LINK", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@UNLINK
	public Response unlink(@PathParam("path") String path,
						   @Context UriInfo uriInfo,
						   @Context HttpServletRequest httpRequest,
						   @Context HttpServletResponse httpResponse,
						   InputStream requestBodyStream) throws NasapiException {
		return doMethod("UNLINK", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@PURGE
	public Response purge(@PathParam("path") String path,
						  @Context UriInfo uriInfo,
						  @Context HttpServletRequest httpRequest,
						  @Context HttpServletResponse httpResponse,
						  InputStream requestBodyStream) throws NasapiException {
		return doMethod("PURGE", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@LOCK
	public Response lock(@PathParam("path") String path,
						 @Context UriInfo uriInfo,
						 @Context HttpServletRequest httpRequest,
						 @Context HttpServletResponse httpResponse,
						 InputStream requestBodyStream) throws NasapiException {
		return doMethod("LOCK", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@UNLOCK
	public Response unlock(@PathParam("path") String path,
						   @Context UriInfo uriInfo,
						   @Context HttpServletRequest httpRequest,
						   @Context HttpServletResponse httpResponse,
						   InputStream requestBodyStream) throws NasapiException {
		return doMethod("UNLOCK", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@PROPFIND
	public Response propfind(@PathParam("path") String path,
							 @Context UriInfo uriInfo,
							 @Context HttpServletRequest httpRequest,
							 @Context HttpServletResponse httpResponse,
							 InputStream requestBodyStream) throws NasapiException {
		return doMethod("PROPFIND", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	@VIEW
	public Response view(@PathParam("path") String path,
						 @Context UriInfo uriInfo,
						 @Context HttpServletRequest httpRequest,
						 @Context HttpServletResponse httpResponse,
						 InputStream requestBodyStream) throws NasapiException {
		return doMethod("VIEW", path, uriInfo, httpRequest, httpResponse, requestBodyStream);
	}

	private Response doMethod(String httpMethod,
							  String path,
							  UriInfo uriInfo,
							  HttpServletRequest httpRequest,
							  HttpServletResponse httpResponse,
							  InputStream requestBodyStream) throws NasapiException {
		MultivaluedHashMap<String,String> pathParameters = new MultivaluedHashMap<String,String>();
		Mapping mapping = mappings.find(path, pathParameters);
		if (mapping == null) {
			throw new com.adeptions.exceptions.NotFoundException("No such URI '" + path + "'");
		}
		EndpointRequest request = new EndpointRequest(uriInfo, httpRequest, pathParameters, requestBodyStream);
		EndpointResponse response = new EndpointResponse(httpResponse);
		Object methodResult = mapping.doMethod(httpMethod, path, request, response);
		Response result = null;
		if (methodResult != null) {
			if (methodResult instanceof Response) {
				result = (Response)methodResult;
			} else if (methodResult instanceof EndpointResponse) {
				result = ((EndpointResponse)methodResult).toResponse();
			} else {
				result = response.toResponse(methodResult);
			}
		} else {
			result = response.toResponse();
		}
		return result;
	}

}
