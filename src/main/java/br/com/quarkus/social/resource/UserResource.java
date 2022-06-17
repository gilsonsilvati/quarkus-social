package br.com.quarkus.social.resource;

import br.com.quarkus.social.resource.dto.CreateUserRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @POST
    public Response createUser(CreateUserRequest createUserRequest) {
        return Response.ok(createUserRequest).build();
    }

    @GET
    public Response listAllUsers() {
        return Response.ok().build();
    }
}
