package br.com.quarkus.social.resource;

import br.com.quarkus.social.resource.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.net.URI;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class UserResourceTest {

    public static final String BASE_URL = "/users";

    @Test
    @DisplayName("should create an user successfully")
    public void createUserTest() {
        var request = new CreateUserRequest();
        request.setName("Fulani");
        request.setAge(30);

        var response = given()
                                    .contentType(ContentType.JSON)
                                    .body(request)
                                .when()
                                    .post(URI.create(BASE_URL))
                                .then()
                                    .extract().response();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }
}
