package br.com.quarkus.social.resource.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PostResponse {

    private String text;
    private ZonedDateTime date;
}
