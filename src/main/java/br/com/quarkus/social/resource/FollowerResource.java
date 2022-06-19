package br.com.quarkus.social.resource;

import br.com.quarkus.social.domain.model.Follower;
import br.com.quarkus.social.domain.model.User;
import br.com.quarkus.social.domain.repository.FollowerRepository;
import br.com.quarkus.social.domain.repository.UserRepository;
import br.com.quarkus.social.resource.dto.FollowerRequest;
import br.com.quarkus.social.resource.dto.FollowerResponse;
import br.com.quarkus.social.resource.dto.FollowersPerUserResponse;
import br.com.quarkus.social.resource.dto.ResponseError;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Path("users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class FollowerResource {

    private final UserRepository userRepository;
    private final FollowerRepository repository;

    private final Validator validator;

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request) {
        Set<ConstraintViolation<FollowerRequest>> violations = validator.validate(request);

        if (violations.isEmpty()) {
            if (userId.equals(request.getFollowerId())) {
                return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself.").build();
            }

            var optionalUser = getUser(userId);
            var optionalFollower = getUser(request.getFollowerId());

            if (optionalUser.isPresent() && optionalFollower.isPresent()) {
                var follows = repository.follows(optionalFollower.get(), optionalUser.get());

                if (!follows) {
                    var follower = new Follower();
                    follower.setUser(optionalUser.get());
                    follower.setFollowerId(optionalFollower.get());

                    repository.persist(follower);
                }

                return Response.noContent().build();
            }

            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {
        if (getUser(userId).isPresent()) {
            var list = repository.findByUser(userId);

            var followers = list.stream()
                    .map(FollowerResponse::new)
                    .collect(Collectors.toList());

            var response = new FollowersPerUserResponse();
            response.setFollowersCount(list.size());
            response.setContent(followers);

            return Response.ok(response).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private Optional<User> getUser(final Long id) {
        return userRepository.findByIdOptional(id);
    }
}
