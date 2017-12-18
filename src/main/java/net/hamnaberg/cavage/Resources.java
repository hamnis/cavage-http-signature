package net.hamnaberg.cavage;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/resource")
@Produces("application/json")
public class Resources {

    @GET
    public Response get() {
        return Response.ok("{\"message\": \"Hello World\"}").build();
    }

    @POST
    public Response post(@FormParam("name") String name) {
        return Response.ok(String.format("{\"message\": \"Hello %s\"}", name)).build();
    }

}
