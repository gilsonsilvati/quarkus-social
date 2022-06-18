package br.com.quarkus.social.resource.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Text is required")
    @Length(max = 150, message = "Text cannot exceed 150 characters")
    private String text;

}
