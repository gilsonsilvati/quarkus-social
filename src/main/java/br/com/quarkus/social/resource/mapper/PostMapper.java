package br.com.quarkus.social.resource.mapper;

import br.com.quarkus.social.domain.model.Post;
import br.com.quarkus.social.resource.dto.PostResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PostMapper {

    List<PostResponse> toResources(final List<Post> posts);
}
