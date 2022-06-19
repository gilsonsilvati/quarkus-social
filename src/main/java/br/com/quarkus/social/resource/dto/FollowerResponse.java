package br.com.quarkus.social.resource.dto;

import br.com.quarkus.social.domain.model.Follower;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowerResponse {

    private final Long id;
    private final String name;

    public FollowerResponse(Follower follower) {
        this(follower.getId(), follower.getFollowerId().getName());
    }
}
