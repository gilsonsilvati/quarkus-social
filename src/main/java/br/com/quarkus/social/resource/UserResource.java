package br.com.quarkus.social.resource;

import br.com.quarkus.social.domain.model.User;
import br.com.quarkus.social.domain.repository.UserRepository;
import br.com.quarkus.social.resource.dto.CreateUserRequest;
import br.com.quarkus.social.resource.dto.ResponseError;
import br.com.quarkus.social.resource.mapper.UserMapper;
import io.quarkus.panache.common.Sort;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserResource {

    private final UserRepository repository;

    private final Validator validator;

    private final UserMapper mapper;

    @POST
    @Transactional
    public Response createUser(CreateUserRequest request) {
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        if (violations.isEmpty()) {
            var user = mapper.toResource(request);

            repository.persist(user);

            return Response.status(Response.Status.CREATED).entity(user).build();
        }

        return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
    }

    @GET
    public Response listAllUsers() {
        var query = repository.findAll(Sort.ascending("name"));

        return Response.ok(query.list()).build();
    }

    @GET
    @Path("{id}")
    public Response findUserById(@PathParam("id") Long id) {
        var optionalUser = getUser(id);

        if (optionalUser.isPresent()) {
            return Response.ok(optionalUser.get()).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        var optionalUser = getUser(id);

        if (optionalUser.isPresent()) {
            repository.delete(optionalUser.get());

            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest request) {
        var optionalUser = getUser(id);

        if (optionalUser.isPresent()) {
            optionalUser.get().setName(request.getName());
            optionalUser.get().setAge(request.getAge());

            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private Optional<User> getUser(final Long id) {
        return repository.findByIdOptional(id);
    }
}
