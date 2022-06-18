package br.com.quarkus.social.resource.dto;

import lombok.Data;

@Data
public class FieldError {

    private final String field;
    private final String message;

}
