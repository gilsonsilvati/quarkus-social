package br.com.quarkus.social.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends EntityBase implements Serializable {

    private static final long serialVersionUID = -7327691531462841242L;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;
}
