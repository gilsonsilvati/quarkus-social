package br.com.quarkus.social.resource.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FollowerRequest {

    @NotNull(message = "Follower Id is required")
    private Long followerId;
}
