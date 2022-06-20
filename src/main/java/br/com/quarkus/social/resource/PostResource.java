package br.com.quarkus.social.resource;

import br.com.quarkus.social.domain.model.Post;
import br.com.quarkus.social.domain.model.User;
import br.com.quarkus.social.domain.repository.FollowerRepository;
import br.com.quarkus.social.domain.repository.PostRepository;
import br.com.quarkus.social.domain.repository.UserRepository;
import br.com.quarkus.social.resource.dto.CreatePostRequest;
import br.com.quarkus.social.resource.dto.ResponseError;
import br.com.quarkus.social.resource.mapper.PostMapper;
import io.quarkus.panache.common.Sort;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Path("users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class PostResource {

    private final UserRepository userRepository;
    private final PostRepository repository;
    private final FollowerRepository followerRepository;

    private final Validator validator;

    private final PostMapper mapper;

    @POST
    @Transactional
    public Response createPost(@PathParam("userId") Long userId, CreatePostRequest request) {
        Set<ConstraintViolation<CreatePostRequest>> violations = validator.validate(request);

        if (violations.isEmpty()) {
            var optionalUser = getUser(userId);

            if (optionalUser.isPresent()) {
                var post = new Post();
                post.setText(request.getText());
                post.setUser(optionalUser.get());

                repository.persist(post);

                return Response.status(Response.Status.CREATED).entity(post).build();
            }

            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
    }

    @GET
    public Response listPost(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId) {
        if (Objects.isNull(followerId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId.").build();
        }

        var optionalUser = getUser(userId);

        if (optionalUser.isPresent()) {
            var follower = getUser(followerId);

            if (follower.isPresent()) {
                var follows = followerRepository.follows(follower.get(), optionalUser.get());

                if (follows) {
                    var query = repository.find("user", Sort.descending("date"), optionalUser.get());

                    if (query.stream().findAny().isPresent()) {
                        var postResponses = mapper.toResources(query.list());

                        return Response.ok(postResponses).build();
                    }

                    return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts.").build();
            }

            return Response.status(Response.Status.BAD_REQUEST).entity("Nonexistent follower.").build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private Optional<User> getUser(final Long id) {
        return userRepository.findByIdOptional(id);
    }
}
