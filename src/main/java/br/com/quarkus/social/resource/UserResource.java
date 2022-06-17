package br.com.quarkus.social.resource;

import br.com.quarkus.social.resource.dto.CreateUserRequest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/users")
public class UserResource {

    @POST
    public Response createUser(CreateUserRequest createUserRequest) {
        return Response.ok().build();
    }
}
