package br.com.quarkus.social.resource;

import br.com.quarkus.social.resource.dto.CreateUserRequest;
import br.com.quarkus.social.resource.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("should create an user successfully")
    @Order(1)
    void createUserTest() {
        var request = new CreateUserRequest();
        request.setName("Fulani");
        request.setAge(30);

        var response = given()
                                    .contentType(ContentType.JSON)
                                    .body(request)
                                .when()
                                    .post(apiURL)
                                .then()
                                    .extract().response();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("should return error json is not valid")
    @Order(2)
    void createUserValidationErrorTest() {
        var request = new CreateUserRequest();
        request.setName(null);
        request.setAge(null);

        var response = given()
                                    .contentType(ContentType.JSON)
                                    .body(request)
                                .when()
                                    .post(apiURL)
                                .then()
                                    .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
//        assertEquals("Name is required", errors.get(0).get("message"));
//        assertEquals("Age is required", errors.get(1).get("message"));
    }

    @Test
    @DisplayName("should list all users")
    @Order(3)
    void listAllUsersTest() {

        given()
        .when().get(apiURL)
        .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("size()", Matchers.is(1));
    }
}
