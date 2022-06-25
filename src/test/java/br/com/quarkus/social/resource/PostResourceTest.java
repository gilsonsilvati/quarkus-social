package br.com.quarkus.social.resource;

import br.com.quarkus.social.domain.model.User;
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

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    Long userId;

    @BeforeEach
    @Transactional
    void setUp() {
        var user = new User();
        user.setName("Fulani");
        user.setAge(30);

        userRepository.persist(user);

        userId = user.getId();
    }

    @Test
    @DisplayName("should create a post for a user")
    void createPostTest() {
        System.out.println("userId: " + userId);

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
        System.out.println("userId: " + userId);

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
}
