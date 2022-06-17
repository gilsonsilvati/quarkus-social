package br.com.quarkus.social.resource;

import br.com.quarkus.social.domain.model.User;
import br.com.quarkus.social.domain.repository.UserRepository;
import br.com.quarkus.social.resource.dto.CreateUserRequest;
import br.com.quarkus.social.resource.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
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
public class UserResource {

    private final UserRepository repository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest createUserRequest) {
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(createUserRequest);

        if (violations.isEmpty()) {
            // TODO: ADD MapStruct...
            var user = new User();
            user.setName(createUserRequest.getName());
            user.setAge(createUserRequest.getAge());

            repository.persist(user);

            return Response.ok(user).build();
        }

        var responseError = ResponseError.createFromValidation(violations);

        return Response.status(Response.Status.BAD_REQUEST).entity(responseError).build();
    }

    @GET
    public Response listAllUsers() {
        PanacheQuery<User> query = repository.findAll(Sort.ascending("name"));

        return Response.ok(query.list()).build();
    }

    @GET
    @Path("{id}")
    public Response findUserById(@PathParam("id") Long id) {
        Optional<User> optionalUser = getUser(id);

        if (optionalUser.isPresent()) {
            return Response.ok(optionalUser.get()).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        Optional<User> optionalUser = getUser(id);

        if (optionalUser.isPresent()) {
            repository.delete(optionalUser.get());

            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest createUserRequest) {
        Optional<User> optionalUser = getUser(id);

        if (optionalUser.isPresent()) {
            optionalUser.get().setName(createUserRequest.getName());
            optionalUser.get().setAge(createUserRequest.getAge());

            return Response.ok(optionalUser.get()).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private Optional<User> getUser(final Long id) {
        return repository.findByIdOptional(Long.valueOf(id));
    }
}
