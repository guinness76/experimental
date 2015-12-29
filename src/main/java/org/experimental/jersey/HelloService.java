package org.experimental.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Created by mattg on 12/29/15.
 */
@Path("/hello")
public class HelloService {

    @GET
    @Path("/{param}")
    public Response getMsg(@PathParam("param") String msg) {

        String output = "Jersey says : " + msg;

        return Response.status(200).entity(output).build();

    }
}
