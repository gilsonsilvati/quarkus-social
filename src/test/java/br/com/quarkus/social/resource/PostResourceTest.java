package br.com.quarkus.social.resource;

import br.com.quarkus.social.domain.model.Follower;
import br.com.quarkus.social.domain.model.Post;
import br.com.quarkus.social.domain.model.User;
import br.com.quarkus.social.domain.repository.FollowerRepository;
import br.com.quarkus.social.domain.repository.PostRepository;
import br.com.quarkus.social.domain.repository.UserRepository;
import br.com.quarkus.social.resource.dto.CreatePostRequest;
import br.com.quarkus.social.resource.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository repository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    void setUp() {
        /* Default test user */
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);

        userRepository.persist(user);
        userId = user.getId();

        /* Creating a post */
        var post = new Post();
        post.setUser(user);
        post.setText("Hello guys!!!");

        repository.persist(post);

        /* User who doesn't follow anyone */
        var userNotFollower = new User();
        userNotFollower.setName("Beltrano");
        userNotFollower.setAge(33);

        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        /* Follower user */
        var userFollower = new User();
        userFollower.setName("Ciclano");
        userFollower.setAge(37);

        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        var follower = new Follower();
        follower.setUser(user);
        follower.setFollowerId(userFollower);

        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should create a post for a user")
    void createPostTest() {
        var request = new CreatePostRequest();
        request.setText("Some text");

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(request)
        .when()
                .post()
        .then()
                .statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    void postForAnInexistentUserTest() {
        var inexistentUserId = 999;

        var request = new CreatePostRequest();
        request.setText("Some text");

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .body(request)
        .when()
                .post()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should return error json is not valid")
    void createPostValidationErrorTest() {
        var request = new CreatePostRequest();

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(request)
        .when()
                .post()
        .then()
                .statusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    void listPostFollowerHeaderNotSendTest() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(is("You forgot the header followerId."));
    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    void listPostUserNotFoundTest() {
        var inexistentUserId = 999;
        var followerId = 99;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .header("followerId", followerId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    void listPostFollowerNotFoundTest() {
        var inexistentFollowerId = 99;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", inexistentFollowerId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(is("Nonexistent follower."));
    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    void listPostNotAFollowerTest() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .body(is("You can't see these posts."));
    }

    @Test
    @DisplayName("should return posts")
    void listPostTest() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("size()", is(1));
    }
}
