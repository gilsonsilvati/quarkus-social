package br.com.quarkus.social.resource.mapper;

import br.com.quarkus.social.domain.model.User;
import br.com.quarkus.social.resource.dto.CreateUserRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserMapper {

    User toResource(final CreateUserRequest createUserRequest);
}
