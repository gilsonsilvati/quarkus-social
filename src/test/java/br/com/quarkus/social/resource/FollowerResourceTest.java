package br.com.quarkus.social.resource;

import br.com.quarkus.social.domain.model.Follower;
import br.com.quarkus.social.domain.model.User;
import br.com.quarkus.social.domain.repository.FollowerRepository;
import br.com.quarkus.social.domain.repository.UserRepository;
import br.com.quarkus.social.resource.dto.FollowerRequest;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository repository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        /* Default test user */
        var user = new User();
        user.setName("Gilson Show");
        user.setAge(37);

        userRepository.persist(user);
        userId = user.getId();

        /* Follower User */
        var followerUser = new User();
        followerUser.setName("Fulano");
        followerUser.setAge(37);

        userRepository.persist(followerUser);
        followerId = followerUser.getId();

        /* Follower */
        var follower = new Follower();
        follower.setUser(user);
        follower.setFollowerId(followerUser);

        repository.persist(follower);
    }

    // TODO: Implements test for validations...

    @Test
    @DisplayName("should return 409 when follower id is equals to user id")
    void sameUserAsFollowerTest() {
        var request = new FollowerRequest();
        request.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(request)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(is("You can't follow yourself."));
    }

    @Test
    @DisplayName("should return 404 on follow a user when User id doesn't exist")
    void userNotFoundWhenTryingToFollowTest() {
        var inexistentUserId = 999;

        var request = new FollowerRequest();
        request.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .body(request)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should follow a user")
    void followUserTest() {
        var request = new FollowerRequest();
        request.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(request)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 on list user followers and User id doesn't exist")
    void userNotFoundWhenListingFollowersTest() {
        var inexistentUserId = 999;

        given()
                .pathParam("userId", inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should list a user's follower's")
    void listFollowersTest() {
        var response = given()
                                    .pathParam("userId", userId)
                            .when()
                                    .get()
                            .then()
                                    .extract().response();

        var count = response.jsonPath().get("followersCount");
        var content = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, count);
        assertEquals(1, content.size());
    }

    @Test
    @DisplayName("should return 404 on unfollow user and User id doesn't exist")
    void userNotFoundWhenUnfollowingAUserTest() {
        var inexistentUserId = 999;

        given()
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", followerId)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should unfollow an user")
    void unfollowUserTest() {
        given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}
